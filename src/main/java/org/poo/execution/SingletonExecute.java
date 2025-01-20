package org.poo.execution;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.commands.AddAccount;
import org.poo.commands.AddFunds;
import org.poo.commands.SetAlias;
import org.poo.commands.SetMinBalance;
import org.poo.commands.business.AddNewBusinessAssociate;
import org.poo.commands.CheckCardStatus;
import org.poo.commands.business.ChangeDepositLimit;
import org.poo.commands.business.ChangeSpendingLimit;
import org.poo.commands.create.CreateCard;
import org.poo.commands.create.CreateOneTimeCard;
import org.poo.commands.delete.DeleteAccount;
import org.poo.commands.delete.DeleteCard;
import org.poo.commands.interest.AddInterest;
import org.poo.commands.interest.ChangeInterestRate;
import org.poo.commands.payment.AcceptSplitPayment;
import org.poo.commands.payment.PayOnline;
import org.poo.commands.payment.SendMoney;
import org.poo.commands.payment.SplitPayment;
import org.poo.commands.print.PrintTransactions;
import org.poo.commands.UpgradePlan;
import org.poo.commands.print.PrintUsers;
import org.poo.commands.reports.BusinessReport;
import org.poo.commands.reports.Report;
import org.poo.commands.reports.SpendingsReport;
import org.poo.commands.withdrawal.CashWithdrawal;
import org.poo.commands.withdrawal.WithdrawSavings;
import org.poo.commerciant.Commerciant;
import org.poo.exchange.Exchange;
import org.poo.graph.ExchangeGraph;
import org.poo.mapper.Mappers;
import org.poo.userDetails.User;

/**
 * This class Launches all the commands, initialises the HashMaps used in most commands,
 * initialises the exchangeGraph used to convert currencies
 */
public final class SingletonExecute {
    private final ArrayNode output;
    private final User[] users;
    private final Exchange[] exchanges;
    private final ExecutionCommand[] commands;
    private final Commerciant[] commerciants;
    private static SingletonExecute instance;

    private SingletonExecute(final ArrayNode output, final User[] users,
                             final Commerciant[] commerciants,
                   final Exchange[] exchanges, final ExecutionCommand[] commands) {
        this.output = output;
        this.users = users;
        this.exchanges = exchanges;
        this.commands = commands;
        this.commerciants = commerciants;
    }
    public static SingletonExecute getInstance(final ArrayNode output,
                                               final User[] users, final Commerciant[] commerciants,
                                               final Exchange[] exchanges,
                                               final ExecutionCommand[] commands) {
        if (instance == null) {
            instance = new SingletonExecute(output, users, commerciants, exchanges, commands);
        }
        return instance;
    }
    /**
     * The method that does the initialisation and executes the commands
     */
    public void execution() {
        ExchangeGraph exchangeGraph = new ExchangeGraph(exchanges);
        Mappers mappers = new Mappers();
        for (User user : users) {
            mappers.addEmailToUser(user.getEmail(), user);
        }
        for (Commerciant commerciant : commerciants) {
            mappers.addAccountToCommerciant(commerciant.getAccount(), commerciant);
            mappers.addIbanToAccount(commerciant.getIban(), commerciant.getAccount());
            mappers.addNameToCommerciant(commerciant.getCommerciant(), commerciant);
        }
        for (ExecutionCommand command : commands) {
            switch (command.getCommand()) {
                case "printUsers":
                    new PrintUsers(command, users, output).execute();
                    break;
                case "printTransactions":
                    new PrintTransactions(command, mappers, output).execute();
                    break;
                case "addAccount":
                    new AddAccount(command, mappers).execute();
                    break;
                case "addFunds":
                    new AddFunds(command, mappers).execute();
                    break;
                case "createCard":
                    new CreateCard(command, mappers).execute();
                    break;
                case "createOneTimeCard":
                    new CreateOneTimeCard(command, mappers).execute();
                    break;
                case "deleteAccount":
                    new DeleteAccount(command, output, mappers).execute();
                    break;
                case "deleteCard":
                    new DeleteCard(command, users, mappers).execute();
                    break;
                case "payOnline":
                    new PayOnline(command, exchangeGraph, output, mappers).execute();
                    break;
                case "sendMoney":
                    new SendMoney(command, output, users, exchangeGraph, mappers).execute();
                    break;
                case "setAlias":
                    new SetAlias(command, mappers).execute();
                    break;
                case "setMinimumBalance":
                    new SetMinBalance(command, output, mappers).execute();
                    break;
                case "checkCardStatus":
                    new CheckCardStatus(command, users, output).execute();
                    break;
                case "changeInterestRate":
                    new ChangeInterestRate(command, mappers, output).execute();
                    break;
                case "splitPayment":
                    new SplitPayment(command, mappers).execute();
                    break;
                case "addInterest":
                    new AddInterest(command, output, mappers).execute();
                    break;
                case "report":
                    new Report(command, output, mappers).execute();
                    break;
                case "spendingsReport":
                    new SpendingsReport(command, output, mappers).execute();
                    break;
                case "withdrawSavings":
                    new WithdrawSavings(command, mappers, exchangeGraph).execute();
                    break;
                case "upgradePlan":
                    new UpgradePlan(command, mappers, exchangeGraph).execute();
                    break;
                case "cashWithdrawal":
                    new CashWithdrawal(command, output, mappers, exchangeGraph).execute();
                    break;
                case "acceptSplitPayment":
                    new AcceptSplitPayment(command, exchangeGraph, mappers).execute();
                    break;
                case "addNewBusinessAssociate":
                    new AddNewBusinessAssociate(command, mappers).execute();
                    break;
                case "changeSpendingLimit":
                    new ChangeSpendingLimit(command, mappers).execute();
                    break;
                case "changeDepositLimit":
                    new ChangeDepositLimit(command, mappers).execute();
                    break;
                case "businessReport":
                    new BusinessReport(command, mappers).execute();
                    break;
                default:
                    throw new IllegalArgumentException("Command " + command.getCommand()
                                                        + " doesn't exist.");
            }
        }
    }

    /**
     * This method is used around the program to quickly make an ObjectNode that describes an error
     * Since there are no standard errors this is the only method of this type,
     * because it is being used the most often
     * @param command The command that is put inside the object
     * @param description The description that is put inside the object
     * @param timestamp The timestamp that is put inside the object
     * @return ObjectNode that represents an error
     */
    public static ObjectNode makeGeneralError(final String command, final String description,
                                              final int timestamp) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode outputNode = mapper.createObjectNode();
        outputNode.put("description", description);
        outputNode.put("timestamp", timestamp);
        ObjectNode errorNode = mapper.createObjectNode();
        errorNode.put("command", command);
        errorNode.set("output", outputNode);
        errorNode.put("timestamp", timestamp);
        return errorNode;
    }

    /**
     * Transaction error that is usually seen by using printTransactions on a user
     * @param description the error description
     * @param timestamp the timestamp of the command
     * @param user in this object's transactions list will the objectNode be placed
     */
    public static void addTransactionError(final String description,
                                           final int timestamp,
                                           final User user) {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("timestamp", timestamp);
        objectNode.put("description", description);
        user.getTransactions().add(objectNode);
    }

    /**
     * Terminates the instance for the next test
     */
    public static void finishInstance() {
        instance = null;
    }
}

