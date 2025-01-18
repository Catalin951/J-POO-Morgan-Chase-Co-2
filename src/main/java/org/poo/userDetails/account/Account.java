package org.poo.userDetails.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Getter;
import lombok.Setter;
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
    private ArrayNode transactions;
    private double totalSpendingThreshold = 0;
    private ArrayList<String> coupons = new ArrayList<>();
    // nrOfTransactions is the number of transactions to the nrOfTransactions commerciants
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
    public double getThresholdCashback(User user, double convertedAmount) {
        totalSpendingThreshold += convertedAmount;
        double cashback = 0;
        if (totalSpendingThreshold >= 100 && totalSpendingThreshold < 300) {
            switch (user.getServicePlan()) {
                case "standard", "student" -> cashback = 0.1;
                case "silver" -> cashback = 0.3;
                case "gold" -> cashback = 0.5;
            }
        }
        else if (totalSpendingThreshold >= 300 && totalSpendingThreshold < 500) {
            switch (user.getServicePlan()) {
                case "standard", "student" -> cashback = 0.2;
                case "silver" -> cashback = 0.4;
                case "gold" -> cashback = 0.55;
            }
        }
        else if (totalSpendingThreshold >= 500) {
            switch (user.getServicePlan()) {
                case "standard", "student" -> cashback = 0.25;
                case "silver" -> cashback = 0.5;
                case "gold" -> cashback = 0.7;
            }
        }
        // percentage
        return cashback / 100;
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
    public abstract void changeInterest(double interest);
    public abstract double getInterest();
}
