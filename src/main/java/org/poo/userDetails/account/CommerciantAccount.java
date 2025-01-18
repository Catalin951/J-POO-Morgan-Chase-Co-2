package org.poo.userDetails.account;

public final class CommerciantAccount extends Account {
    public CommerciantAccount(final String currency, final String iban) {
        super(currency, iban);
    }
    public String getAccountType() {
        return "commerciant";
    }
    public void changeInterest(final double interest) {
    }
    public double getInterest() {
        return 0;
    }
}
