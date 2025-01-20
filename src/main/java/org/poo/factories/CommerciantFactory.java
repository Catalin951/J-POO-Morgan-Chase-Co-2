package org.poo.factories;

import org.poo.commerciant.Commerciant;
import org.poo.commerciant.NrOfTransactions;
import org.poo.commerciant.SpendingThreshold;
import org.poo.fileio.CommerciantInput;

public final class CommerciantFactory {
    private CommerciantFactory() {
    }

    /**
     * Creates a commerciant based on the two types: spendingThreshold and nrOfTransactions
     * @param commerciantInput The command that tells the factory what to create
     * @return the commerciant
     */
    public static Commerciant createCommerciant(final CommerciantInput commerciantInput) {
        return switch (commerciantInput.getCashbackStrategy()) {
            case "spendingThreshold" -> new SpendingThreshold(commerciantInput);
            case "nrOfTransactions" -> new NrOfTransactions(commerciantInput);
            default -> throw new IllegalArgumentException("cashback strategy not supported");
        };
    }
}
