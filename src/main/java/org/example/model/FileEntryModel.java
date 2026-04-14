package org.example.model;

import java.util.List;

public class FileEntryModel {
    private final String fileName;
    private final List<DigestEntryModel> digestEntryList;

    public FileEntryModel(String fileName, List<DigestEntryModel> digestEntryList) {
        this.fileName = fileName;
        this.digestEntryList = digestEntryList;
    }

    public String getFileName() {
        return fileName;
    }

    public List<DigestEntryModel> getDigestEntryList() {
        return digestEntryList;
    }

    public DigestEntryModel findDigestByType(String digestType) {
        for (DigestEntryModel digestEntry: digestEntryList) {
            if (digestEntry.getTypeDigest().equals(digestType)) {
                return digestEntry;
            }
        }

        return null;
    }

    public void addDigestEntry(DigestEntryModel digestEntry) {
        digestEntryList.add(digestEntry);
    }
}
