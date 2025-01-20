package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.Constants;
import org.poo.execution.ExecutionCommand;
import org.poo.graph.ExchangeGraph;
import org.poo.mapper.Mappers;
import org.poo.userDetails.User;
import org.poo.userDetails.account.Account;

public final class UpgradePlan {
    private final ExecutionCommand input;
    private final Mappers mappers;
    private final ExchangeGraph exchangeGraph;
    public UpgradePlan(final ExecutionCommand input, final Mappers mappers,
                       final ExchangeGraph exchangeGraph) {
        this.input = input;
        this.mappers = mappers;
        this.exchangeGraph = exchangeGraph;
    }
    public void execute() {
        Account account = mappers.getAccountForIban(input.getAccount());
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }
        User user = mappers.getUserForAccount(account);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        String newPlan = input.getNewPlanType();
        String currentPlan = user.getServicePlan();
        if (user.getServicePlan().equals(newPlan)) {
            ObjectNode objectNode = new ObjectMapper().createObjectNode();
            objectNode.put("timestamP", input.getTimestamp());
            objectNode.put("description", "The user already has the " + newPlan + " plan");
            user.getTransactions().add(objectNode);
            return;
        }
        if ((newPlan.equals("standard") || newPlan.equals("student"))
                || currentPlan.equals("gold")) {
            ObjectNode objectNode = new ObjectMapper().createObjectNode();
            objectNode.put("timestamP", input.getTimestamp());
            objectNode.put("description", "You cannot downgrade your plan");
            user.getTransactions().add(objectNode);
            return;
        }
        double fee = -1;
        if (newPlan.equals("silver")) {
            fee = Constants.RON_100;
        }
        if (newPlan.equals("gold")) {
            if (currentPlan.equals("standard") || currentPlan.equals("student")) {
                fee = Constants.RON_350_FEE;
            } else if (currentPlan.equals("silver")) {
                if (user.getNrOf300RonPayments() >= Constants.MINIMUM_PAYMENTS) {
                    fee = 0;
                } else {
                    fee = Constants.RON_250_FEE;
                }
            }
        }
        if (fee == -1) {
            throw new IllegalArgumentException("Plan not supported");
        }
        double convertedFee = exchangeGraph.convertCurrency("RON", account.getCurrency(), fee);
        if (account.getBalance() < convertedFee) {
            ObjectNode objectNode = new ObjectMapper().createObjectNode();
            objectNode.put("timestamP", input.getTimestamp());
            objectNode.put("description", "Insufficient funds");
            user.getTransactions().add(objectNode);
        } else {
            account.setBalance(account.getBalance() - convertedFee);
            user.setServicePlan(newPlan);
            ObjectNode objectNode = new ObjectMapper().createObjectNode();
            objectNode.put("timestamp", input.getTimestamp());
            objectNode.put("accountIBAN", account.getIban());
            objectNode.put("newPlanType", newPlan);
            objectNode.put("description", "Upgrade plan");
            user.getTransactions().add(objectNode);
        }
    }
}
