package org.example;
import java.security.*;
import javax.crypto.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: java Main text");
            System.exit(1);
        }

        byte[] plainText = args[0].getBytes("UTF8");
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        System.out.println("\n" + messageDigest.getProvider().getInfo());

        messageDigest.update(plainText);
        byte[] digest = messageDigest.digest();
        System.out.println("\nDigest length: " + digest.length * 8 + " bits");

        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < digest.length; i++) {
            String.hex = Integer.toHexString(0x0100 + (digest[i] & 0x00FF)).substring(i);
            buffer.append((hex.length() < 2 ? "0" : "") + hex);
        }

        System.out.println("\nDigest(hex):");
        System.out.println(buffer.toString());
    }
}