package org.example.service;

import org.example.model.CatalogModel;
import org.example.model.DigestEntryModel;
import org.example.model.FileEntryModel;

import java.util.*;

public class StatusService {
    private final CatalogModel catalogModel;

    public StatusService(CatalogModel catalogModel) {
        this.catalogModel = catalogModel;
    }

    public Map<String, Status> analyze(Map<String, String> fileToDigest, String digestType) {
        Map<String, Status> result = new HashMap<>();
        Map<String, List<String>> digestToFiles = buildDigestToFiles(fileToDigest);

        for (Map.Entry<String, String> entry : fileToDigest.entrySet()) {
            String fileName = entry.getKey();
            String digest = entry.getValue();

            FileEntryModel fileEntryDTO = new FileEntryModel(
                    fileName,
                    Collections.singletonList(new DigestEntryModel(digestType, digest))
            );

            Status status = resolveStatus(fileEntryDTO, digestToFiles);

            result.put(fileEntryDTO.getFileName(), status);
        }

        return result;
    }

    private Status resolveStatus(FileEntryModel fileEntry, Map<String, List<String>> digestToFiles) {
        String digest = fileEntry.getDigestEntryList().get(0).getDigestHex();
        String digestType = fileEntry.getDigestEntryList().get(0).getTypeDigest();

        if (hasCollisionInFolder(digest, digestToFiles) || hasCollisionInCatalog(fileEntry.getFileName(), digest)) {
            return Status.COLLISION;
        }

        FileEntryModel newFileEntry = catalogModel.findFileEntryByName(fileEntry.getFileName());

        if (newFileEntry == null) {
            return Status.NOT_FOUND;
        }

        DigestEntryModel newDigestEntry = newFileEntry.findDigestByType(digestType);

        if (newDigestEntry == null) {
            return Status.NOT_FOUND;
        }

        if (!digest.equals(newDigestEntry.getDigestHex())) {
            return Status.NOT_OK;
        }

        return Status.OK;
    }

    private Map<String, List<String>> buildDigestToFiles(Map<String, String> fileToDigest) {
        Map<String, List<String>> digestToFiles = new HashMap<>();

        for (Map.Entry<String, String> entry : fileToDigest.entrySet()) {
            String fileName = entry.getKey();
            String digest = entry.getValue();

            digestToFiles
                    .computeIfAbsent(digest, k -> new ArrayList<>())
                    .add(fileName);
        }

        return digestToFiles;
    }

    private boolean hasCollisionInFolder(String digest, Map<String, List<String>> digestToFiles) {
        List<String> files = digestToFiles.get(digest);
        return files != null && files.size() > 1;
    }

    private boolean hasCollisionInCatalog(String fileName, String digest) {
        for (FileEntryModel fileEntry : catalogModel.getFileEntryList()) {
            if (fileEntry.getFileName().equals(fileName)) {
                continue;
            }

            for (DigestEntryModel digestEntry : fileEntry.getDigestEntryList()) {
                if (digest.equals(digestEntry.getDigestHex())) {
                    return true;
                }
            }
        }

        return false;
    }

}
