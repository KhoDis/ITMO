import java.util.concurrent.locks.ReentrantLock;

/**
 * Bank implementation.
 * This implementation is thread-safe.
 *
 * @author Adis Khodzhayarov
 */
public class BankImpl implements Bank {
    /**
     * An array of accounts by index.
     */
    private final Account[] accounts;

    /**
     * Creates new bank instance.
     *
     * @param n the number of accounts (numbered from 0 to n-1).
     */
    public BankImpl(int n) {
        accounts = new Account[n];
        for (int i = 0; i < n; i++) {
            accounts[i] = new Account();
        }
    }

    /**
     * Gets the number of accounts in the bank.
     *
     * @return the number of accounts.
     */
    @Override
    public int getNumberOfAccounts() {
        return accounts.length;
    }

    /**
     * Gets the amount of money on the account.
     *
     * @param index the account number.
     *
     * @return the amount of money on the account.
     */
    @Override
    public long getAmount(int index) {
        try {
            accounts[index].lock.lock();
            return accounts[index].amount;
        } finally {
            accounts[index].lock.unlock();
        }
    }

    /**
     * Gets the total amount of money in the bank.
     *
     * @return the total amount of money.
     */
    @Override
    public long getTotalAmount() {
        try {
            long sum = 0;
            for (Account account : accounts) {
                account.lock.lock();
                sum += account.amount;
            }
            return sum;
        } finally {
            for (Account account : accounts) {
                account.lock.unlock();
            }
        }
    }

    /**
     * Deposits the specified amount of money to the specified account.
     *
     * @param index the account number.
     * @param amount the amount of money to deposit.
     *
     * @return the new amount of money on the account.
     */
    @Override
    public long deposit(int index, long amount) {
        Account account = accounts[index];
        try {
            accounts[index].lock.lock();
            if (amount <= 0)
                throw new IllegalArgumentException("Invalid amount: " + amount);
            if (amount > MAX_AMOUNT || account.amount + amount > MAX_AMOUNT)
                throw new IllegalStateException("Overflow");
            account.amount += amount;
            return account.amount;
        } finally {
            account.lock.unlock();
        }
    }

    /**
     * Withdraws the specified amount of money from the specified account.
     *
     * @param index the account number.
     * @param amount the amount of money to withdraw.
     *
     * @return the new amount of money on the account.
     */
    @Override
    public long withdraw(int index, long amount) {
        Account account = accounts[index];
        try {
            account.lock.lock();
            if (amount <= 0)
                throw new IllegalArgumentException("Invalid amount: " + amount);
            if (account.amount < amount)
                throw new IllegalStateException("Underflow");
            account.amount -= amount;
            return account.amount;
        } finally {
            account.lock.unlock();
        }
    }

    /**
     * Transfers the specified amount of money from one account to another.
     *
     * @param fromIndex the account number to withdraw from.
     * @param toIndex the account number to deposit to.
     */
    @Override
    public void transfer(int fromIndex, int toIndex, long amount) {
        Account from = accounts[fromIndex];
        Account to = accounts[toIndex];
        try {
            if (fromIndex < toIndex) {
                from.lock.lock();
                to.lock.lock();
            } else {
                to.lock.lock();
                from.lock.lock();
            }
            if (amount <= 0)
                throw new IllegalArgumentException("Invalid amount: " + amount);
            if (amount > from.amount)
                throw new IllegalStateException("Underflow");
            else if (amount > MAX_AMOUNT || to.amount + amount > MAX_AMOUNT)
                throw new IllegalStateException("Overflow");
            from.amount -= amount;
            to.amount += amount;
        } finally {
            to.lock.unlock();
            from.lock.unlock();
        }
    }

    /**
     * Private account data structure.
     */
    static class Account {
        /**
         * Account lock.
         */
        private final ReentrantLock lock = new ReentrantLock();

        /**
         * Amount of funds in this account.
         */
        private long amount;
    }
}
