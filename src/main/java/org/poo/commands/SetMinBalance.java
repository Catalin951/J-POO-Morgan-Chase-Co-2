package org.poo.commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.execution.Execute;
import org.poo.fileio.CommandInput;
import org.poo.mapper.Mappers;
import org.poo.userDetails.account.Account;

public final class SetMinBalance implements Command {
    private final CommandInput input;
    private final ArrayNode output;
    private final Mappers mappers;

    public SetMinBalance(final CommandInput input, final ArrayNode output, final Mappers mappers) {
        this.input = input;
        this.output = output;
        this.mappers = mappers;
    }

    public void execute() {
        Account requestedAccount = mappers.getAccountForIban(input.getAccount());
        if (requestedAccount == null) {
            output.add(Execute.makeGeneralError("setMinBalance",
                    "Account not found",
                    input.getTimestamp()));
            return;
        }
        requestedAccount.setMinBalance(input.getAmount());
    }
}
