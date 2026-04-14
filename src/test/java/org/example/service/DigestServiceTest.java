package org.example.service;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

class DigestServiceTest {

    @Test
    void calculateDigestMD5() throws IOException, NoSuchAlgorithmException {
        DigestService digestService = new DigestService();
        String smallText = getResourcePath("test-file.txt");
        String bigText = getResourcePath("lorem-ipsum.txt");

        String digest = digestService.calculateDigestHex(smallText, "MD5");
        assertEquals("698dc19d489c4e4db73e28a713eab07b", digest);

        digest = digestService.calculateDigestHex(bigText, "MD5");
        assertEquals("47fd1b99bc06826e73c71960d86b0aae", digest);
    }

    @Test
    void calculateDigestSHA1() throws IOException, NoSuchAlgorithmException {
        DigestService digestService = new DigestService();
        String smallText = getResourcePath("test-file.txt");
        String bigText = getResourcePath("lorem-ipsum.txt");

        String digest = digestService.calculateDigestHex(smallText, "SHA1");
        assertEquals("2e6f9b0d5885b6010f9167787445617f553a735f", digest);

        digest = digestService.calculateDigestHex(bigText, "SHA1");
        assertEquals("735b0ef698bde61dcca93609a8b5ca203c843538", digest);
    }

    @Test
    void calculateDigestSHA256() throws IOException, NoSuchAlgorithmException {
        DigestService digestService = new DigestService();
        String smallText = getResourcePath("test-file.txt");
        String bigText = getResourcePath("lorem-ipsum.txt");

        String digest = digestService.calculateDigestHex(smallText, "SHA256");
        assertEquals("46070d4bf934fb0d4b06d9e2c46e346944e322444900a435d7d9a95e6d7435f5", digest);

        digest = digestService.calculateDigestHex(bigText, "SHA256");
        assertEquals("71ea6689adbb0b5b0a6c5355e2165623e0aed35c0ff4e40c0e4415ae1ba8747a", digest);
    }

    @Test
    void calculateDigestSHA512() throws IOException, NoSuchAlgorithmException {
        DigestService digestService = new DigestService();
        String smallText = getResourcePath("test-file.txt");
        String bigText = getResourcePath("lorem-ipsum.txt");

        String digest = digestService.calculateDigestHex(smallText, "SHA512");
        assertEquals("b123e9e19d217169b981a61188920f9d28638709a5132201684d792b9264271b7f09157ed4321b1c097f7a4abecfc0977d40a7ee599c845883bd1074ca23c4af", digest);

        digest = digestService.calculateDigestHex(bigText, "SHA512");
        assertEquals("a5586b9dc49cd7f1330be956e46d5ffb0005a6f4946de36173fd8f803ff09cf83b53df234436c76a738a4b71dcd5fa0f8e29ec8d3c40dd574ed3962218eb0fcb", digest);
    }

    @Test
    void algorithmNotValid() {
        DigestService digestService = new DigestService();
        String path = getResourcePath("test-file.txt");

        assertThrows(NoSuchAlgorithmException.class, () -> digestService.calculateDigestHex(path, "aaa"));
    }

    @Test
    void fileNotFound() {
        DigestService digestService = new DigestService();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> digestService.calculateDigestHex("src/test/resources/not-exists.txt", "aaa")
        );
        assertTrue(exception.getMessage().contains("Arquivo não encontrado"));
    }

    @Test
    void notAFile() {
        DigestService digestService = new DigestService();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> digestService.calculateDigestHex("src/test/resources", "aaa")
        );
        assertTrue(exception.getMessage().contains("Caminho não é um arquivo"));
    }

    private String getResourcePath(String fileName){
        return getClass()
                .getClassLoader()
                .getResource(fileName)
                .getPath();
    }
}