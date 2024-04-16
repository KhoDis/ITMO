package info.kgeorgiy.ja.khodzhayarov.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Account extends Remote {
    // Идентификатор
    String getId() throws RemoteException;

    // Количество денег
    int getAmount() throws RemoteException;

    // Изменить количество денег
    void setAmount(int amount) throws RemoteException;
}