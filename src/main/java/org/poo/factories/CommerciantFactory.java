package org.poo.factories;

import org.poo.commerciant.Commerciant;
import org.poo.commerciant.NrOfTransactions;
import org.poo.commerciant.SpendingThreshold;
import org.poo.fileio.CommandInput;
import org.poo.fileio.CommerciantInput;
import org.poo.userDetails.account.Account;
import org.poo.userDetails.account.ClassicAccount;
import org.poo.userDetails.account.CommerciantAccount;
import org.poo.userDetails.account.SavingsAccount;
import org.poo.utils.Utils;

public class CommerciantFactory {
    public static Commerciant createCommerciant(final CommerciantInput commerciantInput) {
        return switch (commerciantInput.getCashbackStrategy()) {
            case "spendingThreshold" -> new SpendingThreshold(commerciantInput);
            case "nrOfTransactions" -> new NrOfTransactions(commerciantInput);
            default -> throw new IllegalArgumentException("cashback strategy not supported");
        };
    }
}