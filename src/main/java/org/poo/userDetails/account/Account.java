package org.poo.userDetails.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.Constants;
import org.poo.userDetails.User;
import org.poo.userDetails.card.Card;

import java.util.ArrayList;

@Getter
@Setter
public abstract class Account {
    private final String currency;
    private final String iban;
    private final ArrayList<Card> cards = new ArrayList<>();
    private double balance;
    private double minBalance;
    private double currentSplitAmount;
    private ArrayNode transactions;
    private double totalSpendingThreshold = 0;
    private ArrayList<String> coupons = new ArrayList<>();
    private final User owner = null;
    protected ArrayList<BusinessEntity> businessEntities = null;
    private final ArrayList<User> managers = null;
    private final ArrayList<User> employees = null;
    private int nrOfTransactions = 0;
    public Account(final String currency, final String iban) {
        this.currency = currency;
        this.iban = iban;
        transactions = new ObjectMapper().createArrayNode();
        balance = 0;
        minBalance = -1;
    }

    /**
     * Returns the cashback afferent to the transaction depending on how much has
     * already been spent on this type of commerciants and the owner's service plan.
     * @param user the owner of the account
     * @param convertedAmount the amount spent in RON
     * @return the cashback to be received as a percentage
     */
    public double getThresholdCashback(final User user, final double convertedAmount) {
        totalSpendingThreshold += convertedAmount;
        double cashback = 0;
        if (totalSpendingThreshold >= Constants.RON_100
                && totalSpendingThreshold < Constants.RON_300) {
            switch (user.getServicePlan()) {
                case "standard", "student" -> cashback = Constants.RON_100_STANDARD_CASHBACK;
                case "silver" -> cashback = Constants.RON_100_SILVER_CASHBACK;
                case "gold" -> cashback = Constants.RON_100_GOLD_CASHBACK;
                default -> cashback = 0;
            }
        } else if (totalSpendingThreshold >= Constants.RON_300
                    && totalSpendingThreshold < Constants.RON_500) {
            switch (user.getServicePlan()) {
                case "standard", "student" -> cashback = Constants.RON_300_STANDARD_CASHBACK;
                case "silver" -> cashback = Constants.RON_300_SILVER_CASHBACK;
                case "gold" -> cashback = Constants.RON_300_GOLD_CASHBACK;
                default -> cashback = 0;
            }
        } else if (totalSpendingThreshold >= Constants.RON_500) {
            switch (user.getServicePlan()) {
                case "standard", "student" -> cashback = Constants.RON_500_STANDARD_CASHBACK;
                case "silver" -> cashback = Constants.RON_500_SILVER_CASHBACK;
                case "gold" -> cashback = Constants.RON_500_GOLD_CASHBACK;
                default -> cashback = 0;
            }
        }
        // percentage
        return cashback / Constants.PERCENT;
    }
    /**
     * Method for adding an amount to the balance of the account
     * @param amount Amount to add
     */
    public void addToBalance(final double amount) {
        balance += amount;
    }
    /**
     * Method for subtracting an amount from the balance of the account
     * @param amount Amount to subtract
     */
    public void subtractFromBalance(final double amount) {
        balance -= amount;
    }

    /**
     * Implemented in the subclasses to get the string that is the type of the account
     * @return The string that is the type of the account
     */
    public abstract String getAccountType();

    /**
     * Function to be overridden in below classes
     * @param interest the interest
     */
    public abstract void changeInterest(double interest);

    /**
     * Function to be overridden in below classes
     * @return the interest of the account
     */
    public abstract double getInterest();
}
