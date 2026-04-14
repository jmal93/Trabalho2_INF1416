package org.example.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigestService {
    public String calculateDigestHex(String filePath, String digestType) throws IOException, NoSuchAlgorithmException {
        File file = new File(filePath);
        validFile(file);

        byte[] bytes = getFileBytes(file);
        String formattedDigestType = mapDigestType(digestType);

        byte[] digestHex = getDigestHex(bytes, formattedDigestType);

        return hexToString(digestHex);
    }

    private void validFile(File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException("Arquivo não encontrado");
        }

        if (!file.isFile()) {
            throw new IllegalArgumentException("Caminho não é um arquivo");
        }
    }

    private byte[] getFileBytes(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] bytes = new byte[(int)file.length()];
        fileInputStream.read(bytes);
        fileInputStream.close();

        return bytes;
    }

    private String mapDigestType(String digestType) {
        switch (digestType) {
            case "MD5":
                return digestType;
            case "SHA1":
                return "SHA-1";
            case "SHA256":
                return "SHA-256";
            case "SHA512":
                return "SHA-512";
            default:
                return "";
        }
    }

    private byte[] getDigestHex(byte[] bytes, String digestType) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(digestType);
        messageDigest.update(bytes);
        return messageDigest.digest();
    }

    private String hexToString(byte[] hex) {
        StringBuilder stringBuilder = new StringBuilder();

        for (byte b: hex) {
            stringBuilder.append(String.format("%02x", b & 0xFF));
        }

        return stringBuilder.toString();
    }
}
