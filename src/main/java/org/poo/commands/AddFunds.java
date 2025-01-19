package org.poo.commands;

import org.poo.execution.ExecutionCommand;
import org.poo.mapper.Mappers;
import org.poo.userDetails.account.Account;

public final class AddFunds {
    private final ExecutionCommand input;
    private final Mappers mappers;
    public AddFunds(final ExecutionCommand input, final Mappers mappers) {
        this.input = input;
        this.mappers = mappers;
    }
    public void execute() {
        Account account = mappers.getAccountForIban(input.getAccount());

        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }

        account.addToBalance(input.getAmount());
    }
}
