package org.poo.commerciant;

import org.poo.fileio.CommerciantInput;
import org.poo.graph.ExchangeGraph;
import org.poo.userDetails.User;
import org.poo.userDetails.account.Account;

public final class SpendingThreshold extends Commerciant {
    public SpendingThreshold(final CommerciantInput commerciant) {
        super(commerciant);
    }
    @Override
    public void giveCashback(final String currencyType, final double amountPaid,
                             final User payer, final Account payerAccount,
                             final ExchangeGraph exchangeGraph) {
        double couponCashback = this.getCouponCashbackAndRemove(payerAccount);
        double ronAmount = exchangeGraph.convertToRon(currencyType, amountPaid);
        double thresholdCashback = payerAccount.getThresholdCashback(payer, ronAmount);
        payerAccount.setBalance(payerAccount.getBalance()
                                + amountPaid * (thresholdCashback + couponCashback));
    }
}
