package org.poo.commerciant;

import org.poo.Constants;
import org.poo.fileio.CommerciantInput;
import org.poo.graph.ExchangeGraph;
import org.poo.userDetails.User;
import org.poo.userDetails.account.Account;

public final class NrOfTransactions extends Commerciant {
    public NrOfTransactions(final CommerciantInput commerciant) {
        super(commerciant);
    }
    @Override
    public void giveCashback(final String currencyType, final double amountPaid,
                             final User payer, final Account payerAccount,
                             final ExchangeGraph exchangeGraph) {
        int nrOfTransactions = payerAccount.getNrOfTransactions();
        payerAccount.setNrOfTransactions(nrOfTransactions + 1);
        double couponCashback = this.getCouponCashbackAndRemove(payerAccount);
        if (nrOfTransactions == Constants.FIRST_TRANSACTIONS_CHECKPOINT) {
            payerAccount.getCoupons().addLast("Food");
        } else if (nrOfTransactions == Constants.SECOND_TRANSACTIONS_CHECKPOINT) {
            payerAccount.getCoupons().addLast("Clothes");
        } else if (nrOfTransactions == Constants.THIRD_TRANSACTIONS_CHECKPOINT) {
            payerAccount.getCoupons().addLast("Tech");
        }
        payerAccount.setBalance(payerAccount.getBalance() + amountPaid * couponCashback);
    }
}
