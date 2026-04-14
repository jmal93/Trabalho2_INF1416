package org.example;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class DigestCalculatorTest {

    @Test
    void shouldThrowWhenArgsAreInsufficient() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> DigestCalculator.validateArgs(new String[]{"SHA256", "pasta"})
        );

        assertEquals("Argumentos insuficientes", exception.getMessage());
    }

    @Test
    void shouldReturnFilesFromValidFolder() throws Exception {
        Path tempDir = Files.createTempDirectory("digest-test");
        Files.createFile(tempDir.resolve("a.txt"));
        Files.createFile(tempDir.resolve("b.txt"));

        File[] files = DigestCalculator.getFilesInFolder(tempDir.toString());

        assertNotNull(files);
        assertEquals(2, files.length);
    }

    @Test
    void shouldThrowWhenFolderIsInvalid() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> DigestCalculator.getFilesInFolder("pasta-que-nao-existe")
        );

        assertEquals("Pasta inválida", exception.getMessage());
    }

    @Test
    void shouldRunAndPrintStatusAndUpdateXml() throws Exception {
        Path tempDir = Files.createTempDirectory("digest-run-test");
        Path file = tempDir.resolve("arquivo1.txt");
        Files.write(file, "abc".getBytes(StandardCharsets.UTF_8));

        Path xml = tempDir.resolve("catalog.xml");
        Files.write(
                xml,
                (
                        "<CATALOG>\n" +
                                "</CATALOG>\n"
                ).getBytes(StandardCharsets.UTF_8)
        );

        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try {
            System.setOut(new PrintStream(stdout));

            DigestCalculator.run(new String[]{
                    "SHA256",
                    tempDir.toString(),
                    xml.toString()
            });
        } finally {
            System.setOut(originalOut);
        }

        String output = stdout.toString();

        assertTrue(output.contains("arquivo1.txt SHA256"));
        assertTrue(output.contains("(NOT_FOUND)") || output.contains("(NOT FOUND)"));

        String updatedXml = new String(Files.readAllBytes(xml), StandardCharsets.UTF_8);
        assertTrue(updatedXml.contains("<FILE_NAME>arquivo1.txt</FILE_NAME>"));
        assertTrue(updatedXml.contains("<DIGEST_TYPE>SHA256</DIGEST_TYPE>"));
    }
}