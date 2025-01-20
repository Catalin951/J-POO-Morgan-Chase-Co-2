package org.poo.commands.payment;

import org.poo.commands.Command;
import org.poo.execution.ExecutionCommand;
import org.poo.mapper.Mappers;
import org.poo.userDetails.User;
import org.poo.userDetails.account.Account;

public final class SplitPayment implements Command {
    private final ExecutionCommand input;
    private final Mappers mappers;
    public SplitPayment(final ExecutionCommand input, final Mappers mappers) {
        this.input = input;
        this.mappers = mappers;
    }
    /**
     * This command initiates the splitpayment process by adding to the stack of
     * every user the command so that they later know (in order) which splitpayment
     * they have to choose
     */
    public void execute() {
        // Add to each account the command so they know
        // how much they have to pay and if all have paid
        // also to be able to add to their accounts the transaction
        for (String iban : input.getAccounts()) {
            Account account = mappers.getAccountForIban(iban);
            if (account == null) {
                throw new IllegalArgumentException("Invalid iban: " + iban);
            }
            User user = mappers.getUserForAccount(account);
            if (user == null) {
                throw new IllegalArgumentException("Invalid account: " + iban);
            }
            user.getSplitPaymentQueue().add(input);
            user.getCorrespondingSplitPaymentAccount().add(iban);
        }
    }
}
