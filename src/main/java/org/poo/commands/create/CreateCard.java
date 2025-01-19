package org.poo.commands.create;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.commands.Command;
import org.poo.execution.ExecutionCommand;
import org.poo.mapper.Mappers;
import org.poo.userDetails.User;
import org.poo.userDetails.account.Account;
import org.poo.userDetails.card.ClassicCard;
import org.poo.utils.Utils;

public final class CreateCard implements Command {
    private final ExecutionCommand input;
    private final Mappers mappers;
    public CreateCard(final ExecutionCommand input, final Mappers mappers) {
        this.input = input;
        this.mappers = mappers;
    }
    public void execute() {
        User requestedUser = mappers.getUserForEmail(input.getEmail());

        if (requestedUser == null) {
            return;
        }
        Account account = mappers.getAccountForIban(input.getAccount());
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("timestamp", input.getTimestamp());
        if (account == null) {
            objectNode.put("description", "User isn't owner");
            requestedUser.getTransactions().add(objectNode);
            return;
        }
        String cardNumber = Utils.generateCardNumber();

        objectNode.put("description", "New card created");
        objectNode.put("card", cardNumber);
        objectNode.put("cardHolder", requestedUser.getEmail());
        objectNode.put("account", account.getIban());

        requestedUser.getTransactions().add(objectNode);
        account.getTransactions().add(objectNode);

        ClassicCard newCard = new ClassicCard(cardNumber);
        account.getCards().addLast(newCard);
    }
}
