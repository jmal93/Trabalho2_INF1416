package org.example.model;

import java.util.List;

public class CatalogModel {
    private List<FileEntryModel> fileEntryList;

    public CatalogModel(List<FileEntryModel> fileEntryList) {
        this.fileEntryList = fileEntryList;
    }

    public List<FileEntryModel> getFileEntryList() {
        return fileEntryList;
    }
}
