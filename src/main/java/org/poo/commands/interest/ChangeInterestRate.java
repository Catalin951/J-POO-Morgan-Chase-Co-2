package org.poo.commands.interest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.commands.Command;
import org.poo.execution.Execute;
import org.poo.execution.ExecutionCommand;
import org.poo.mapper.Mappers;
import org.poo.userDetails.account.Account;

public final class ChangeInterestRate implements Command {
    private final Mappers mapper;
    private final ExecutionCommand input;
    private final ArrayNode output;

    public ChangeInterestRate(final ExecutionCommand input, final Mappers mappers,
                              final ArrayNode output) {
        this.mapper = mappers;
        this.input = input;
        this.output = output;
    }
    public void execute() {
        Account requestedAccount = mapper.getAccountForIban(input.getAccount());

        if (requestedAccount == null) {
            output.add(Execute.makeGeneralError("changeInterestRate",
                    "Account not found",
                    input.getTimestamp()));
            return;
        }
        String type = requestedAccount.getAccountType();
        if (type.equals("classic")) {
            output.add(Execute.makeGeneralError("changeInterestRate",
                    "This is not a savings account",
                    input.getTimestamp()));
            return;
        }
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("description",
                "Interest rate of the account changed to " + input.getInterestRate());
        objectNode.put("timestamp", input.getTimestamp());
        requestedAccount.getTransactions().add(objectNode);
        mapper.getUserForAccount(requestedAccount).getTransactions().add(objectNode);
        requestedAccount.changeInterest(input.getInterestRate());
    }
}
