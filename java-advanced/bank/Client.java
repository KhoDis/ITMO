package info.kgeorgiy.ja.khodzhayarov.bank;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import info.kgeorgiy.ja.khodzhayarov.bank.utils.ArgsParser;

public class Client {
    /** Utility class. */
    private Client() {}

    public static void main(final String... args) {
        Bank bank = null;
        try {
            bank = (Bank) Naming.lookup("//localhost/bank");
        } catch (final NotBoundException e) {
            log("Bank is not bound", e);
            return;
        } catch (final MalformedURLException e) {
            log("Bank URL is invalid", e);
            return;
        } catch (RemoteException e) {
            log("Bank could not be contacted", e);
        }

        if (bank == null) {
            return;
        }

        try {
            ArgsParser parser = new ArgsParser(args, "firstName", "lastName", "passport", "accountId", "change");

            process(bank, parser.getString(), parser.getString(), parser.getString(), parser.getString(), parser.getInt());
        } catch (ArgsParser.ArgsParserException e) {
            log("Arguments are invalid: " + e.getMessage(), e);
        } catch (RemoteException e) {
            log("Bank could not be contacted", e);
        }
    }

    private static void process(Bank bank, String firstName, String lastName, String passportId, String accountId, int change) throws RemoteException {
        RemotePerson person = getRemotePerson(bank, firstName, lastName, passportId);
        if (person == null) {
            log("Unable to get person instance.");
        }
        Account account = getAccount(person, accountId, bank);
        if (account != null) {
            account.setAmount(account.getAmount() + change);
        }
        log("Transaction has successfully accomplished.");
    }

    private static Account getAccount(Person person, String accountId, Bank bank) throws RemoteException {
        Account account = bank.getLocalAccount(person, accountId);
        if (account == null) {
            if (!bank.createAccount(person, accountId)) {
                log("Unable to create new account.");
                return null;
            }
            return bank.getLocalAccount(person, accountId);
        }
        return account;
    }

    private static RemotePerson getRemotePerson(Bank bank, String firstName, String lastName, String passportId) throws RemoteException {
        RemotePerson person = bank.getRemotePerson(passportId);
        if (person == null) {
            if (!bank.createPerson(firstName, lastName, passportId)) {
                log("Unable to create new person.");
                return null;
            }
            return bank.getRemotePerson(passportId);
        }
        return person;
    }

    private static void log(String x) {
        System.err.println(x + ".");
    }

    private static void log(String x, Exception e) {
        System.err.println(x + ": " + e.getMessage());
    }
}
