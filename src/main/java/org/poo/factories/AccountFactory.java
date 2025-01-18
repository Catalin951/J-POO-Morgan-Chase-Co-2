package org.poo.factories;

import org.poo.fileio.CommandInput;
import org.poo.userDetails.account.Account;
import org.poo.userDetails.account.ClassicAccount;
import org.poo.userDetails.account.CommerciantAccount;
import org.poo.userDetails.account.SavingsAccount;
import org.poo.utils.Utils;

public class AccountFactory {
    /**
     * This method creates one of the two types of accounts depending on what command is given
     * @param command The inputted command
     * @return Reference to either savings account or classic account
     */
    public static Account createAccount(final CommandInput command) {
        String accountType = command.getAccountType();
        String iban = Utils.generateIBAN();
        String currency = command.getCurrency();

        return switch (accountType) {
            case "savings" -> {
                double interestRate = command.getInterestRate();
                yield new SavingsAccount(currency, iban, interestRate);
            }
            case "classic" -> new ClassicAccount(currency, iban);
            default -> throw new IllegalArgumentException("Invalid account type: " + accountType);
        };
    }
    public static Account createCommerciantAccount(final String iban) {
        return new CommerciantAccount("RON", iban);
    }
}
