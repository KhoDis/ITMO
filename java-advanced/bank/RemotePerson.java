package info.kgeorgiy.ja.khodzhayarov.bank;

import info.kgeorgiy.ja.khodzhayarov.bank.Person;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemotePerson extends UnicastRemoteObject implements Person {
    private final String firstName;
    private final String lastName;
    private final String passport;

    public RemotePerson(String firstName, String lastName, String passport, int port) throws RemoteException {
        super(port);
        this.firstName = firstName;
        this.lastName = lastName;
        this.passport = passport;
    }

    @Override
    public String getFirstName() throws RemoteException {
        return firstName;
    }

    @Override
    public String getLastName() throws RemoteException {
        return lastName;
    }

    @Override
    public String getPassport() throws RemoteException {
        return passport;
    }
}
