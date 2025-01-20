package org.poo.factories;

import org.poo.execution.ExecutionCommand;
import org.poo.userDetails.account.Account;
import org.poo.userDetails.account.SavingsAccount;
import org.poo.userDetails.account.ClassicAccount;
import org.poo.userDetails.account.BusinessAccount;
import org.poo.userDetails.account.CommerciantAccount;
import org.poo.utils.Utils;

public final class AccountFactory {
    private AccountFactory() {
    }
    /**
     * This method creates one of the two types of accounts depending on what command is given
     * @param command The inputted command
     * @return Reference to either savings account or classic account
     */
    public static Account createAccount(final ExecutionCommand command) {
        String accountType = command.getAccountType();
        String iban = Utils.generateIBAN();
        String currency = command.getCurrency();

        return switch (accountType) {
            case "savings" -> {
                double interestRate = command.getInterestRate();
                yield new SavingsAccount(currency, iban, interestRate);
            }
            case "classic" -> new ClassicAccount(currency, iban);
            case "business" -> new BusinessAccount(currency, iban);
            default -> throw new IllegalArgumentException("Invalid account type: " + accountType);
        };
    }

    /**
     * Especially made for the commerciant, returns a commerciant account
     * @param iban the iban for the account
     * @return commerciant account
     */
    public static Account createCommerciantAccount(final String iban) {
        return new CommerciantAccount("RON", iban);
    }
}
