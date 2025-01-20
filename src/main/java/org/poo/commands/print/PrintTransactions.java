package org.poo.commands.print;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.commands.Command;
import org.poo.execution.ExecutionCommand;
import org.poo.mapper.Mappers;
import org.poo.userDetails.User;

import java.util.stream.StreamSupport;

public final class PrintTransactions implements Command {
    private final Mappers mappers;
    private final ExecutionCommand input;
    private final ArrayNode output;
    public PrintTransactions(final ExecutionCommand input, final Mappers mappers,
                             final ArrayNode output) {
        this.mappers = mappers;
        this.input = input;
        this.output = output;
    }
    public void execute() {
        User requestedUser = mappers.getUserForEmail(input.getEmail());
        if (requestedUser == null) {
            throw new IllegalArgumentException("User not found");
        }
        try {
            ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("command", "printTransactions");
        ArrayNode transactionsCopy = requestedUser.getTransactions().deepCopy();
        ArrayNode sortedArray = new ObjectMapper().createArrayNode();
        StreamSupport.stream(transactionsCopy.spliterator(), false)
                .sorted((node1, node2) -> {
                    int timestamp1 = node1.get("timestamp").asInt();
                    int timestamp2 = node2.get("timestamp").asInt();
                    return Integer.compare(timestamp1, timestamp2);
                })
                .forEach(sortedArray::add);
        objectNode.set("output", sortedArray);
        objectNode.put("timestamp", input.getTimestamp());
        output.add(objectNode);
        } catch (Exception e) {
            return;
        }
    }
}
