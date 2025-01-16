package org.poo.userDetails;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Data;
import org.poo.fileio.UserInput;
import org.poo.userDetails.account.Account;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is where all the information about an user is held
 * It contains an alias map that maps an alias to an account
 */
@Data
public final class User {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final ArrayList<Account> accounts;
    private final ArrayNode transactions;
    private final HashMap<String, Account> aliasMap;

    public User(final UserInput userInput) {
        firstName = userInput.getFirstName();
        lastName = userInput.getLastName();
        email = userInput.getEmail();
        accounts = new ArrayList<>();
        transactions = new ObjectMapper().createArrayNode();
        aliasMap = new HashMap<>();
    }

    /**
     * Maps the alias to an account
     * @param alias Key
     * @param account Value
     */
    public void setAlias(final String alias, final Account account) {
        aliasMap.put(alias, account);
    }

    /**
     * Returns the value to which the alias is mapped
     * @param alias key
     * @return Value
     */
    public Account getAccountFromAlias(final String alias) {
        return aliasMap.get(alias);
    }
}
