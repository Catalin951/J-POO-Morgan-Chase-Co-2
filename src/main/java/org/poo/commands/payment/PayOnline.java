package org.poo.commands.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.Constants;
import org.poo.commands.Command;
import org.poo.commerciant.Commerciant;
import org.poo.execution.ExecutionCommand;
import org.poo.execution.SingletonExecute;
import org.poo.graph.ExchangeGraph;
import org.poo.mapper.Mappers;
import org.poo.userDetails.User;
import org.poo.userDetails.account.Account;
import org.poo.userDetails.account.BusinessEntity;
import org.poo.userDetails.card.Card;

public final class PayOnline implements Command {
    private final ExecutionCommand input;
    private final ArrayNode output;
    private final ExchangeGraph exchangeGraph;
    private final Mappers mappers;
    public PayOnline(final ExecutionCommand input, final ExchangeGraph exchangeGraph,
                     final ArrayNode output, final Mappers mappers) {
        this.input = input;
        this.exchangeGraph = exchangeGraph;
        this.output = output;
        this.mappers = mappers;
    }

    /**
     * This command returns an error if the given used isn't found
     * Outputs an error if the requested card isn't found
     * Uses the exchangeGraph to find a path between the currencies and checks
     * if the account has enough money
     * The transactions are placed in the account and in the user
     */
    public void execute() {
        if (input.getAmount() == 0) {
            return;
        }
        User requestedUser = mappers.getUserForEmail(input.getEmail());
        if (requestedUser == null) {
            throw new IllegalArgumentException("User " + input.getEmail() + " not found");
        }

        Card requestedCard = null;
        Account requestedAccount = null;
        for (Account account : requestedUser.getAccounts()) {
            for (Card card : account.getCards()) {
                if (card.getCardNumber().equals(input.getCardNumber())) {
                    requestedCard = card;
                    requestedAccount = account;
                    break;
                }
            }
        }
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        if (requestedCard == null) {
            objectNode.put("command", "payOnline");
            ObjectNode outputNode = new ObjectMapper().createObjectNode();
            outputNode.put("timestamp", input.getTimestamp());
            outputNode.put("description", "Card not found");
            objectNode.set("output", outputNode);
            objectNode.put("timestamp", input.getTimestamp());
            output.add(objectNode);
            return;
        }
        if (requestedCard.isFrozen()) {
            SingletonExecute.addTransactionError("The card is frozen",
                    input.getTimestamp(), requestedUser);
            return;
        }
        String from = input.getCurrency();
        String to = requestedAccount.getCurrency();
        double convertedAmount = exchangeGraph.convertCurrency(from, to, input.getAmount());
        double commission = requestedUser.getCommissionForTransaction(input.getAmount(),
                                                                    from,
                                                                    exchangeGraph);
        if (requestedAccount.getBalance() - convertedAmount * (1 + commission) < 0) {
            SingletonExecute.addTransactionError("Insufficient funds",
                    input.getTimestamp(), requestedUser);
        } else {
            if (mappers.hasUserToBusinessEntity(requestedUser)) {
                BusinessEntity businessEntity = mappers.getBusinessEntityForUser(requestedUser);
                if (businessEntity.getAccount().equals(requestedAccount)) {
                    if (!businessEntity.canPayOnline(convertedAmount * (1 + commission))) {
                        return;
                    }
                }
            }
            objectNode.put("timestamp", input.getTimestamp());
            objectNode.put("description", "Card payment");
            objectNode.put("amount", convertedAmount);
            objectNode.put("commerciant", input.getCommerciant());
            requestedUser.getTransactions().add(objectNode);
            requestedAccount.getTransactions().add(objectNode);
            requestedCard.subtractFromBalance(convertedAmount * (1 + commission), requestedAccount,
                                              mappers, input.getTimestamp());
            Commerciant commerciant = mappers.getCommerciantForName(input.getCommerciant());
            // Give cashback
            commerciant.giveCashback(requestedAccount.getCurrency(), convertedAmount,
                                     requestedUser, requestedAccount, exchangeGraph);
            if (exchangeGraph.convertToRon(input.getCurrency(),
                input.getAmount()) >= Constants.RON_300
                && requestedUser.getServicePlan().equals("silver")) {
                requestedUser.setNrOf300RonPayments(requestedUser.getNrOf300RonPayments() + 1);
            }
        }
    }
}
