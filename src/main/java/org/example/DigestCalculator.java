package org.example;

import org.example.model.CatalogModel;
import org.example.service.CatalogService;
import org.example.service.DigestService;
import org.example.service.Status;
import org.example.service.StatusService;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class DigestCalculator {

    public static void main(String[] args) throws Exception {
        run(args);
    }

    static void run(String[] args) throws Exception {
        validateArgs(args);

        String digestType = args[0];
        String folderPath = args[1];
        String xmlFilePath = args[2];

        CatalogService catalogService = new CatalogService();
        DigestService digestService = new DigestService();

        CatalogModel catalogModel = readCatalog(catalogService, xmlFilePath);
        Map<String, String> fileToDigest = buildFileToDigest(folderPath, digestType, digestService);
        Map<String, Status> statuses = analyzeStatuses(catalogModel, fileToDigest, digestType);

        printResults(statuses, fileToDigest, digestType);
        updateCatalogIfNeeded(statuses, fileToDigest, digestType, catalogModel, catalogService, xmlFilePath);
    }

    static void validateArgs(String[] args) {
        if (args.length < 3) {
            System.err.println("Uso: DigestCalculator <Tipo_Digest> <Pasta> <Arquivo_XML>");
            throw new IllegalArgumentException("Argumentos insuficientes");
        }
    }

    static CatalogModel readCatalog(CatalogService catalogService, String xmlFilePath) throws Exception {
        return catalogService.read(xmlFilePath);
    }

    static Map<String, String> buildFileToDigest(
            String folderPath,
            String digestType,
            DigestService digestService
    ) throws IOException, NoSuchAlgorithmException {
        File[] files = getFilesInFolder(folderPath);
        Map<String, String> fileToDigest = new HashMap<String, String>();

        for (File file : files) {
            if (!file.isFile()) {
                continue;
            }

            String fileName = file.getName();
            String digest = digestService.calculateDigestHex(file.getPath(), digestType);
            fileToDigest.put(fileName, digest);
        }

        return fileToDigest;
    }

    static File[] getFilesInFolder(String folderPath) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();

        if (files == null) {
            throw new IllegalArgumentException("Pasta inválida");
        }

        return files;
    }

    static Map<String, Status> analyzeStatuses(
            CatalogModel catalogModel,
            Map<String, String> fileToDigest,
            String digestType
    ) {
        StatusService statusService = new StatusService(catalogModel);
        return statusService.analyze(fileToDigest, digestType);
    }

    static void printResults(
            Map<String, Status> statuses,
            Map<String, String> fileToDigest,
            String digestType
    ) {
        for (Map.Entry<String, Status> entry : statuses.entrySet()) {
            String fileName = entry.getKey();
            String digest = fileToDigest.get(fileName);
            Status status = entry.getValue();

            System.out.println(fileName + " " + digestType + " " + digest + " (" + status + ")");
        }
    }

    static void updateCatalogIfNeeded(
            Map<String, Status> statuses,
            Map<String, String> fileToDigest,
            String digestType,
            CatalogModel catalogModel,
            CatalogService catalogService,
            String xmlFilePath
    ) throws Exception {
        boolean updated = false;

        for (Map.Entry<String, Status> entry : statuses.entrySet()) {
            if (entry.getValue() == Status.NOT_FOUND) {
                String fileName = entry.getKey();
                String digest = fileToDigest.get(fileName);

                catalogService.addDigest(catalogModel, fileName, digestType, digest);
                updated = true;
            }
        }

        if (updated) {
            catalogService.save(catalogModel, xmlFilePath);
        }
    }
}