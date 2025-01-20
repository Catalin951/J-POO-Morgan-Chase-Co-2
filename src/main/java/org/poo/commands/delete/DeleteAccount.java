package org.poo.commands.delete;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.commands.Command;
import org.poo.execution.ExecutionCommand;
import org.poo.execution.SingletonExecute;
import org.poo.mapper.Mappers;
import org.poo.userDetails.User;
import org.poo.userDetails.account.Account;
import org.poo.userDetails.account.BusinessEntity;

public final class DeleteAccount implements Command {
    private final ExecutionCommand input;
    private final ArrayNode output;
    private final Mappers mappers;
    public DeleteAccount(final ExecutionCommand input,
                         final ArrayNode output,
                         final Mappers mappers) {
        this.input = input;
        this.output = output;
        this.mappers = mappers;
    }

    public void execute() {
        User requestedUser = mappers.getUserForEmail(input.getEmail());
        Account requestedAccount = mappers.getAccountForIban(input.getAccount());
        if (mappers.hasUserToBusinessEntity(requestedUser)) {
            BusinessEntity businessEntity = mappers.getBusinessEntityForUser(requestedUser);
            if (businessEntity.getAccount().equals(requestedAccount)) {
                if (!businessEntity.getRole().equals("owner")) {
                    return;
                }
            }
        }
        if (requestedAccount == null) {
            output.add(SingletonExecute.makeGeneralError("deleteAccount",
                    "Account not found",
                    input.getTimestamp()));
            return;
        }
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("command", "deleteAccount");
        ObjectNode outputNode = new ObjectMapper().createObjectNode();

        if (requestedAccount.getBalance() != 0) {
            outputNode.put("error",
                    "Account couldn't be deleted - see org.poo.transactions for details");
            outputNode.put("timestamp", input.getTimestamp());
            objectNode.set("output", outputNode);
            objectNode.put("timestamp", input.getTimestamp());
            output.add(objectNode);

            ObjectNode transactionErrorNode = new ObjectMapper().createObjectNode();
            transactionErrorNode.put("description",
                    "Account couldn't be deleted - there are funds remaining");
            transactionErrorNode.put("timestamp", input.getTimestamp());
            requestedAccount.getTransactions().add(transactionErrorNode);
            mappers.getUserForAccount(requestedAccount).getTransactions().add(transactionErrorNode);
            return;
        }
        requestedUser.getAccounts().remove(requestedAccount);

        outputNode.put("success", "Account deleted");
        outputNode.put("timestamp", input.getTimestamp());
        objectNode.put("timestamp", input.getTimestamp());
        objectNode.set("output", outputNode);
        output.add(objectNode);
    }
}
