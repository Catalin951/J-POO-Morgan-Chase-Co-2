package org.poo.commands.print;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.commands.Command;
import org.poo.fileio.CommandInput;
import org.poo.mapper.Mappers;
import org.poo.userDetails.User;

public final class PrintTransactions implements Command {
    private final Mappers mappers;
    private final CommandInput input;
    private final ArrayNode output;
    public PrintTransactions(final CommandInput input, final Mappers mappers,
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
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("command", "printTransactions");
        objectNode.set("output", requestedUser.getTransactions().deepCopy());
        objectNode.put("timestamp", input.getTimestamp());
        output.add(objectNode);
    }
}
