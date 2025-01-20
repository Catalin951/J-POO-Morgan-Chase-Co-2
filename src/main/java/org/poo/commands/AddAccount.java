package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.execution.ExecutionCommand;
import org.poo.factories.AccountFactory;
import org.poo.mapper.Mappers;
import org.poo.userDetails.User;
import org.poo.userDetails.account.Account;
import org.poo.userDetails.account.BusinessEntity;

public final class AddAccount implements Command {
    private final ExecutionCommand input;
    private final Mappers mappers;

    public AddAccount(final ExecutionCommand input, final Mappers mappers) {
        this.input = input;
        this.mappers = mappers;
    }

    public void execute() {
        User requestedUser = mappers.getUserForEmail(input.getEmail());

        if (requestedUser == null) {
            throw new IllegalArgumentException("User not found");
        }

        Account newAccount = AccountFactory.createAccount(input);
        if (newAccount.getAccountType().equals("business")) {
            BusinessEntity businessEntity = new BusinessEntity(requestedUser, "owner", newAccount);
            mappers.addUserToBusinessEntity(requestedUser, businessEntity);
        }
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
