package org.poo.userDetails.card;

import org.poo.mapper.Mappers;
import org.poo.userDetails.account.Account;

public final class ClassicCard extends Card {
    public ClassicCard() {
    }
    public ClassicCard(final String cardNumber) {
        super(cardNumber);
    }
    public void subtractFromBalance(final double amount, final Account account,
                                    final Mappers mappers, final int timestamp) {
        account.subtractFromBalance(amount);
    }
}
