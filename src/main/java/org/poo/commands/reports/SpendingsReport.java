package org.poo.commands.reports;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.commands.Command;
import org.poo.execution.Execute;
import org.poo.fileio.CommandInput;
import org.poo.mapper.Mappers;
import org.poo.userDetails.account.Account;

import java.util.Map;
import java.util.TreeMap;

public final class SpendingsReport implements Command {
    private final Mappers mappers;
    private final CommandInput input;
    private final ArrayNode output;
    private final int startTimestamp;
    private final int endTimestamp;

    public SpendingsReport(final CommandInput input, final ArrayNode output,
                           final Mappers mappers) {
        this.mappers = mappers;
        this.input = input;
        this.output = output;
        startTimestamp = input.getStartTimestamp();
        endTimestamp = input.getEndTimestamp();
    }
    private static final class CommerciantPayments {
        private final TreeMap<String, Double> commerciantPayments;

        CommerciantPayments() {
            this.commerciantPayments = new TreeMap<>();
        }

        public void addPayment(final String commerciant, final Double amount) {
            Double currentAmount = commerciantPayments.getOrDefault(commerciant, 0.0);
            Double updatedAmount = currentAmount + amount;
            commerciantPayments.put(commerciant, updatedAmount);
        }
    }

    public void execute() {
        Account account = mappers.getAccountForIban(input.getAccount());
        ObjectMapper mapper = new ObjectMapper();
        if (account == null) {
            output.add(Execute.makeGeneralError("spendingsReport",
                    "Account not found",
                    input.getTimestamp()));
            return;
        }

        if (account.getAccountType().equals("savings")) {
            ObjectNode spendingsError = mapper.createObjectNode();
            spendingsError.put("command", "spendingsReport");
            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("error", "This kind of report is not supported for a saving account");
            spendingsError.set("output", outputNode);
            spendingsError.put("timestamp", input.getTimestamp());
            output.add(spendingsError);
            return;
        }
        ArrayNode transactions = account.getTransactions();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("command", "spendingsReport");

        ObjectNode outputNode = mapper.createObjectNode();
        outputNode.put("IBAN", account.getIban());
        outputNode.put("balance", account.getBalance());
        outputNode.put("currency", account.getCurrency());

        ArrayNode transactionsArrayNode = mapper.createArrayNode();
        ArrayNode commerciantsArrayNode = mapper.createArrayNode();
        this.makeTransactionsAndCommerciants(transactionsArrayNode, commerciantsArrayNode,
                                             transactions);
        outputNode.set("transactions", transactionsArrayNode);
        outputNode.set("commerciants", commerciantsArrayNode);
        objectNode.set("output", outputNode);
        objectNode.put("timestamp", input.getTimestamp());
        output.add(objectNode);
    }
    private void makeTransactionsAndCommerciants(final ArrayNode transactionsArrayNode,
                                                 final ArrayNode commerciantsArrayNode,
                                                 final ArrayNode transactions) {
        CommerciantPayments commerciantPayments = new CommerciantPayments();
        for (JsonNode transaction : transactions) {
            if (!transaction.isObject()) {
                throw new RuntimeException("Transactions must be of type ObjectNode");
            }
            int timestamp = transaction.get("timestamp").asInt();
            if (!transaction.has("commerciant")) {
                continue;
            }
            if (timestamp >= startTimestamp && timestamp <= endTimestamp) {
                String commerciant = transaction.get("commerciant").asText();
                double amount = transaction.get("amount").asDouble();
                commerciantPayments.addPayment(commerciant, amount);
                transactionsArrayNode.add(transaction);
            }
        }
        for (Map.Entry<String, Double> entry
                : commerciantPayments.commerciantPayments.entrySet()) {
            ObjectNode commerciantNode = new ObjectMapper().createObjectNode();
            commerciantNode.put("commerciant", entry.getKey());
            commerciantNode.put("total", entry.getValue());
            commerciantsArrayNode.add(commerciantNode);
        }
    }
}
