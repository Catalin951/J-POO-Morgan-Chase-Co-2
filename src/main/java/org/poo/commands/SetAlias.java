package org.poo.commands;

import org.poo.execution.ExecutionCommand;
import org.poo.mapper.Mappers;
import org.poo.userDetails.User;
import org.poo.userDetails.account.Account;

public final class SetAlias implements Command {
    private final ExecutionCommand input;
    private final Mappers mappers;
    public SetAlias(final ExecutionCommand input, final Mappers mappers) {
        this.input = input;
        this.mappers = mappers;
    }

    /**
     * Sets an alias for an account
     */
    public void execute() {
        User requestedUser = mappers.getUserForEmail(input.getEmail());
        if (requestedUser == null) {
            throw new IllegalArgumentException("User not found in SetAlias");
        }
        for (Account account : requestedUser.getAccounts()) {
            if (account.getIban().equals(input.getAccount())) {
                requestedUser.setAlias(input.getAlias(), account);
                break;
            }
        }
    }
}
