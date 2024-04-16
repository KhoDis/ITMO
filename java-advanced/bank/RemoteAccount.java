package info.kgeorgiy.ja.khodzhayarov.bank;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import info.kgeorgiy.ja.khodzhayarov.bank.Account;

public class RemoteAccount extends UnicastRemoteObject implements Account {
    private final String id;
    private int amount;

    public RemoteAccount(String id, int port) throws RemoteException {
        super(port);
        this.id = id;
        this.amount = 0;
    }

    public String getId() {
        return id;
    }
    public int getAmount() {
        return amount;
    }
    public void setAmount(int amount) {
        this.amount = amount;
    }
}