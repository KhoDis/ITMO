package info.kgeorgiy.ja.khodzhayarov.walk;

import info.kgeorgiy.ja.khodzhayarov.walk.walker.SimpleWalker;
import info.kgeorgiy.ja.khodzhayarov.walk.walker.Walker;
import info.kgeorgiy.ja.khodzhayarov.walk.walker.WalkerException;
import info.kgeorgiy.ja.khodzhayarov.walk.walker.hasher.SHA1Hasher;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;


public class Walk {
    public static void main(String[] args) {
        if (validateArgs(args)) {
            // :NOTE: err, \n - fixed
            System.err.println("Wrong argument amount.");
            System.err.println("Usage: 'inputFileName' 'outputFileName'");
            return;
        }

        Path inputFile = getPath(args[0]);
        // :NOTE: code style - fixed
        if (inputFile == null) {
            return;
        }

        Path outputFile = getPath(args[1]);
        if (outputFile == null) {
            return;
        }

        Walker walker = new SimpleWalker(inputFile, outputFile, new SHA1Hasher());
        try {
            walker.walk();
        } catch (WalkerException e) {
            // :NOTE: err, \n, redundant reflection - fixed
            System.err.println("Couldn't walk fully: " + e.getMessage());
        }
    }

    private static boolean validateArgs(String[] args) {
        return args == null || args.length != 2 || args[0] == null || args[1] == null;
    }

    private static Path getPath(String fileName) {
        try {
            return Path.of(fileName);
        } catch (InvalidPathException e) {
            System.err.println("Can't convert file name to path '" + fileName + "': " + e.getMessage());
            return null;
        }
    }
}