package org.poo.commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.execution.ExecutionCommand;
import org.poo.execution.SingletonExecute;
import org.poo.mapper.Mappers;
import org.poo.userDetails.account.Account;

public final class SetMinBalance implements Command {
    private final ExecutionCommand input;
    private final ArrayNode output;
    private final Mappers mappers;

    public SetMinBalance(final ExecutionCommand input,
                         final ArrayNode output,
                         final Mappers mappers) {
        this.input = input;
        this.output = output;
        this.mappers = mappers;
    }

    /**
     * Sets a minimum balance for the account
     */
    public void execute() {
        Account requestedAccount = mappers.getAccountForIban(input.getAccount());
        if (requestedAccount == null) {
            output.add(SingletonExecute.makeGeneralError("setMinBalance",
                    "Account not found",
                    input.getTimestamp()));
            return;
        }
        requestedAccount.setMinBalance(input.getAmount());
    }
}
