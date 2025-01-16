package org.poo.commands.delete;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.commands.Command;
import org.poo.fileio.CommandInput;
import org.poo.userDetails.User;
import org.poo.userDetails.account.Account;
import org.poo.userDetails.card.Card;

public final class DeleteCard implements Command {
    private final User[] users;
    private final CommandInput input;
    public DeleteCard(final CommandInput input, final User[] users) {
        this.users = users;
        this.input = input;
    }

    /**
     * Goes through the cards to find the card to be destroyed
     * and removes it from the account
     */
    public void execute() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        for (User user : users) {
            for (Account account : user.getAccounts()) {
                int i = 0;
                for (Card card : account.getCards()) {
                    if (card.getCardNumber().equals(input.getCardNumber())) {
                        objectNode.put("timestamp", input.getTimestamp());
                        objectNode.put("description", "The card has been destroyed");
                        objectNode.put("card", card.getCardNumber());
                        objectNode.put("cardHolder", user.getEmail());
                        objectNode.put("account", account.getIban());
                        account.getCards().remove(i);
                        user.getTransactions().add(objectNode);
                        return;
                    }
                    i++;
                }
            }
        }
    }
}
