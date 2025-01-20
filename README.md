
# J. POO Morgan Chase & Co.
 ### Manole Catalin-Gabriel 321CD
 #### A program designed to handle given commands like: account creation of business or personal type, transactions, payments, and report generation for them.

### Design Patterns used:

- **Factory**: Inside the AccountFactory and the CommerciantFactory classes. I used it because it encapsulates
                the account creation logic and makes it easily expandable with more classes.
- **Command**: All the commands in the commands package used by the invoker class SingletonExecute.
                All of them implement the Command interface. It maintains the SOLID principle since every class
                does it's own thing and the functionality can be changed (or extended) without modifying the SingletonExecute.
- **Singleton**: The SingletonExecute class uses the Singleton design pattern to allow only one instance of the class.
                This makes sure there is only one execution of the commands. There can be only one bank.
- **Builder**: The ObjectNodeBuilder uses the Builder design pattern to make ObjectNode objects easily
                throughout the entire code. Used in the ObjectNodeBuilder class.
## Project classes

### Commands

**Command** is the interface that all the commands implement. The command design pattern is used to execute the given commands gracefully, every command having its own class.

- **AddAccount.java**: Adds a new account to a user.
- **AddFunds.java**: Adds funds to an account.
- **CheckCardStatus.java**: Checks the status of a user's card (the frozen field) and blocks the card permanently if the card's balance is under the minimum balance.
- **SetAlias.java**: Sets an alias for used for account identification. A Hashmap is used for mapping the alias to an IBAN.
- **SetMinBalance.java**: Sets a minimum balance for an account. If the balance is below this and the checkCardStatus command is executed on a card, the card freezes.

#### **Create**
- **CreateCard.java**: Creates a standard card for an account.
- **CreateOneTimeCard.java**: Creates a one-time-use card for an account.

#### **Delete**
- **DeleteAccount.java**: Deletes a user’s account.
- **DeleteCard.java**: Deletes a user’s card.

#### **Business**
- **AddNewBusinessAssociate.java**: Does multiple checks and adds to the array of businessEntities inside the account so the user can be remember and tracked.
- **ChangeDepositLimit.java**: Changes deposit limit for all Employees.
- **ChangeSpendingLimit.java**: Changes spending limit for all Employees.


#### **Interest**
- **AddInterest.java**: Adds interest to a user’s savings account. If the account isn't a savings account, an error is outputted.
- **ChangeInterestRate.java**: Changes the interest rate for a user’s account, the same error is outputted if the account is not a savings one.

#### **Payment**
These classes rely on GraphExchange to properly convert one currency to another. All of them output errors if they fail, and place their respective transactions in the user and in the account:
- **PayOnline.java**: Online payments with commerciants, only 1 card is used.
- **SendMoney.java**: Enables money transfers between accounts.
- **SplitPayment.java**: Allows users to split a payment among multiple accounts with equal or varying amounts. In case of failure, all of the accounts get an error. A command is queued into all the accounts that participate in transaction, and they either have to accept it or reject it. When all participants have replied, the transaction is completed.
- **AcceptSplitPayment**: Retrieves the ExecutionCommand from the queue, increase the acceptedSplitPayments field by one and if it becomes equal to the number of participants, starts handling the transaction. 
- **RejectSplitPayment**: Retrieves the ExecutionCommand from the queue. Left to implement the adding of the transaction failure to the involved accounts.

#### **Print**
- **PrintTransactions.java**: Outputs the transaction history of a user.
- **PrintUsers.java**: Outputs the details of all the users.

#### **ServicePlan**
- **UpgradePlan.java**: Gives an error if the request isn't valid. Calculates the fee depending on the current plan and the requested plan, and extracts the sum from the account if it can be done.

#### **Withdrawal**
- **CashWithdrawal.java**: It subtracts the given amount from the account if there is enough balance.
- **WithdrawSavings**: Allows to withdraw an amount from a savings account if the user has a classic account and is older than 21.

#### **Reports**
- **Report.java**: This command is outputting the transactions of an account between 2 specific timestamps. Different transactions are
                    chosen depending on what account is used. Through the entirety of the program the transactions are being placed in
                    the transactions fields of the account objects and this is where it is being put to use.
- **SpendingsReport.java**: A report only for classic accounts that outputs the transactions and how much has been spent on every commerciant.
                            This is achieved with the internal class CommerciantPayments which contains a TreeMap that maps the name of the
                            commerciant to the amount they have at a certain moment in a sorted fashion (by name). whenever the add method is called again,
                            the value increases by the amount it was called with.
- **BusinessReport**: Not yet finished, for now it just makes a report of the transaction type for the entire timeline (not between the requested timestamps)

### Exchange

- **Exchange.java**: Holds information about the exchanges.

### Graph

- **ExchangeGraph.java**: Has a graph structure that adds different currencies as nodes and contains a convertCurrency method which uses Djikstra's algorithm to find the shortest path between 2 currencies, calculating sequentially the converted amount by multiplying the current amount by the weight of the edge.

### Execution

- **SingletonExecute.java**: The main class in this program. The singleton design pattern is used, making it possible only for one instance of the class to exist at a time. It is the invoker of the command design pattern.


### Factories

- **AccountFactory.java**: The factory design pattern is used for creating the three different types of accounts.
- **CommerciantFactory.java**: The factory design pattern is used for creating the two types of commerciants: nrOfTransactions and spendingThreshold

### **Builder**
- **ObjectNodeBuilder.java**: The builder design pattern is used to create ObjectNodes easily. There are many overloaded methods for all types of possible fields and their values.

### Mapper

- **Mappers.java**: Provides mappings between the classes used, such as users and accounts and ibans. The HashMaps used are for: 
- **acount -> user**
- **email -> user**
- **iban -> account**
- **user -> businessEntity**
- **name -> commerciant**
- **account -> commerciant**

### UserDetails

The `userDetails` package contains the the information-holding classes:

#### **User.java**
Defines the user entity, which can have multiple accounts each with multiple cards.

#### **Account**
- **Account.java**: Abstract class that helps in using polimorphism.
- **ClassicAccount.java**: A classic account type.
- **SavingsAccount.java**: An account that has an additional field interestRate which can change the account's ballance upon the execution of the addInterest command.
- **BusinessAccount**: An account that initialises the businessEntities ArrayList. Here are all the people with access to the account.
- **BusinessEntity**: Entity that can be either Owner, Manager or Employee. Keeps track of the payments and the money spent / added.
- **CommerciantAccount**: Account used for Commerciants.

#### **Card**
- **Card.java**: Abstract base class for cards.
- **ClassicCard.java**: A Classic card.
- **OneTimeCard.java**: A one-time-use card that renews itself every time it used by destroying itself and reputting itself in the arraylist containing the cards inside the account.

### Main

- **Main.java**: The entry point that also converts the input classes to actual use classes.