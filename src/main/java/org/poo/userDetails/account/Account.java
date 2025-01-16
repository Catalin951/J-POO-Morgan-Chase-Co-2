package org.poo.userDetails.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Getter;
import lombok.Setter;
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
    public Account(final String currency, final String iban) {
        this.currency = currency;
        this.iban = iban;
        transactions = new ObjectMapper().createArrayNode();
        balance = 0;
        minBalance = -1;
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
