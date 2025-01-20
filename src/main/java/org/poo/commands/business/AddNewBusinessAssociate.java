package org.poo.commands.business;

import org.poo.execution.ExecutionCommand;
import org.poo.mapper.Mappers;
import org.poo.userDetails.User;
import org.poo.userDetails.account.Account;
import org.poo.userDetails.account.BusinessEntity;

public final class AddNewBusinessAssociate {
    private final ExecutionCommand input;
    private final Mappers mappers;
    public AddNewBusinessAssociate(final ExecutionCommand input, final Mappers mappers) {
        this.input = input;
        this.mappers = mappers;
    }
    public void execute() {
        Account account = mappers.getAccountForIban(input.getAccount());
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }
        if (!account.getAccountType().equals("business")) {
            throw new IllegalArgumentException("Account type is not business");
        }
        User user = mappers.getUserForEmail(input.getEmail());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        if (mappers.hasUserToBusinessEntity(user)) {
            throw new IllegalArgumentException("user already exists in business account");
        }
        for (BusinessEntity businessEntity : account.getBusinessEntities()) {
            if (businessEntity.getBusinessUser().equals(user)) {
                throw new IllegalArgumentException("user already exists in business acccount");
            }
        }
        BusinessEntity businessEntity = new BusinessEntity(user, input.getRole(), account);
        mappers.addUserToBusinessEntity(user, businessEntity);
        account.getBusinessEntities().addLast(businessEntity);
    }
}
