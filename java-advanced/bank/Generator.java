package info.kgeorgiy.ja.khodzhayarov.bank;

import java.util.HashMap;
import java.util.Random;

public class Generator {
    public static LocalPerson createPerson(int seed) {
        return new LocalPerson(getString(seed), getString(seed + 9523 ^ 123), getString(seed * 414 ^ 523), new HashMap<>());
    }

    private static String getString(int seed) {
        return seed + "-" + Long.toHexString(Double.doubleToLongBits(new Random(seed).nextInt()));
    }
}
