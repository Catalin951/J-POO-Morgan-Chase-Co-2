package org.poo.commands.withdrawal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
            ObjectNode objectNode = new ObjectMapper().createObjectNode();
            objectNode.put("timestamp", input.getTimestamp());
            objectNode.put("description", "You don't have the minimum age required.");
            user.getTransactions().add(objectNode);
            return;
        }
        Account receivingAccount = getReceivingAccount(user);
        if (receivingAccount == null) {
            throw new IllegalArgumentException("Account not found");
        }
        double sendingAccountConvertedAmount = exchangeGraph.convertCurrency(
                                                account.getCurrency(), input.getCurrency(),
                                                input.getAmount());

        double commission = user.getCommissionForTransaction(input.getAmount(), input.getCurrency(), exchangeGraph);
        System.out.println("Commission in withdraw savings: " + commission);

        if (sendingAccountConvertedAmount < account.getBalance()) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        double receivingAccountConvertedAmount = exchangeGraph.convertCurrency(
                                                input.getCurrency(), receivingAccount.getCurrency(),
                                                input.getAmount());


        // Change sender balance and add his cashback
//        account.subtractFromBalance(sendingAccountConvertedAmount * (1 - thresholdCashback));
//        receivingAccount.addToBalance(receivingAccountConvertedAmount);

        // add in output
    }

    public Account getReceivingAccount(User user) {
        for (Account account : user.getAccounts()) {
            if (account.getCurrency().equals(input.getCurrency())) {
                return account;
            }
        }
        return null;
    }
}
