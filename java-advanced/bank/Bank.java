package info.kgeorgiy.ja.khodzhayarov.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Set;

public interface Bank extends Remote {
    /**
     * Returns account by identifier.
     * @param subId account id TODO
     * @return account with specified identifier or {@code null} if such account does not exist.
     */
    LocalAccount getLocalAccount(Person person, String subId) throws RemoteException;

    /**
     * Creates a new account with a specified identifier if it does not already exist.
     * @param subId account id TODO
     * @return created or existing account.
     */
    boolean createAccount(Person person, String subId) throws RemoteException;

    RemotePerson getRemotePerson(String passport) throws RemoteException;

    LocalPerson getLocalPerson(String passport) throws RemoteException;

    Set<String> getPersonAccounts(Person person) throws RemoteException;

    boolean createPerson(String firstName, String lastName, String passport) throws RemoteException;
}