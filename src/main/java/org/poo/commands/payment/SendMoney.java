package org.poo.commands.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.commands.Command;
import org.poo.commerciant.Commerciant;
import org.poo.execution.Execute;
import org.poo.fileio.CommandInput;
import org.poo.graph.ExchangeGraph;
import org.poo.mapper.Mappers;
import org.poo.userDetails.User;
import org.poo.userDetails.account.Account;

import java.util.Map;

public final class SendMoney implements Command {
    private final User[] users;
    private final CommandInput input;
    private final ExchangeGraph exchangeGraph;
    private final Mappers mappers;
    private final ArrayNode output;
    public SendMoney(final CommandInput input, final ArrayNode output, final User[] users,
                     final ExchangeGraph exchangeGraph, final Mappers mappers) {
        this.users = users;
        this.input = input;
        this.exchangeGraph = exchangeGraph;
        this.mappers = mappers;
        this.output = output;
    }

    /**
     * Uses the exchangeGraph to find a path between the currencies and checks
     * if the account has enough money
     * The transactions are placed in the account and in the user object of both
     * the payer and the receiver
     */
    public void execute() {
        User payer = null;
        Account payerAccount = null;
        for (User user : users) {
            for (Account account : user.getAccounts()) {
                if (account.getIban().equals(input.getAccount())) {
                    payer = user;
                    payerAccount = account;
                    break;
                }
            }
        }
        if (payer == null) {
            throw new IllegalArgumentException("User not found");
        }
        Account receiverAccount = mappers.getAccountForIban(input.getReceiver());
        if (mappers.hasAccountToCommerciant(receiverAccount)) {
            this.sendToCommerciant(payer, payerAccount);
        }
        else {
            this.sendToUser(payer, payerAccount);
        }
    }
    private void sendToUser(User payer, Account payerAccount) {
        User receiver = null;
        Account receiverAccount = null;
        for (User user : users) {
            Account mappedAccount = user.getAccountFromAlias(input.getAccount());
            if (mappedAccount != null) {
                receiver = user;
                receiverAccount = mappedAccount;
                break;
            }
            for (Account account : user.getAccounts()) {
                if (account.getIban().equals(input.getReceiver())) {
                    receiver = user;
                    receiverAccount = account;
                    break;
                }
            }
        }
        if (payer == null || receiver == null) {
            output.add(Execute.makeGeneralError("sendMoney", "User not found",
                                                input.getTimestamp()));
            return;
        }
        String from = payerAccount.getCurrency();
        String to = receiverAccount.getCurrency();
        double convertedAmount = exchangeGraph.convertCurrency(from, to, input.getAmount());
        double commission = payer.getCommissionForTransaction(input.getAmount(), from, exchangeGraph);
        System.out.println("CHECK OUTPUT OF COMISSION ; Commission in sendmoney: " + commission);
        // CHECK IF YOU MUST OUTPUT COMISSION TOO OR JUST THE INPTG.GETAMOUNT
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        if (payerAccount.getBalance() - input.getAmount() * (1 + commission) < 0) {
            objectNode.put("timestamp", input.getTimestamp());
            objectNode.put("description", "Insufficient funds");
            payer.getTransactions().add(objectNode);
            payerAccount.getTransactions().add(objectNode);
        } else {
            objectNode.put("timestamp", input.getTimestamp());
            objectNode.put("description", input.getDescription());
            objectNode.put("senderIBAN", payerAccount.getIban());
            objectNode.put("receiverIBAN", receiverAccount.getIban());
            objectNode.put("amount", input.getAmount() + " " + from);
            objectNode.put("transferType", "sent");
            payer.getTransactions().add(objectNode);

            ObjectNode receiverNode = objectNode.deepCopy();
            receiverNode.put("transferType", "received");
            receiverNode.put("amount", convertedAmount + " " + to);
            receiver.getTransactions().add(receiverNode);
            receiverAccount.getTransactions().add(receiverNode);

            payerAccount.getTransactions().add(objectNode);
            if (exchangeGraph.convertToRon(payerAccount.getCurrency(), input.getAmount()) >= 300) {
                payer.setNrOf300RonPayments(payer.getNrOf300RonPayments() + 1);
            }
            // Make payment
            payerAccount.setBalance(payerAccount.getBalance() - input.getAmount() * (1 + commission));
            receiverAccount.setBalance(receiverAccount.getBalance() + convertedAmount);
        }
    }

    private void sendToCommerciant(User payer, Account payerAccount) {
        Account receiverAccount = mappers.getAccountForIban(input.getReceiver());
        Commerciant commerciant = mappers.getCommerciantForAccount(receiverAccount);

        String from = payerAccount.getCurrency();
        String to = receiverAccount.getCurrency();
        double convertedAmount = input.getAmount();
        convertedAmount = exchangeGraph.convertCurrency(from, to, input.getAmount());
        double commission = payer.getCommissionForTransaction(input.getAmount(), from, exchangeGraph);
        System.out.println("CHECK OUTPUT OF COMISSION ; Commission in sendmoney: " + commission);
        // CHECK IF YOU MUST OUTPUT COMISSION TOO OR JUST THE INPTG.GETAMOUNT
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        if (payerAccount.getBalance() - input.getAmount() * (1 + commission) < 0) {
            objectNode.put("timestamp", input.getTimestamp());
            objectNode.put("description", "Insufficient funds");
            payer.getTransactions().add(objectNode);
            payerAccount.getTransactions().add(objectNode);
        } else {
            if (exchangeGraph.convertToRon(payerAccount.getCurrency(), input.getAmount()) >= 300) {
                payer.setNrOf300RonPayments(payer.getNrOf300RonPayments() + 1);
            }
            objectNode.put("timestamp", input.getTimestamp());
            objectNode.put("description", input.getDescription());
            objectNode.put("senderIBAN", payerAccount.getIban());
            objectNode.put("receiverIBAN", receiverAccount.getIban());
            objectNode.put("amount", input.getAmount() + " " + from);
            objectNode.put("transferType", "sent");
            payer.getTransactions().add(objectNode);

//            ObjectNode receiverNode = objectNode.deepCopy();
//            receiverNode.put("transferType", "received");
//            receiverNode.put("amount", convertedAmount + " " + to);
//            receiver.getTransactions().add(receiverNode);
//            receiverAccount.getTransactions().add(receiverNode);

            payerAccount.getTransactions().add(objectNode);

            // Make payment
            payerAccount.setBalance(payerAccount.getBalance() - input.getAmount() * (1 + commission));
            receiverAccount.setBalance(receiverAccount.getBalance() + convertedAmount);

            // Give cashback
            commerciant.giveCashback(payerAccount.getCurrency(), input.getAmount(), payer,
                                     payerAccount, exchangeGraph);
//            double ronAmount = exchangeGraph.convertToRon(payerAccount.getCurrency(), input.getAmount());
//            double thresholdCashback = payerAccount.getThresholdCashback(payer, ronAmount);
//            payerAccount.setBalance(payerAccount.getBalance() + input.getAmount() * thresholdCashback);
//            System.out.println("thresholdCashback in sendmoney : " + thresholdCashback);
        }
    }
}
