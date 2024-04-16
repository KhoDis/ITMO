package info.kgeorgiy.ja.khodzhayarov.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

public class RemoteBank implements Bank, Remote {
    private final int port;
    private final ConcurrentMap<String, Set<String>> subIdsByPassport;
    private final ConcurrentMap<String, RemotePerson> personByPassport;
    private final ConcurrentMap<String, RemoteAccount> accountById;

    public RemoteBank(int port) {
        this.port = port;
        this.subIdsByPassport = new ConcurrentHashMap<>();
        this.personByPassport = new ConcurrentHashMap<>();
        this.accountById = new ConcurrentHashMap<>();
    }

    @Override
    public boolean createAccount(Person person, String subId) throws RemoteException {
        if (!validatePerson(person) || subId == null) {
            return false;
        }
        final String accountId = generateAccountId(person, subId);
        accountById.putIfAbsent(accountId, new RemoteAccount(accountId, port));
        subIdsByPassport.computeIfAbsent(person.getPassport(), passport -> ConcurrentHashMap.newKeySet()).add(subId);
        if (!personByPassport.containsKey(person.getPassport())) {
            personByPassport.put(person.getPassport(), new RemotePerson(person.getFirstName(), person.getLastName(), person.getPassport(), port));
        }
        return true;
    }

    private boolean validatePerson(Person person) throws RemoteException {
        return Stream.of(person, person.getFirstName(), person.getLastName(), person.getPassport()).noneMatch(Objects::isNull);
    }

    private String generateAccountId(Person person, String subId) throws RemoteException {
        return String.join(":", person.getPassport(), subId);
    }

    @Override
    public RemotePerson getRemotePerson(String passportId) throws RemoteException {
        return tryGetPerson(passportId);
    }

    private RemotePerson tryGetPerson(String passportId) {
        return passportId != null ? personByPassport.get(passportId) : null;
    }

    @Override
    public LocalPerson getLocalPerson(String passport) throws RemoteException {
        Person person = tryGetPerson(passport);

        if (person == null) {
            return null;
        }

        return new LocalPerson(person.getFirstName(),
                person.getLastName(),
                person.getPassport(),
                getLocalAccountBySubId(person));
    }

    private Map<String, LocalAccount> getLocalAccountBySubId(Person person) throws RemoteException {
        ConcurrentHashMap<String, LocalAccount> map = new ConcurrentHashMap<>();
        for (String subId : getPersonAccounts(person)) {
            map.put(subId, getLocalAccount(person, subId));
        }
        return map;
    }

    @Override
    public LocalAccount getLocalAccount(Person person, String subId) throws RemoteException {
        if (person == null || subId == null) {
            return null;
        }

        if (person instanceof LocalPerson localPerson) {
            return localPerson.getAccount(subId);
        }

        RemoteAccount account = accountById.get(generateAccountId(person, subId));
        return new LocalAccount(account.getId(), account.getAmount());
    }

    @Override
    public Set<String> getPersonAccounts(Person person) throws RemoteException {
        return subIdsByPassport.get(person.getPassport());
    }

    @Override
    public boolean createPerson(String firstName, String lastName, String passport) throws RemoteException {
        if (firstName == null || lastName == null || passport == null) {
            return false;
        }

        if (personByPassport.containsKey(passport) || subIdsByPassport.containsKey(passport)) {
            return false;
        }

        personByPassport.put(passport, new RemotePerson(firstName, lastName, passport, port));
        subIdsByPassport.put(passport, ConcurrentHashMap.newKeySet());
        return true;
    }
}