package org.poo.userDetails;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.Constants;
import org.poo.execution.ExecutionCommand;
import org.poo.fileio.UserInput;
import org.poo.graph.ExchangeGraph;
import org.poo.userDetails.account.Account;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import static org.poo.Constants.CURRENT_YEAR;

/**
 * This class is where all the information about an user is held
 * It contains an alias map that maps an alias to an account
 */
@Getter
@Setter
public final class User {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String birthDate;
    private final int age;
    private String servicePlan;
    private int nrOf300RonPayments = 0;
    private Queue<ExecutionCommand> splitPaymentQueue;
    private Queue<String> correspondingSplitPaymentAccount;
    private final String occupation;
    private final ArrayList<Account> accounts;
    private final ArrayNode transactions;
    private final HashMap<String, Account> aliasMap;

    public User(final UserInput userInput) {
        splitPaymentQueue = new LinkedList<>();
        correspondingSplitPaymentAccount = new LinkedList<>();
        firstName = userInput.getFirstName();
        lastName = userInput.getLastName();
        email = userInput.getEmail();
        birthDate = userInput.getBirthDate();
        occupation = userInput.getOccupation();
        accounts = new ArrayList<>();
        transactions = new ObjectMapper().createArrayNode();
        aliasMap = new HashMap<>();
        if (occupation.equals("student")) {
            servicePlan = "student";
        } else {
            servicePlan = "standard";
        }
        String[] dateSegments = birthDate.split("-");
        int birthYear, birthMonth, birthDay;
        try {
            birthYear = Integer.parseInt(dateSegments[0]);
            birthMonth = Integer.parseInt(dateSegments[1]);
            birthDay = Integer.parseInt(dateSegments[2]);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid birthdate format");
        }
        age = getAge(birthYear, birthMonth, birthDay);
    }

    private int getAge(final int birthYear, final int birthMonth, final int birthDay) {
        int tempAge = 0;
        // Handle all cases of birthdate being bigger than current date
        if (Constants.CURRENT_YEAR < birthYear
            || Constants.CURRENT_YEAR == birthYear && Constants.CURRENT_MONTH < birthMonth
            || Constants.CURRENT_YEAR == birthYear && Constants.CURRENT_MONTH == birthMonth
                && Constants.CURRENT_DAY < birthDay) {
            throw new IllegalArgumentException("birthdate " + birthDate
                                                + " is greater than " + CURRENT_YEAR);
        }
        // If current year is birth year, age is 0
        // Else check for all other combinations
        if (Constants.CURRENT_YEAR > birthYear) {
            tempAge = Constants.CURRENT_YEAR - birthYear;
            if (Constants.CURRENT_MONTH < birthMonth) {
                tempAge--;
            } else if (Constants.CURRENT_MONTH == birthMonth && Constants.CURRENT_DAY < birthDay) {
                tempAge--;
            }
        }
        return tempAge;
    }

    /**
     * Maps the alias to an account
     * @param alias Key
     * @param account Value
     */
    public void setAlias(final String alias, final Account account) {
        aliasMap.put(alias, account);
    }

    /**
     * Returns the value to which the alias is mapped
     * @param alias key
     * @return Value
     */
    public Account getAccountFromAlias(final String alias) {
        return aliasMap.get(alias);
    }

    /**
     * Depending on what type of servicePlan the user currently has, returns the commission taken on
     * the transaction to be made. Only in the silver plan the amount matters.
     * @param amount The amount of RON the user is spending.
     * It must be converted before using the method.
     * @return the commission as a percentage (0 % is no commission, it can also be 0.1% or 0.2 %)
     */
    private double getCommission(final double amount) {
        return switch (servicePlan) {
            case "standard" -> (Constants.SERVICE_PLAN_STANDARD_COMMISSION / Constants.PERCENT);
            case "silver" -> {
                if (amount >= Constants.RON_500) {
                    yield (Constants.SERVICE_PLAN_SILVER_COMMISSION / Constants.PERCENT);
                }
                yield 0.0;
            }
            case "student", "gold" -> 0.0;
            default -> throw new IllegalArgumentException("Service plan "
                                                        + servicePlan
                                                        + " is not supported");
        };
    }

    /**
     * This function returns the commission taken on this particular transaction.
     * @param amount the money to be spent on the transaction.
     * @param from the currency of the amount
     * @param exchangeGraph the graph that makes it possible to convert the currency
     * @return the commission
     */
    public double getCommissionForTransaction(final double amount,
                                              final String from,
                                              final ExchangeGraph exchangeGraph) {
        double convertedAmount = exchangeGraph.convertCurrency(from, "RON", amount);
        return this.getCommission(convertedAmount);
    }
}
