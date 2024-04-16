package info.kgeorgiy.ja.khodzhayarov.bank;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ClientTests {
    private static final int defaultBankPort = 8888;
    private static Registry registry;
    private static Bank bank = null;

    public static void unexportRegistry(final Registry registry) {
        try {
            UnicastRemoteObject.unexportObject(registry, true);
        } catch (NoSuchObjectException e) {
            System.err.println("Could not unexport object " + e.getMessage());
            e.printStackTrace();
        }
    }

    @BeforeClass
    public static void init() throws RemoteException, NotBoundException {
        registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        registry.rebind("//localhost/bank", new RemoteBank(8080));
        bank = (Bank) registry.lookup("//localhost/bank");

        System.out.println("Bank created");
    }

    @AfterClass
    public static void finish() throws NoSuchObjectException {
        UnicastRemoteObject.unexportObject(bank, true);
        unexportRegistry(registry);
    }

    @Test
    public void test_account() throws RemoteException {
        Person p = Generator.createPerson(111);
        for (int i = 0; i < 20; i++) {
            Client.main(p.getFirstName(), p.getLastName(), p.getPassport(), "inc", "10");
        }
        LocalAccount account = bank.getLocalAccount(p, "inc");
        assertEquals(200, account.getAmount());
    }

    @Test
    public void test_accounts() throws RemoteException {
        int n = 10;
        int times = 20;

        List<LocalPerson> persons = IntStream.range(0, n)
                .mapToObj(i -> Generator.createPerson(i * 100))
                .toList();

        for (int i = 0; i < times; i++) {
            for (LocalPerson p : persons) {
                Client.main(p.getFirstName(), p.getLastName(), p.getPassport(), "person" + i, "" + (i * i));
            }
        }

        for (int i = 0; i < n; i++) {
            LocalAccount account = bank.getLocalAccount(persons.get(i), "person" + i);
            assertEquals((i * i) * times, account.getAmount());
        }
    }
}
