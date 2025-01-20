package org.poo.mapper;

import org.poo.commerciant.Commerciant;
import org.poo.userDetails.User;
import org.poo.userDetails.account.Account;
import org.poo.userDetails.account.BusinessEntity;

import java.util.HashMap;

/**
 * Mappers uses 3 HashMaps to store information so it can be accessed
 * Its utility stems from the different Maps that are all in one object:
 * account -> user
 * account -> commerciant
 * name -> commerciant
 * email -> user
 * iban -> account
 * user -> businessEntity
 */
public final class Mappers {
    private final HashMap<Account, User> accountToUserMap;
    private final HashMap<Account, Commerciant> accountToCommerciantMap;
    private final HashMap<String, Commerciant> nameToCommerciantMap;
    private final HashMap<String, User> emailToUserMap;
    private final HashMap<String, Account> ibanToAccountMap;
    private final HashMap<User, BusinessEntity> userToBusinessEntityMap;

    public Mappers() {
        this.accountToUserMap = new HashMap<>();
        this.emailToUserMap = new HashMap<>();
        this.ibanToAccountMap = new HashMap<>();
        this.accountToCommerciantMap = new HashMap<>();
        this.nameToCommerciantMap = new HashMap<>();
        this.userToBusinessEntityMap = new HashMap<>();
    }
    /**
     * Maps the given user to a businessEntity
     * @param user Key
     * @param businessEntity Value
     */
    public void addUserToBusinessEntity(final User user, final BusinessEntity businessEntity) {
        if (user != null && businessEntity != null) {
            userToBusinessEntityMap.put(user, businessEntity);
        } else {
            throw new IllegalArgumentException("Commerciant or account is null");
        }
    }
    /**
     * Returns the  that the name is mapped to
     * @param user Key
     * @return The value which is the corresponding Commerciant
     */
    public BusinessEntity getBusinessEntityForUser(final User user) {
        return userToBusinessEntityMap.get(user);
    }

    /**
     * Returns true if the user has been mapped to a businessEntity or false otherwise
     * @param user the mapped key
     * @return true or false depending on whether the key was mapped or not
     */
    public boolean hasUserToBusinessEntity(final User user) {
        return userToBusinessEntityMap.containsKey(user);
    }
    /**
     * Maps the given name to a commerciant
     * @param name Key
     * @param commerciant Value
     */
    public void addNameToCommerciant(final String name, final Commerciant commerciant) {
        if (name != null && commerciant != null) {
            nameToCommerciantMap.put(name, commerciant);
        } else {
            throw new IllegalArgumentException("Commerciant or account is null");
        }
    }
    /**
     * Returns the Commerciant that the name is mapped to
     * @param name Key
     * @return The value which is the corresponding Commerciant
     */
    public Commerciant getCommerciantForName(final String name) {
        return nameToCommerciantMap.get(name);
    }
    /**
     * Maps the given account to a commerciant
     * @param account Key
     * @param commerciant Value
     */
    public void addAccountToCommerciant(final Account account, final Commerciant commerciant) {
        if (account != null && commerciant != null) {
            accountToCommerciantMap.put(account, commerciant);
        }
    }
    /**
     * Returns the Commerciant that the account is mapped to
     * @param account Key
     * @return The value which is the corresponding Commerciant
     */
    public Commerciant getCommerciantForAccount(final Account account) {
        return accountToCommerciantMap.get(account);
    }

    /**
     * Returns true if the account has been mapped to a commerciant or false otherwise
     * @param account the mapped key
     * @return true or false depending on whether the key was mapped or not
     */
    public boolean hasAccountToCommerciant(final Account account) {
        return accountToCommerciantMap.containsKey(account);
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
