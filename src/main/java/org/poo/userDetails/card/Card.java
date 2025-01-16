package org.poo.userDetails.card;

import lombok.Data;
import org.poo.mapper.Mappers;
import org.poo.userDetails.account.Account;


@Data
public abstract class Card {
    protected String cardNumber;
    protected String status;
    protected boolean isFrozen;
    public Card(final String cardNumber) {
        this.cardNumber = cardNumber;
        this.status = "active";
        isFrozen = false;
    }
    public Card() {
    }
    public abstract void subtractFromBalance(double amount, Account ownerAccount,
                                             Mappers mappers, int timestamp);
}
