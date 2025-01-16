package org.poo.commands.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.commands.Command;
import org.poo.fileio.CommandInput;
import org.poo.graph.ExchangeGraph;
import org.poo.userDetails.User;
import org.poo.userDetails.account.Account;

public final class SendMoney implements Command {
    private final User[] users;
    private final CommandInput input;
    private final ExchangeGraph exchangeGraph;
    public SendMoney(final CommandInput input, final User[] users,
                     final ExchangeGraph exchangeGraph) {
        this.users = users;
        this.input = input;
        this.exchangeGraph = exchangeGraph;
    }

    /**
     * Uses the exchangeGraph to find a path between the currencies and checks
     * if the account has enough money
     * The transactions are placed in the account and in the user object of both
     * the payer and the receiver
     */
    public void execute() {
        User payer = null;
        Account payerAccount = null;
        for (User user : users) {
            for (Account account : user.getAccounts()) {
                if (account.getIban().equals(input.getAccount())) {
                    payer = user;
                    payerAccount = account;
                    break;
                }
            }
        }

        User receiver = null;
        Account receiverAccount = null;
        for (User user : users) {
            Account mappedAccount = user.getAccountFromAlias(input.getAccount());
            if (mappedAccount != null) {
                receiver = user;
                receiverAccount = mappedAccount;
                break;
            }
            for (Account account : user.getAccounts()) {
                if (account.getIban().equals(input.getReceiver())) {
                    receiver = user;
                    receiverAccount = account;
                    break;
                }
            }
        }
        if (payer == null || receiver == null) {
            return;
        }
        String from = payerAccount.getCurrency();
        String to = receiverAccount.getCurrency();
        double convertedAmount = input.getAmount();
        if (!from.equals(to)) {
            convertedAmount = exchangeGraph.convertCurrency(from, to, input.getAmount());
        }
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        if (payerAccount.getBalance() - input.getAmount() < 0) {
            objectNode.put("timestamp", input.getTimestamp());
            objectNode.put("description", "Insufficient funds");
            payer.getTransactions().add(objectNode);
            payerAccount.getTransactions().add(objectNode);
        } else {
            objectNode.put("timestamp", input.getTimestamp());
            objectNode.put("description", input.getDescription());
            objectNode.put("senderIBAN", payerAccount.getIban());
            objectNode.put("receiverIBAN", receiverAccount.getIban());
            objectNode.put("amount", input.getAmount() + " " + from);
            objectNode.put("transferType", "sent");
            payer.getTransactions().add(objectNode);

            ObjectNode receiverNode = objectNode.deepCopy();
            receiverNode.put("transferType", "received");
            receiverNode.put("amount", convertedAmount + " " + to);
            receiver.getTransactions().add(receiverNode);
            receiverAccount.getTransactions().add(receiverNode);

            payerAccount.getTransactions().add(objectNode);
            payerAccount.setBalance(payerAccount.getBalance() - input.getAmount());
            receiverAccount.setBalance(receiverAccount.getBalance() + convertedAmount);
        }
    }
}
