package info.kgeorgiy.ja.khodzhayarov.walk.walker.hasher;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA1Hasher implements Hasher {
    public static final int BUFFER_SIZE = 4096;
    public static final String ZEROES = "0".repeat(40);

    @Override
    public String hash(String fileName) throws HasherException {
        Path path = getPath(fileName);

        if (path == null || !Files.isRegularFile(path)) {
            // :NOTE: move to a const - fixed
            return ZEROES;
        }
        try (InputStream reader = Files.newInputStream(path)) {
            return digest(reader);
        } catch (IOException e) {
            throw new HasherException("Unable to open a file to hash", e);
        }
    }

    private String digest(InputStream reader) throws HasherException {
        MessageDigest sha1 = getMessageDigest();
        int n = 0;
        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            while (n != -1) {
                n = reader.read(buffer);
                if (n > 0) {
                    sha1.update(buffer, 0, n);
                }
            }
        } catch (IOException e) {
            throw new HasherException("Unable to read a file to hash", e);
        }
        return bytesToHexString(sha1.digest());
    }

    private Path getPath(String file) {
        try {
            return Path.of(file).normalize();
        } catch (InvalidPathException e) {
            return null;
        }
    }

    private String bytesToHexString(byte[] hash) {
        return String.format("%0" + (hash.length << 1) + "x", new BigInteger(1, hash));
    }

    private MessageDigest getMessageDigest() throws HasherException {
        try {
            return MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new HasherException("Hashing algorithm not found", e);
        }
    }
}
