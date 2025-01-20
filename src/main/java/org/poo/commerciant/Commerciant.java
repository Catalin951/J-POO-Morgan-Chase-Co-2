package org.poo.commerciant;

import lombok.Getter;
import lombok.Setter;
import org.poo.Constants;
import org.poo.factories.AccountFactory;
import org.poo.fileio.CommerciantInput;
import org.poo.graph.ExchangeGraph;
import org.poo.userDetails.User;
import org.poo.userDetails.account.Account;

@Getter
@Setter
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

    /**
     * Function to be overridden in subclasses,
     * different implementations depending on the commerciant
     * @param currencyType The currency type of the amount
     * @param amountPaid The amount spent on the transaction
     * @param payer The user that payed
     * @param payerAccount The account of the user
     * @param exchangeGraph Graph used for converting currencies
     */
    public void giveCashback(final String currencyType, final double amountPaid,
                             final User payer, final Account payerAccount,
                             final ExchangeGraph exchangeGraph) {

    }

    /**
     * This method uses the coupons and removes it, giving
     * cashback depending on the type of the coupon
     * @param payerAccount the account on which this is applied
     * @return the cashback of the coupon
     */
    public double getCouponCashbackAndRemove(final Account payerAccount) {
        int i = 0;
        for (String string : payerAccount.getCoupons()) {
            if (string.equals(this.getType())) {
                payerAccount.getCoupons().remove(i);
                return switch (this.getType()) {
                    case "Food" -> Constants.FOOD_COUPON_CASHBACK / Constants.PERCENT;
                    case "Clothes" -> Constants.CLOTHES_COUPON_CASHBACK / Constants.PERCENT;
                    case "Tech" -> Constants.TECH_COUPON_CASHBACK / Constants.PERCENT;
                    default -> throw new IllegalStateException("Unexpected value: "
                                                                + this.getType());
                };
            }
        }
        return 0;
    }
}
