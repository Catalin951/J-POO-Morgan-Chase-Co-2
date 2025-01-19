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

public final class AcceptSplitPayment implements Command {
    private final ExecutionCommand input;
    private final ExchangeGraph exchangeGraph;
    private final Mappers mappers;
    public AcceptSplitPayment(final ExecutionCommand input, final ExchangeGraph exchangeGraph,
                        final Mappers mappers) {
        this.input = input;
        this.exchangeGraph = exchangeGraph;
        this.mappers = mappers;
    }
    public void execute() {
        User currentSplitUser = mappers.getUserForEmail(input.getEmail());
        if (currentSplitUser == null) {
            return;
        }
        ExecutionCommand command = currentSplitUser.getSplitPaymentQueue().poll();
        if (command == null) {
            return;
        }
        command.setAcceptedSplitPayments(command.getAcceptedSplitPayments() + 1);
        if (command.getAcceptedSplitPayments() == command.getAccounts().size()) {
            List<String> splittingIBANs = command.getAccounts();
            ArrayList<Account> splittingAccounts = new ArrayList<>();
            ArrayNode involvedAccountsArray = new ObjectMapper().createArrayNode();
            ArrayNode amountForUsersArray = new ObjectMapper().createArrayNode();
            int i = 0;
            for (String splittingIBAN : splittingIBANs) {
                involvedAccountsArray.add(splittingIBAN);
                Account splittingAccount = mappers.getAccountForIban(splittingIBAN);
                User splittingUser = mappers.getUserForAccount(splittingAccount);
//                splittingUser.getCorrespondingSplitPaymentAccount().remove();
//                splittingUser.getSplitPaymentQueue().remove();
                double splitAmount = 0;
                if (command.getSplitPaymentType().equals("equal")) {
                    splitAmount = command.getAmount() / command.getAccounts().size();
                } else {
                    splitAmount = command.getAmountForUsers().get(i++);
                    amountForUsersArray.add(splitAmount);
                }
                splittingAccount.setCurrentSplitAmount(splitAmount);
                splittingAccounts.addLast(splittingAccount);
            }

            if (splittingAccounts.size() != splittingIBANs.size()) {
                throw new IllegalArgumentException("Not all accounts exist");
            }

            ObjectNode outputNode = makeOutputNode(command, amountForUsersArray, involvedAccountsArray);
            ArrayList<Double> newBalances = new ArrayList<>();

            for (Account account : splittingAccounts) {
                String from = command.getCurrency();
                String to = account.getCurrency();
                double convertedAmount = exchangeGraph.convertCurrency(from, to,
                        account.getCurrentSplitAmount());
                if (account.getBalance() < convertedAmount) {
                    outputNode.put("error", "Account " + account.getIban()
                            + " has insufficient funds for a split payment.");
                    addTransactionFailure(splittingAccounts, outputNode);
                    return;
                }
                newBalances.add(account.getBalance() - convertedAmount);
            }
            i = 0;
            for (Account account : splittingAccounts) {
                account.setBalance(newBalances.get(i));
                User splittingUser = mappers.getUserForAccount(account);
                splittingUser.getTransactions().add(outputNode);
                i++;
            }
        }
//        } else {
//            currentSplitUser.getSplitPaymentQueue().remove();
//        }
    }

    private static ObjectNode makeOutputNode(ExecutionCommand command, ArrayNode amountForUsersArray, ArrayNode involvedAccountsArray) {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("timestamp", command.getTimestamp());
        String formattedString = String.format("%.2f", command.getAmount())
                + " " + command.getCurrency();
        String description = "Split payment of " + formattedString;
        objectNode.put("description", description);
        objectNode.put("currency", command.getCurrency());
        if (command.getSplitPaymentType().equals("custom")) {
            objectNode.set("amountForUsers", amountForUsersArray);
            objectNode.put("splitPaymentType", "custom");
        }
        else {
            objectNode.put("amount", command.getAmount() / command.getAccounts().size());
            objectNode.put("splitPaymentType", "equal");
        }
        objectNode.set("involvedAccounts", involvedAccountsArray);
        return objectNode;
    }

    private void addTransactionFailure(final ArrayList<Account> splittingAccounts, ObjectNode outputNode) {
        for (Account account : splittingAccounts) {
            account.getTransactions().add(outputNode);
            mappers.getUserForAccount(account).getTransactions().add(outputNode);
        }
    }
}