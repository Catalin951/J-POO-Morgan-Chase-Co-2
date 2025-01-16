package org.poo.commands;

import org.poo.fileio.CommandInput;
import org.poo.mapper.Mappers;
import org.poo.userDetails.User;
import org.poo.userDetails.account.Account;

public final class SetAlias implements Command {
    private final CommandInput input;
    private final Mappers mappers;
    public SetAlias(final CommandInput input, final Mappers mappers) {
        this.input = input;
        this.mappers = mappers;
    }
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
