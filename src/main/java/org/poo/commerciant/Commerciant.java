package org.poo.commerciant;

import org.poo.factories.AccountFactory;
import org.poo.fileio.CommerciantInput;
import lombok.Data;
import org.poo.graph.ExchangeGraph;
import org.poo.userDetails.User;
import org.poo.userDetails.account.Account;

@Data
public class Commerciant {
    private final String commerciant;
    private final int id;
    private final String iban;
    private final Account account;
    private final String type;
    private final String cashbackStrategy;
    public Commerciant(final CommerciantInput commerciant) {
        this.commerciant = commerciant.getCommerciant();
        this.id = commerciant.getId();
        this.iban = commerciant.getAccount();
        this.type = commerciant.getType();
        this.cashbackStrategy = commerciant.getCashbackStrategy();
        account = AccountFactory.createCommerciantAccount(iban);
    }
    public void giveCashback(final String currencyType, final double amountPaid,
                             final User payer, final Account payerAccount,
                             final ExchangeGraph exchangeGraph) {

    }
    protected double getCouponCashbackAndRemove(Account payerAccount) {
        int i = 0;
        for (String string : payerAccount.getCoupons()) {
            if (string.equals(this.getType())) {
                payerAccount.getCoupons().remove(i);
                return switch (this.getType()) {
                    case "Food" -> 0.2 / 100;
                    case "Clothes" -> 0.5 / 100;
                    case "Tech" -> 1.0 / 100;
                    default -> throw new IllegalStateException("Unexpected value: " + this.getType());
                };
            }
        }
        return 0;
    }

}
