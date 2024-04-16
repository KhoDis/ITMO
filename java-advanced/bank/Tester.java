package info.kgeorgiy.ja.khodzhayarov.bank;

import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class Tester {
    public static void main(String[] args) {
        JUnitCore junit = new JUnitCore();
        junit.addListener(new TextListener(System.out));
        Result result = junit.run(ClientTest.class);
        System.exit(result.wasSuccessful() ? 0 : 1);
    }
}
