package org.poo.commands.payment;

import org.poo.execution.ExecutionCommand;
import org.poo.mapper.Mappers;
import org.poo.userDetails.User;

public final class RejectSplitPayment {
    private final ExecutionCommand input;
    private final Mappers mappers;
    public RejectSplitPayment(final ExecutionCommand input, final Mappers mappers) {
        this.input = input;
        this.mappers = mappers;
    }

    /**
     * The rejectSplitPayment command simply retrieves the
     * first element in the queue of split commands
     */
    public void execute() {
        User currentSplitUser = mappers.getUserForEmail(input.getEmail());
        if (currentSplitUser == null) {
            return;
        }
        currentSplitUser.getSplitPaymentQueue().poll();
    }
}
