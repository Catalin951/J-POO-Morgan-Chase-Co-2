package org.poo.userDetails.account;

public final class SavingsAccount extends Account {
    private double interestRate;
    public SavingsAccount(final String currency, final String iban,
                          final double interestRate) {
        super(currency, iban);
        this.interestRate = interestRate;
    }
    public String getAccountType() {
        return "savings";
    }
    public void changeInterest(final double interest) {
        this.interestRate = interest;
    }
    public double getInterest() {
        return interestRate;
    }
}
