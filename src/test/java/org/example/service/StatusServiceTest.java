package org.example.service;

import org.example.model.CatalogModel;
import org.example.model.DigestEntryModel;
import org.example.model.FileEntryModel;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class StatusServiceTest {

    @Test
    void shouldReturnOK() {
        DigestEntryModel digestEntryModel = new DigestEntryModel("SHA256", "abc");
        FileEntryModel fileEntryModel = new FileEntryModel("test.txt", Collections.singletonList(digestEntryModel));
        CatalogModel catalogModel = new CatalogModel(Collections.singletonList(fileEntryModel));

        StatusService service = new StatusService(catalogModel);

        Map<String, String> fileToDigest = new HashMap<>();
        fileToDigest.put("test.txt", "abc");

        Map<String, Status> result = service.analyze(fileToDigest, "SHA256");

        assertEquals(Status.OK, result.get("test.txt"));
    }

    @Test
    void shouldReturnNotOK() {
        DigestEntryModel digestEntryModel = new DigestEntryModel("SHA256", "abc");
        FileEntryModel fileEntryModel = new FileEntryModel("test.txt", Collections.singletonList(digestEntryModel));
        CatalogModel catalogModel = new CatalogModel(Collections.singletonList(fileEntryModel));

        StatusService service = new StatusService(catalogModel);

        Map<String, String> fileToDigest = new HashMap<>();
        fileToDigest.put("test.txt", "different");

        Map<String, Status> result = service.analyze(fileToDigest, "SHA256");

        assertEquals(Status.NOT_OK, result.get("test.txt"));
    }

    @Test
    void shouldReturnNotFoundFile() {
        CatalogModel catalogModel = new CatalogModel(new ArrayList<>());

        StatusService service = new StatusService(catalogModel);

        Map<String, String> fileToDigest = new HashMap<>();
        fileToDigest.put("test.txt", "abc");

        Map<String, Status> result = service.analyze(fileToDigest, "SHA256");

        assertEquals(Status.NOT_FOUND, result.get("test.txt"));
    }

    @Test
    void shouldReturnNotFoundDigest() {
        FileEntryModel fileEntryModel = new FileEntryModel("test.txt", new ArrayList<>());
        CatalogModel catalogModel = new CatalogModel(Collections.singletonList(fileEntryModel));

        StatusService service = new StatusService(catalogModel);

        Map<String, String> fileToDigest = new HashMap<>();
        fileToDigest.put("test.txt", "different");

        Map<String, Status> result = service.analyze(fileToDigest, "SHA256");

        assertEquals(Status.NOT_FOUND, result.get("test.txt"));
    }

    @Test
    void shouldReturnCollisionFolder() {
        CatalogModel catalogModel = new CatalogModel(new ArrayList<>());

        StatusService service = new StatusService(catalogModel);

        Map<String, String> fileToDigest = new HashMap<>();
        fileToDigest.put("test1.txt", "abc");
        fileToDigest.put("test2.txt", "abc");

        Map<String, Status> result = service.analyze(fileToDigest, "SHA256");

        assertEquals(Status.COLLISION, result.get("test1.txt"));
        assertEquals(Status.COLLISION, result.get("test2.txt"));
    }

    @Test
    void shouldReturnCollisionCatalog() {
        DigestEntryModel digestEntryModel = new DigestEntryModel("SHA256", "abc");
        FileEntryModel fileEntryModel = new FileEntryModel("test1.txt", Collections.singletonList(digestEntryModel));
        CatalogModel catalogModel = new CatalogModel(Collections.singletonList(fileEntryModel));

        StatusService service = new StatusService(catalogModel);

        Map<String, String> fileToDigest = new HashMap<>();
        fileToDigest.put("test2.txt", "abc");

        Map<String, Status> result = service.analyze(fileToDigest, "SHA256");

        assertEquals(Status.COLLISION, result.get("test2.txt"));
    }
}