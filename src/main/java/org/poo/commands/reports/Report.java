package org.poo.commands.reports;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.commands.Command;
import org.poo.execution.ExecutionCommand;
import org.poo.execution.SingletonExecute;
import org.poo.mapper.Mappers;
import org.poo.userDetails.account.Account;

public final class Report implements Command {
    private final Mappers mappers;
    private final ExecutionCommand input;
    private final ArrayNode output;
    private final int startTimestamp;
    private final int endTimestamp;

    public Report(final ExecutionCommand input, final ArrayNode output, final Mappers mappers) {
        this.mappers = mappers;
        this.input = input;
        this.output = output;
        startTimestamp = input.getStartTimestamp();
        endTimestamp = input.getEndTimestamp();
    }

    public void execute() {
        Account account = mappers.getAccountForIban(input.getAccount());
        ObjectMapper mapper = new ObjectMapper();
        if (account == null) {
            output.add(SingletonExecute.makeGeneralError("report",
                    "Account not found",
                    input.getTimestamp()));
            return;
        }
        ArrayNode transactions = account.getTransactions();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("command", "report");

        ObjectNode outputNode = mapper.createObjectNode();
        outputNode.put("IBAN", account.getIban());
        outputNode.put("balance", account.getBalance());
        outputNode.put("currency", account.getCurrency());

        ArrayNode transactionsArrayNode = mapper.createArrayNode();

        if (account.getAccountType().equals("Savings")) {
            this.makeTransactionsSavings(transactionsArrayNode, transactions);
        } else {
            this.makeTransactionsClassic(transactionsArrayNode, transactions);
        }
        outputNode.set("transactions", transactionsArrayNode);

        objectNode.set("output", outputNode);
        objectNode.put("timestamp", input.getTimestamp());
        output.add(objectNode);
    }

    private void makeTransactionsSavings(final ArrayNode transactionsArrayNode,
                                         final ArrayNode transactions) {
        for (JsonNode transaction : transactions) {
            if (!transaction.isObject()) {
                throw new RuntimeException("Transactions must be of type ObjectNode");
            }
            int timestamp = transaction.get("timestamp").asInt();
            if (timestamp >= startTimestamp && timestamp <= endTimestamp) {
                String commandName = transaction.get("command").asText();
                if (commandName.equals("addInterest")
                        || commandName.equals("changeInterestRate")) {
                    transactionsArrayNode.add(transaction);
                }
            }
        }
    }

    private void makeTransactionsClassic(final ArrayNode transactionsArrayNode,
                                         final ArrayNode transactions) {
        for (JsonNode transaction : transactions) {
            if (!transaction.isObject()) {
                throw new RuntimeException("Transactions must be of type ObjectNode");
            }
            int timestamp = transaction.get("timestamp").asInt();
            if (timestamp >= startTimestamp && timestamp <= endTimestamp) {
                transactionsArrayNode.add(transaction);
            }
        }
    }

}
