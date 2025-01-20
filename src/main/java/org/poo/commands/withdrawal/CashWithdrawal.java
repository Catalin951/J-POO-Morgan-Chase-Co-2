package org.poo.commands.withdrawal;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.builder.ObjectNodeBuilder;
import org.poo.commands.Command;
import org.poo.execution.ExecutionCommand;
import org.poo.execution.SingletonExecute;
import org.poo.graph.ExchangeGraph;
import org.poo.mapper.Mappers;
import org.poo.userDetails.User;
import org.poo.userDetails.account.Account;
import org.poo.userDetails.card.Card;

public final class CashWithdrawal implements Command {
    private final ExecutionCommand input;
    private final ArrayNode output;
    private final ExchangeGraph exchangeGraph;
    private final Mappers mappers;
    public CashWithdrawal(final ExecutionCommand input, final ArrayNode output,
                          final Mappers mappers, final ExchangeGraph exchangeGraph) {
        this.input = input;
        this.exchangeGraph = exchangeGraph;
        this.output = output;
        this.mappers = mappers;
    }
    public void execute() {
        User user = mappers.getUserForEmail(input.getEmail());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        Account account = null;
        Card card = null;
        for (Account iterAccount : user.getAccounts()) {
            for (Card iterCard : iterAccount.getCards()) {
                if (iterCard.getCardNumber().equals(input.getCardNumber())) {
                    account = iterAccount;
                    card = iterCard;
                }
            }
        }
        if (card == null) {
            output.add(SingletonExecute.makeGeneralError("cashWithdrawal",
                                      "Card not found", input.getTimestamp()));
            return;
        }
        double convertedAmount = exchangeGraph.convertCurrency("RON",
                                                            account.getCurrency(),
                                                            input.getAmount());
        double commission = user.getCommissionForTransaction(input.getAmount(),
                                                            account.getCurrency(),
                                                            exchangeGraph);
        if (account.getBalance() < convertedAmount * (1 + commission)) {
            ObjectNode objectNode = new ObjectNodeBuilder()
                    .put("timestamp", input.getTimestamp())
                    .put("description", "Insufficient funds").build();
            user.getTransactions().add(objectNode);
        } else {
            account.setBalance(account.getBalance() - convertedAmount * (1 + commission));
            ObjectNode objectNode = new ObjectNodeBuilder()
                    .put("timestamp", input.getTimestamp())
                    .put("amount", input.getAmount())
                    .put("description", "Cash withdrawal of " + input.getAmount())
                    .build();
            user.getTransactions().add(objectNode);
        }
    }
}
