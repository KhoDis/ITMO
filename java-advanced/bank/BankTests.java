package info.kgeorgiy.ja.khodzhayarov.bank;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class BankTests {
    private static Bank bank;
    private static final int BANK_PORT = 8080;
    private static Registry registry;

    @BeforeClass
    public static void initBank() throws RemoteException, NotBoundException {
        registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        registry.rebind("//localhost/bank", new RemoteBank(8080));
        bank = (Bank) registry.lookup("//localhost/bank");

        System.out.println("Bank created");
    }

    @Before
    public void createBank() {
        // TODO: change a bit
        bank = new RemoteBank(BANK_PORT);
        try {
            UnicastRemoteObject.exportObject(bank, BANK_PORT);
            Naming.rebind("//localhost/bank", bank);
        } catch (RemoteException e) {
            System.err.println("Couldn't export object: " + e.getMessage());
        } catch (MalformedURLException e) {
            System.err.println("Malformed URL: " + e.getMessage());
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    @After
    public void unexportBank() {
        try {
            UnicastRemoteObject.unexportObject(bank, true);
        } catch (NoSuchObjectException e) {
            System.err.println("Unexporting object not found " + e.getMessage());
        }
    }


    @Test
    public void test_nonExistentPerson() throws RemoteException {
        for (String passport : new String[]{"whom", "inch", "stem", "anybody", "offense"}) {
            assertNull(bank.getLocalPerson(passport));
        }
    }

    @Test
    public void test_createPerson() throws RemoteException {
        assertTrue(bank.createPerson("father", "victory", "N21oTXM"));

        assertTrue(bank.createPerson("father", "victory", "3c7ySY7O5"));
        assertFalse(bank.createPerson("address", "obedient", "N21oTXM"));

        assertFalse(bank.createPerson(null, "gain", "r1360"));
        assertFalse(bank.createPerson("delight", null, "eOTDQimJ"));
        assertFalse(bank.createPerson("operate", "loaf", null));
    }

    @Test
    public void test_nonExistentAccountOnLocalPerson() throws RemoteException {
        String passport = "N21oTXM";
        assertTrue(bank.createPerson("father", "victory", passport));
        LocalPerson localPerson = bank.getLocalPerson(passport);

        assertNull(bank.getLocalAccount(localPerson, "iw2r1"));
        assertNull(bank.getLocalAccount(localPerson, "y6j82Ca"));
    }

    @Test
    public void test_nonExistentAccountOnRemotePerson() throws RemoteException {
        String passport = "N21oTXM";
        assertTrue(bank.createPerson("father", "victory", passport));
        RemotePerson remotePerson = bank.getRemotePerson(passport);

        assertNull(bank.getLocalAccount(remotePerson, "iw2r1"));
    }

    @Test
    public void test_createAndFind() throws RemoteException {
        LocalPerson person = Generator.createPerson(123);
        assertTrue(bank.createPerson(person.getFirstName(), person.getLastName(), person.getPassport()));
        assertEqualPersons(
                person,
                bank.getRemotePerson(person.getPassport())
        );
    }

    @Test
    public void test_createsAndFinds() throws RemoteException {
        for (int i = 0; i < 10; i++) {
            LocalPerson person = Generator.createPerson(i * 100);
            assertTrue(bank.createPerson(person.getFirstName(), person.getLastName(), person.getPassport()));
            assertEqualPersons(person, bank.getRemotePerson(person.getPassport()));
        }
    }

    @Test
    public void test_accountCreation() throws RemoteException {
        LocalPerson person = Generator.createPerson(5151);
        assertTrue(bank.createPerson(person.getFirstName(), person.getLastName(), person.getPassport()));
        Set<String> expected = IntStream.range(0, 20).mapToObj(Integer::toString).collect(Collectors.toSet());
        for (int i = 0; i < 20; i++) {
            bank.createAccount(person, "" + i);
        }
        Set<String> actual = bank.getPersonAccounts(person);
        assertEquals(expected.size(), actual.size());
        assertTrue(expected.containsAll(actual));
    }

    @Test
    public void test_connectionsRemoteLocal() throws RemoteException {
        LocalPerson expected = Generator.createPerson(734);
        assertTrue(bank.createPerson(expected.getFirstName(), expected.getLastName(), expected.getPassport()));
        LocalPerson actual = bank.getLocalPerson(expected.getPassport());
        assertEqualPersons(expected, actual);
        assertTrue(bank.createAccount(actual, "123"));

        LocalPerson local = bank.getLocalPerson(actual.getPassport());
        RemotePerson remote = bank.getRemotePerson(actual.getPassport());
        Set<String> localAccounts = bank.getPersonAccounts(local);
        Set<String> remoteAccounts = bank.getPersonAccounts(remote);

        assertEquals(1, remoteAccounts.size());
        assertEquals(1, localAccounts.size());
        assertTrue(localAccounts.containsAll(remoteAccounts));
    }

    private static void assertEqualPersons(Person a, Person b) throws RemoteException {
        assertEquals(a.getLastName(), b.getLastName());
        assertEquals(a.getFirstName(), b.getFirstName());
        assertEquals(a.getPassport(), b.getPassport());
    }
}
