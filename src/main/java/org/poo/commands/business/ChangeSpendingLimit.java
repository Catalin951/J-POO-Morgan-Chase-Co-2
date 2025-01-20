package org.poo.commands.business;

import org.poo.execution.ExecutionCommand;
import org.poo.mapper.Mappers;
import org.poo.userDetails.User;
import org.poo.userDetails.account.Account;
import org.poo.userDetails.account.BusinessEntity;

public final class ChangeSpendingLimit {
    private final ExecutionCommand input;
    private final Mappers mappers;
    public ChangeSpendingLimit(final ExecutionCommand input, final Mappers mappers) {
        this.input = input;
        this.mappers = mappers;
    }
    public void execute() {
        User user = mappers.getUserForEmail(input.getEmail());
        if (!mappers.hasUserToBusinessEntity(user)) {
            throw new IllegalArgumentException("User " + user.getEmail()
                    + " does not have a business entity");
        }
        BusinessEntity businessEntity = mappers.getBusinessEntityForUser(user);
        Account account = mappers.getAccountForIban(input.getAccount());
        if (account == null) {
            throw new IllegalArgumentException("Account does not exist");
        }
        if (!businessEntity.getAccount().equals(account)) {
            throw new IllegalArgumentException("email and account do not match");
        }
        for (BusinessEntity iterBusinessEntity : account.getBusinessEntities()) {
            iterBusinessEntity.setSpendingLimit(input.getAmount());
        }
    }
}
