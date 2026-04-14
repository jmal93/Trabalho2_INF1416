package org.example.model;

import java.util.List;

public class CatalogModel {
    private final List<FileEntryModel> fileEntryList;

    public CatalogModel(List<FileEntryModel> fileEntryList) {
        this.fileEntryList = fileEntryList;
    }

    public List<FileEntryModel> getFileEntryList() {
        return fileEntryList;
    }

    public FileEntryModel findFileEntryByName(String fileName) {
        for (FileEntryModel entry: fileEntryList) {
            if (entry.getFileName().equals(fileName)) {
                return entry;
            }
        }
        return null;
    }

    public void addFileEntry(FileEntryModel fileEntry) {
        fileEntryList.add(fileEntry);
    }
}
