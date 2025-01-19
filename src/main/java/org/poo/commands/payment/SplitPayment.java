package org.poo.commands.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.commands.Command;
import org.poo.execution.ExecutionCommand;
import org.poo.graph.ExchangeGraph;
import org.poo.mapper.Mappers;
import org.poo.userDetails.User;
import org.poo.userDetails.account.Account;

import java.util.ArrayList;
import java.util.List;

public final class SplitPayment implements Command {
    private final ExecutionCommand input;
    private final ExchangeGraph exchangeGraph;
    private final Mappers mappers;
    public SplitPayment(final ExecutionCommand input, final ExchangeGraph exchangeGraph,
                        final Mappers mappers) {
        this.input = input;
        this.exchangeGraph = exchangeGraph;
        this.mappers = mappers;
    }

    /**
     * This command returns an exception if not all the given accounts exist
     * Uses the exchangeGraph to find a path between the currencies of each
     * account and the given currency and makes the conversion.
     * Firstly checks if the accounts have enough money,
     * an error being placed in all users and accounts if not
     * At the end all the balances are updated
     */
    public void execute() {
        // Add to each account the command so they know how much they have to pay and if all have paid
        // also to be able to add to their accounts the transaction
        int i = 0;
        for (String iban : input.getAccounts()) {
            Account account = mappers.getAccountForIban(iban);
            if (account == null) {
                throw new IllegalArgumentException("Invalid iban: " + iban);
            }
            User user = mappers.getUserForAccount(account);
            if (user == null) {
                throw new IllegalArgumentException("Invalid account: " + iban);
            }
            user.getSplitPaymentQueue().add(input);
            user.getCorrespondingSplitPaymentAccount().add(iban);
        }
    }
    /*
    public void execute() {
        List<String> splittingIBANs = input.getAccounts();
        ArrayList<Account> splittingAccounts = new ArrayList<>();
        ArrayNode involvedAccountsArray = new ObjectMapper().createArrayNode();
        for (String splittingIBAN : splittingIBANs) {
            involvedAccountsArray.add(splittingIBAN);
            splittingAccounts.addFirst(mappers.getAccountForIban(splittingIBAN));
        }

        if (splittingAccounts.size() != splittingIBANs.size()) {
            throw new IllegalArgumentException("Not all accounts exist");
        }

        double splitAmount = input.getAmount() / splittingAccounts.size();
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("timestamp", input.getTimestamp());
        String formattedString = String.format("%.2f", input.getAmount())
                                               + " " + input.getCurrency();
        String description = "Split payment of " + formattedString;
        objectNode.put("description", description);
        objectNode.put("currency", input.getCurrency());
        objectNode.put("amount", splitAmount);
        objectNode.set("involvedAccounts", involvedAccountsArray);

        ArrayList<Double> newBalances = new ArrayList<>();

        for (Account account : splittingAccounts) {
            String from = input.getCurrency();
            String to = account.getCurrency();
            double convertedAmount = exchangeGraph.convertCurrency(from, to, splitAmount);
            if (account.getBalance() < convertedAmount) {
                addTransactionFailure(splittingAccounts, involvedAccountsArray,
                                      splitAmount, description, account.getIban());
                return;
            }
            newBalances.add(account.getBalance() - convertedAmount);
        }
        int i = 0;
        for (Account account : splittingAccounts) {
            account.setBalance(newBalances.get(i));
            User splittingUser = mappers.getUserForAccount(account);
            splittingUser.getTransactions().add(objectNode);
            i++;
        }
    }
    private void addTransactionFailure(final ArrayList<Account> splittingAccounts,
                                       final ArrayNode involvedAccountsArray,
                                       final double splitAmount, final String description,
                                       final String iban) {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("timestamp", input.getTimestamp());
        objectNode.put("description", description);
        objectNode.put("currency", input.getCurrency());
        objectNode.put("amount", splitAmount);
        objectNode.set("involvedAccounts", involvedAccountsArray);
        objectNode.put("error", "Account " + iban
                + " has insufficient funds for a split payment.");
        for (Account account : splittingAccounts) {
            account.getTransactions().add(objectNode);
            mappers.getUserForAccount(account).getTransactions().add(objectNode);
        }
    }
     */
}
