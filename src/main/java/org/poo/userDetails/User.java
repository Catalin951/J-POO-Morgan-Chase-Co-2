package org.poo.userDetails;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Data;
import org.poo.Constants;
import org.poo.fileio.UserInput;
import org.poo.graph.ExchangeGraph;
import org.poo.userDetails.account.Account;
import java.util.ArrayList;
import java.util.HashMap;

import static org.poo.Constants.currentYear;

/**
 * This class is where all the information about an user is held
 * It contains an alias map that maps an alias to an account
 */
@Data
public final class User {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String birthDate;
    private final int age;
    private String servicePlan;
    private final String occupation;
    private final ArrayList<Account> accounts;
    private final ArrayNode transactions;
    private final HashMap<String, Account> aliasMap;

    public User(final UserInput userInput) {
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
        }
        else {
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

    private int getAge(int birthYear, int birthMonth, int birthDay) {
        int tempAge = 0;
        // Handle all cases of birthdate being bigger than current date
        if (Constants.currentYear < birthYear
            || Constants.currentYear == birthYear && Constants.currentMonth < birthMonth
            || Constants.currentYear == birthYear && Constants.currentMonth == birthMonth
                && Constants.currentDay < birthDay) {
            throw new IllegalArgumentException("birthdate " + birthDate + " is greater than " + currentYear);
        }
        // If current year is birth year, age is 0
        // Else check for all other combinations
        if (Constants.currentYear > birthYear) {
            tempAge = Constants.currentYear - birthYear;
            if (Constants.currentMonth < birthMonth) {
                tempAge--;
            }
            else if (Constants.currentMonth == birthMonth && Constants.currentDay < birthDay) {
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
     * @param amount The amount of RON the user is spending. It must be converted before using the method.
     * @return the commission as a percentage (0 % is no commission, it can also be 0.1% or 0.2 %)
     */
    private double getCommission(double amount) {
        return switch (servicePlan) {
            case "standard" -> (0.2 / 100);
            case "silver" -> {
                if (amount >= 500) {
                    yield (0.1 / 100);
                }
                yield 0.0;
            }
            case "student", "gold" -> 0.0;
            default -> throw new IllegalArgumentException("Service plan " + servicePlan + " is not supported");
        };
    }

    /**
     * This function returns the commission taken on this particular transaction.
     * @param amount the money to be spent on the transaction.
     * @param from the currency of the amount
     * @param exchangeGraph the graph that makes it possible to convert the currency
     * @return the commission
     */
    public double getCommissionForTransaction(final double amount, final String from, ExchangeGraph exchangeGraph) {
        double convertedAmount = exchangeGraph.convertCurrency(from, "RON", amount);
        return this.getCommission(convertedAmount);
    }
}
