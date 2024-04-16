package info.kgeorgiy.ja.khodzhayarov.bank;

import info.kgeorgiy.ja.khodzhayarov.bank.Account;

import java.io.Serializable;
import java.rmi.RemoteException;

public class LocalAccount implements Account, Serializable {
    private final String id;
    private int amount;

    public LocalAccount(String id, int amount) {
        this.id = id;
        this.amount = amount;
    }

    @Override
    public String getId() throws RemoteException {
        return id;
    }

    @Override
    public int getAmount() throws RemoteException {
        return amount;
    }

    @Override
    public void setAmount(int amount) throws RemoteException {
        this.amount = amount;
    }
}
