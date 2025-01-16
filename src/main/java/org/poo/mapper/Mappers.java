package org.poo.mapper;

import org.poo.userDetails.User;
import org.poo.userDetails.account.Account;

import java.util.HashMap;

/**
 * Mappers uses 3 HashMaps to store information so it can be accessed
 * Its utility stems from the different Maps that are all in one object:
 * account -> user
 * email -> user
 * iban -> account
 */
public final class Mappers {
    private final HashMap<Account, User> accountToUserMap;
    private final HashMap<String, User> emailToUserMap;
    private final HashMap<String, Account> ibanToAccountMap;

    public Mappers() {
        this.accountToUserMap = new HashMap<>();
        this.emailToUserMap = new HashMap<>();
        this.ibanToAccountMap = new HashMap<>();
    }
    /**
     * Maps the given account to an user
     * @param account Key
     * @param user Value
     */
    public void addAccountToUser(final Account account, final User user) {
        if (account != null && user != null) {
            accountToUserMap.put(account, user);
        }
    }
    /**
     * Returns the user that the account is mapped to
     * @param account Key
     * @return The value which is the corresponding user
     */
    public User getUserForAccount(final Account account) {
        return accountToUserMap.get(account);
    }
    /**
     * Maps the given email to an user
     * @param email Key
     * @param user Value
     */
    public void addEmailToUser(final String email, final User user) {
        if (email != null && user != null) {
            emailToUserMap.put(email, user);
        }
    }
    /**
     * Returns the user that the email is mapped to
     * @param email Key
     * @return The value which is the corresponding user
     */
    public User getUserForEmail(final String email) {
        return emailToUserMap.get(email);
    }
    /**
     * Maps the given iban to an account
     * @param iban Key
     * @param account Value
     */
    public void addIbanToAccount(final String iban, final Account account) {
        if (iban != null && account != null) {
            ibanToAccountMap.put(iban, account);
        }
    }
    /**
     * Returns the account that the iban is mapped to
     * @param iban Key
     * @return The value which is the corresponding account
     */
    public Account getAccountForIban(final String iban) {
        return ibanToAccountMap.get(iban);
    }
}
