package org.poo.userDetails.account;

import java.util.ArrayList;

public final class BusinessAccount extends Account {
    public BusinessAccount(final String currency, final String iban) {
        super(currency, iban);
        businessEntities = new ArrayList<>();
    }
    @Override
    public String getAccountType() {
        return "business";
    }
    @Override
    public void changeInterest(final double interest) {
    }
    @Override
    public double getInterest() {
        return 0;
    }
}
