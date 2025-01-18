package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.graph.ExchangeGraph;
import org.poo.mapper.Mappers;
import org.poo.userDetails.User;
import org.poo.userDetails.account.Account;

public final class UpgradePlan {
    private final CommandInput input;
    private final Mappers mappers;
    private final ExchangeGraph exchangeGraph;
    private final ArrayNode output;
    public UpgradePlan(final CommandInput input, final ArrayNode output, final Mappers mappers, ExchangeGraph exchangeGraph) {
        this.input = input;
        this.mappers = mappers;
        this.exchangeGraph = exchangeGraph;
        this.output = output;
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
        // Can you go from standard to student? check error
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
            fee = 100;
        }
        if (newPlan.equals("gold")) {
            if (currentPlan.equals("standard") || currentPlan.equals("student")) {
                fee = 350;
            } else if (currentPlan.equals("silver")) {
                if (user.getNrOf300RonPayments() >= 5) {
                    fee = 0;
                } else {
                    fee = 250;
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
