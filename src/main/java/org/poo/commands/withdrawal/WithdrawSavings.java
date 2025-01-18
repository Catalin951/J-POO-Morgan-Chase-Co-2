package org.poo.commands.withdrawal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.execution.Execute;
import org.poo.fileio.CommandInput;
import org.poo.graph.ExchangeGraph;
import org.poo.mapper.Mappers;
import org.poo.userDetails.User;
import org.poo.userDetails.account.Account;

public final class WithdrawSavings {
    private final CommandInput input;
    private final Mappers mappers;
    private final ArrayNode output;
    private final ExchangeGraph exchangeGraph;
    public WithdrawSavings(final CommandInput input, final ArrayNode output,
                           final Mappers mappers, final ExchangeGraph exchangeGraph) {
        this.input = input;
        this.mappers = mappers;
        this.output = output;
        this.exchangeGraph = exchangeGraph;
    }

    public void execute() {
        Account account = mappers.getAccountForIban(input.getAccount());
        User user = mappers.getUserForAccount(account);
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }
        if (!account.getAccountType().equals("savings")) {
            throw new IllegalArgumentException("Account is not of type savings.");
        }
        if (user.getAge() < 21) {
            Execute.addTransactionError("You don't have the minimum age required.",
                    input.getTimestamp(), user);
            return;
        }
        Account receivingAccount = getReceivingAccount(user);
        if (receivingAccount == null) {
            Execute.addTransactionError("You do not have a classic account.",
                    input.getTimestamp(), user);
            return;
        }
        double sendingAccountConvertedAmount = exchangeGraph.convertCurrency(
                                                account.getCurrency(), input.getCurrency(),
                                                input.getAmount());

        double commission = user.getCommissionForTransaction(input.getAmount(), input.getCurrency(), exchangeGraph);
        System.out.println("Commission in withdraw savings: " + commission);

        if (sendingAccountConvertedAmount < account.getBalance()) {
            Execute.addTransactionError("Insufficient funds",
                    input.getTimestamp(), user);
            return;
        }

        double receivingAccountConvertedAmount = exchangeGraph.convertCurrency(
                                                input.getCurrency(), receivingAccount.getCurrency(),
                                                input.getAmount());
    }

    public Account getReceivingAccount(User user) {
        for (Account account : user.getAccounts()) {
            if (account.getCurrency().equals(input.getCurrency())
                && !account.getAccountType().equals("savings")) {
                return account;
            }
        }
        return null;
    }
}
