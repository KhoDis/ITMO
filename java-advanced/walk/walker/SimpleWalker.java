package info.kgeorgiy.ja.khodzhayarov.walk.walker;

import info.kgeorgiy.ja.khodzhayarov.walk.walker.hasher.Hasher;
import info.kgeorgiy.ja.khodzhayarov.walk.walker.hasher.HasherException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class SimpleWalker implements Walker {
    private final Path inputPath;
    private final Path outputPath;
    private final Hasher hasher;

    public SimpleWalker(Path inputPath, Path outputPath, Hasher hasher) {
        this.inputPath = inputPath;
        this.outputPath = outputPath;
        this.hasher = hasher;
    }

    @Override
    public void walk() throws WalkerException {
        createOutputPath();

        try (BufferedReader reader = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8)) {
            try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
                String file;
                while ((file = reader.readLine()) != null) {
                    writer.write(getHash(file) + " " + file);
                    writer.newLine();
                }
            } catch (IOException e) {
                // :NOTE: verbError - fixed
                throw getException(false, e);
                // :NOTE: securityException - fixed
            }
        } catch (IOException e) {
            throw getException(true, e);
        }
    }

    private WalkerException getException(boolean isInputFile, Exception e) {
        return new WalkerException("I/O exception occurs on the " + (isInputFile ? "input" : "output") + " file", e);
    }

    private void createOutputPath() throws WalkerException {
        try {
            Path parent = outputPath.getParent();
            // :NOTE: redundant exists - fixed
            if (parent != null) {
                Files.createDirectories(parent);
            }
        } catch (IOException e) {
            throw getException(false, e);
        }
    }

    private String getHash(String fileName) throws WalkerException {
        try {
            return hasher.hash(fileName);
        } catch (HasherException e) {
            throw new WalkerException("Unable to hash the file", e);
        }
    }
}
