package org.example.service;

import static org.junit.jupiter.api.Assertions.*;

import org.example.model.CatalogModel;
import org.example.model.FileEntryModel;
import org.example.model.DigestEntryModel;
import org.junit.jupiter.api.Test;

class CatalogServiceTest {
    @Test
    void shouldReadValidCatalog() throws Exception {
        CatalogService catalogService = new CatalogService();
        String path = getResourcePath("catalog-valid.xml");

        CatalogModel catalog = catalogService.read(path);

        assertNotNull(catalog);
        assertNotNull(catalog.getFileEntryList());
        assertEquals(2, catalog.getFileEntryList().size());

        FileEntryModel firstFile = catalog.getFileEntryList().get(0);
        assertEquals("Arquivo1.dat", firstFile.getFileName());
        assertEquals(2, firstFile.getDigestEntryList().size());

        DigestEntryModel firstDigest = firstFile.getDigestEntryList().get(0);
        assertEquals("SHA1", firstDigest.getTypeDigest());
        assertEquals("8d901bb3a2840ac030f7dbdd7cb823808858cb2f", firstDigest.getDigestHex());

        DigestEntryModel secondDigest = firstFile.getDigestEntryList().get(1);
        assertEquals("MD5", secondDigest.getTypeDigest());
        assertEquals("42b83991bd1b47b373074111c34fb428", secondDigest.getDigestHex());

        FileEntryModel secondFile = catalog.getFileEntryList().get(1);
        assertEquals("Arquivo2.dat", secondFile.getFileName());
        assertEquals(1, secondFile.getDigestEntryList().size());

        DigestEntryModel thirdDigest = secondFile.getDigestEntryList().get(0);
        assertEquals("SHA256", thirdDigest.getTypeDigest());
        assertEquals("c8db093d264aa744d178470ad97aa64e67e84ab96e3b3310fb6f0eda429e6622", thirdDigest.getDigestHex());
    }

    @Test
    void passFileThatDoesNotExist() throws Exception {
        CatalogService catalogService = new CatalogService();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> catalogService.read("src/test/resources/fail.xml")
        );

        assertEquals("Arquivo não encontrado", exception.getMessage());
    }

    @Test
    void passDirectory() throws Exception {
        CatalogService catalogService = new CatalogService();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> catalogService.read("src/test/resources")
        );

        assertEquals("Caminho não é um arquivo", exception.getMessage());
    }

    @Test
    void tagDoesNotExist() throws Exception {
        CatalogService catalogService = new CatalogService();
        String path = getResourcePath("catalog-invalid-tag-not-exist.xml");

        IllegalArgumentException exception =  assertThrows(
                IllegalArgumentException.class,
                () -> catalogService.read(path)
        );

        assertTrue(exception.getMessage().contains("Tag obrigatória não encontrada"));
    }

    private String getResourcePath(String fileName){
        return getClass()
                .getClassLoader()
                .getResource(fileName)
                .getPath();
    }
}