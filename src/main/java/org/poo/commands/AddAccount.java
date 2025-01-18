package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.factories.AccountFactory;
import org.poo.fileio.CommandInput;
import org.poo.mapper.Mappers;
import org.poo.userDetails.User;
import org.poo.userDetails.account.Account;

public final class AddAccount implements Command {
    private final CommandInput input;
    private final Mappers mappers;

    public AddAccount(final CommandInput input, final Mappers mappers) {
        this.input = input;
        this.mappers = mappers;
    }

    public void execute() {
        User requestedUser = mappers.getUserForEmail(input.getEmail());

        if (requestedUser == null) {
            throw new IllegalArgumentException("User not found");
        }
        System.out.println(requestedUser.getAge() );

        Account newAccount = AccountFactory.createAccount(input);
        mappers.addAccountToUser(newAccount, requestedUser);
        mappers.addIbanToAccount(newAccount.getIban(), newAccount);
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("timestamp", input.getTimestamp());
        objectNode.put("description", "New account created");

        newAccount.getTransactions().add(objectNode);
        requestedUser.getTransactions().add(objectNode);
        requestedUser.getAccounts().addLast(newAccount);
    }
}
