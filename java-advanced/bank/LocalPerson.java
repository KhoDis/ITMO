package info.kgeorgiy.ja.khodzhayarov.bank;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Map;

public class LocalPerson implements Person, Serializable {
    private final String firstName;
    private final String lastName;
    private final String passport;
    private final Map<String, LocalAccount> accountBySubId;

    public LocalPerson(String firstName, String lastName, String passportId, Map<String, LocalAccount> accounts) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.passport = passportId;
        this.accountBySubId = accounts;
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

    public LocalAccount getAccount(String subId) {
        return accountBySubId.get(subId);
    }
}
