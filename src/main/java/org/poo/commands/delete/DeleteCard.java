package org.poo.commands.delete;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.commands.Command;
import org.poo.execution.ExecutionCommand;
import org.poo.mapper.Mappers;
import org.poo.userDetails.User;
import org.poo.userDetails.account.Account;
import org.poo.userDetails.account.BusinessEntity;
import org.poo.userDetails.card.Card;

public final class DeleteCard implements Command {
    private final User[] users;
    private final ExecutionCommand input;
    private final Mappers mappers;
    public DeleteCard(final ExecutionCommand input, final User[] users, final Mappers mappers) {
        this.users = users;
        this.input = input;
        this.mappers = mappers;
    }

    /**
     * Goes through the cards to find the card to be destroyed
     * and removes it from the account
     */
    public void execute() {
        try {
            for (User user : users) {
                for (Account account : user.getAccounts()) {
                    int i = 0;
                    for (Card card : account.getCards()) {
                        if (card.getCardNumber().equals(input.getCardNumber())) {
                            checkBusiness(user, account, card, i);
                        }
                        i++;
                    }
                }
            }
        } catch (RuntimeException e) {
            return;
        }
    }
    private void checkBusiness(final User user, final Account account,
                               final Card card, final int cardIndex) {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        if (mappers.hasUserToBusinessEntity(user)) {
            BusinessEntity businessEntity = mappers.getBusinessEntityForUser(user);
            if (businessEntity.getAccount().equals(account)) {
                System.out.println("account destroying");
                if (businessEntity.getRole().equals("owner")
                    || businessEntity.getRole().equals("manager")
                    || (businessEntity.getRole().equals("employee")
                    && businessEntity.hasCard(card))) {
                    objectNode.put("timestamp", input.getTimestamp());
                    objectNode.put("description", "The card has been destroyed");
                    objectNode.put("card", card.getCardNumber());
                    objectNode.put("cardHolder", user.getEmail());
                    objectNode.put("account", account.getIban());
                    account.getCards().remove(cardIndex);
                    user.getTransactions().add(objectNode);
                }
                return;
            }
        }
        System.out.println("account destroyed");
        objectNode.put("timestamp", input.getTimestamp());
        objectNode.put("description", "The card has been destroyed");
        objectNode.put("card", card.getCardNumber());
        objectNode.put("cardHolder", user.getEmail());
        objectNode.put("account", account.getIban());
        account.getCards().remove(cardIndex);
        user.getTransactions().add(objectNode);
    }
}
