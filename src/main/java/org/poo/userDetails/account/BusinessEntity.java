package org.poo.userDetails.account;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.Constants;
import org.poo.userDetails.User;
import org.poo.userDetails.card.Card;

import java.util.ArrayList;

@Getter
@Setter
public final class BusinessEntity {
    private final User businessUser;
    private final String role;
    private double moneySpent = 0;
    private double spendingLimit;
    private double depositLimit;
    private double moneyAdded = 0;
    private final ArrayList<Card> cards;
    private ArrayNode transactions;
    private final Account account;
    public BusinessEntity(final User businessUser, final String role, final Account account) {
        this.businessUser = businessUser;
        this.role = role;
        spendingLimit = Constants.INITIAL_LIMIT_SPENDING;
        depositLimit = Constants.INITIAL_LIMIT_DEPOSITING;
        this.account = account;
        cards = new ArrayList<>();
    }

    /**
     * Adds to the money added field inside this entity
     * @param amount the amount added
     */
    public void addFunds(final double amount) {
        switch (role) {
            case "owner", "manager":
                account.addToBalance(amount);
                moneyAdded += amount;
                break;
            case "employee":
                if (depositLimit > amount) {
                    return;
                }
                account.addToBalance(amount);
                moneyAdded += amount;
                break;
            default:
                throw new IllegalArgumentException("Invalid role");
        }
    }

    /**
     * Adds card to the list of created cards by this entity
     * @param card the card to be added
     */
    public void addCard(final Card card) {
        cards.add(card);
    }

    /**
     * Checks if this business entity has created the card
     * @param card the checked card
     * @return true if the card was created by this entity
     */
    public boolean hasCard(final Card card) {
        for (Card iterCard : cards) {
            if (iterCard.getCardNumber().equals(card.getCardNumber())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if there the spendinglimit is big enough for the transaction
     * and adds the money spent to the inner field
     * @param amount the amount added
     * @return true if the payment can be done
     */
    public boolean canPayOnline(final double amount) {
        switch (role) {
            case "owner", "manager":
                return true;
            case "employee":
                if (spendingLimit > amount) {
                    return false;
                } else {
                    moneySpent += amount;
                    return true;
                }
            default:
                throw new IllegalArgumentException("Invalid role");
        }
    }
}
