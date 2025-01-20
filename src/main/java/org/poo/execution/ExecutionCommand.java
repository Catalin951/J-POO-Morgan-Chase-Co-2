package org.poo.execution;
import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;

import java.util.List;

@Getter
@Setter
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

    public ExecutionCommand(final CommandInput commandInput) {
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
}
