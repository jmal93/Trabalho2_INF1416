package org.example.model;

import java.util.List;

public class FileEntryModel {
    private String fileName;
    private List<DigestEntryModel> digestEntryList;

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
}
