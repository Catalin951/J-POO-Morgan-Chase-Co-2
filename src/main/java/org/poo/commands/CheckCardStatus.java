package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.execution.ExecutionCommand;
import org.poo.userDetails.User;
import org.poo.userDetails.account.Account;
import org.poo.userDetails.card.Card;

public final class CheckCardStatus implements Command {
    private final User[] users;
    private final ExecutionCommand input;
    private final ArrayNode output;

    public CheckCardStatus(final ExecutionCommand input, final User[] users,
                           final ArrayNode output) {
        this.users = users;
        this.input = input;
        this.output = output;
    }

    public void execute() {
        Account requestedAccount = null;
        User requestedUser = null;
        Card requestedCard = null;
        for (User user : users) {
            for (Account account : user.getAccounts()) {
                for (Card card : account.getCards()) {
                    if (card.getCardNumber().equals(input.getCardNumber())) {
                        requestedAccount = account;
                        requestedUser = user;
                        requestedCard = card;
                        break;
                    }
                }
            }
        }
        ObjectMapper mapper = new ObjectMapper();
        if (requestedCard == null) {
            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("command", "checkCardStatus");
            ObjectNode errorNode = mapper.createObjectNode();
            errorNode.put("description", "Card not found");
            errorNode.put("timestamp", input.getTimestamp());
            outputNode.set("output", errorNode);
            outputNode.put("timestamp", input.getTimestamp());
            output.add(outputNode);
            return;
        }
        if (requestedAccount.getBalance() <= 0) {
            requestedCard.setFrozen(true);
            requestedCard.setStatus("frozen");
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("description",
                    "You have reached the minimum amount of funds, the card will be frozen");
            objectNode.put("timestamp", input.getTimestamp());
            requestedUser.getTransactions().add(objectNode);
        }
    }
}
