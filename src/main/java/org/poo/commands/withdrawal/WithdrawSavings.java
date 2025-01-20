package org.poo.commands.withdrawal;

import org.poo.Constants;
import org.poo.execution.SingletonExecute;
import org.poo.execution.ExecutionCommand;
import org.poo.graph.ExchangeGraph;
import org.poo.mapper.Mappers;
import org.poo.userDetails.User;
import org.poo.userDetails.account.Account;

public final class WithdrawSavings {
    private final ExecutionCommand input;
    private final Mappers mappers;
    private final ExchangeGraph exchangeGraph;
    public WithdrawSavings(final ExecutionCommand input, final Mappers mappers,
                           final ExchangeGraph exchangeGraph) {
        this.input = input;
        this.mappers = mappers;
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
        if (user.getAge() < Constants.MINIMUM_AGE) {
            SingletonExecute.addTransactionError("You don't have the minimum age required.",
                    input.getTimestamp(), user);
            return;
        }
        Account receivingAccount = getReceivingAccount(user);
        if (receivingAccount == null) {
            SingletonExecute.addTransactionError("You do not have a classic account.",
                    input.getTimestamp(), user);
            return;
        }
        double sendingAccountConvertedAmount = exchangeGraph.convertCurrency(
                                                account.getCurrency(), input.getCurrency(),
                                                input.getAmount());

        if (sendingAccountConvertedAmount < account.getBalance()) {
            SingletonExecute.addTransactionError("Insufficient funds",
                    input.getTimestamp(), user);
        }
    }

    public Account getReceivingAccount(final User user) {
        for (Account account : user.getAccounts()) {
            if (account.getCurrency().equals(input.getCurrency())
                && !account.getAccountType().equals("savings")) {
                return account;
            }
        }
        return null;
    }
}
