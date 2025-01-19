package org.poo.execution;

import lombok.Data;
import org.poo.fileio.CommandInput;

import java.util.List;

@Data
public final class ExecutionCommand {
    private String command;
    private String email;
    private String account;
    private String newPlanType;
    private String role;
    private String currency;
    private String target;
    private String description;
    private String cardNumber;
    private String commerciant;
    private String receiver;
    private String alias;
    private String accountType;
    private String splitPaymentType;
    private String type;
    private String location;
    private int timestamp;
    private int startTimestamp;
    private int endTimestamp;
    private int acceptedSplitPayments;
    private double splitAmountToPay;
    private double interestRate;
    private double spendingLimit;
    private double depositLimit;
    private double amount;
    private double minBalance;
    private List<String> accounts;
    private List<Double> amountForUsers;

    public ExecutionCommand(CommandInput commandInput) {
        splitAmountToPay = 0;
        acceptedSplitPayments = 0;
        this.command = commandInput.getCommand();
        this.email = commandInput.getEmail();
        this.account = commandInput.getAccount();
        this.newPlanType = commandInput.getNewPlanType();
        this.role = commandInput.getRole();
        this.currency = commandInput.getCurrency();
        this.target = commandInput.getTarget();
        this.description = commandInput.getDescription();
        this.cardNumber = commandInput.getCardNumber();
        this.commerciant = commandInput.getCommerciant();
        this.receiver = commandInput.getReceiver();
        this.alias = commandInput.getAlias();
        this.accountType = commandInput.getAccountType();
        this.splitPaymentType = commandInput.getSplitPaymentType();
        this.type = commandInput.getType();
        this.location = commandInput.getLocation();
        this.timestamp = commandInput.getTimestamp();
        this.startTimestamp = commandInput.getStartTimestamp();
        this.endTimestamp = commandInput.getEndTimestamp();
        this.interestRate = commandInput.getInterestRate();
        this.spendingLimit = commandInput.getSpendingLimit();
        this.depositLimit = commandInput.getDepositLimit();
        this.amount = commandInput.getAmount();
        this.minBalance = commandInput.getMinBalance();
        this.accounts = commandInput.getAccounts();
        this.amountForUsers = commandInput.getAmountForUsers();
    }
    public ExecutionCommand(ExecutionCommand executionCommand) {
        this.splitAmountToPay = executionCommand.splitAmountToPay;
        this.acceptedSplitPayments = executionCommand.acceptedSplitPayments;
        this.command = executionCommand.getCommand();
        this.email = executionCommand.getEmail();
        this.account = executionCommand.getAccount();
        this.newPlanType = executionCommand.getNewPlanType();
        this.role = executionCommand.getRole();
        this.currency = executionCommand.getCurrency();
        this.target = executionCommand.getTarget();
        this.description = executionCommand.getDescription();
        this.cardNumber = executionCommand.getCardNumber();
        this.commerciant = executionCommand.getCommerciant();
        this.receiver = executionCommand.getReceiver();
        this.alias = executionCommand.getAlias();
        this.accountType = executionCommand.getAccountType();
        this.splitPaymentType = executionCommand.getSplitPaymentType();
        this.type = executionCommand.getType();
        this.location = executionCommand.getLocation();
        this.timestamp = executionCommand.getTimestamp();
        this.startTimestamp = executionCommand.getStartTimestamp();
        this.endTimestamp = executionCommand.getEndTimestamp();
        this.interestRate = executionCommand.getInterestRate();
        this.spendingLimit = executionCommand.getSpendingLimit();
        this.depositLimit = executionCommand.getDepositLimit();
        this.amount = executionCommand.getAmount();
        this.minBalance = executionCommand.getMinBalance();
        this.accounts = executionCommand.getAccounts();
        this.amountForUsers = executionCommand.getAmountForUsers();
    }
}