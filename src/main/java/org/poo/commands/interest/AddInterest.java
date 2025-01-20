package org.poo.commands.interest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.execution.ExecutionCommand;
import org.poo.execution.SingletonExecute;
import org.poo.mapper.Mappers;
import org.poo.userDetails.User;
import org.poo.userDetails.account.Account;

public final class AddInterest {
    private final ExecutionCommand input;
    private final Mappers mappers;
    private final ArrayNode output;
    public AddInterest(final ExecutionCommand input, final ArrayNode output,
                       final Mappers mappers) {
        this.input = input;
        this.mappers = mappers;
        this.output = output;
    }
    public void execute() {
        Account account = mappers.getAccountForIban(input.getAccount());
        if (account == null) {
            output.add(SingletonExecute.makeGeneralError("addInterest",
                    "Account not found",
                    input.getTimestamp()));
            return;
        }
        if (!account.getAccountType().equals("savings")) {
            output.add(SingletonExecute.makeGeneralError("addInterest",
                    "This is not a savings account",
                    input.getTimestamp()));
            return;
        }
        double newBalance = account.getBalance() + account.getInterest() * account.getBalance();
        User user = mappers.getUserForAccount(account);
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("amount", account.getInterest() * account.getBalance());
        objectNode.put("currency", account.getCurrency());
        objectNode.put("description", "Interest rate income");
        objectNode.put("timestamp", input.getTimestamp());
        user.getTransactions().add(objectNode);
        account.setBalance(newBalance);
    }
}
