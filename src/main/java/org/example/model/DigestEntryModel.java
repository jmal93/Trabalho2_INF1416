package org.example.model;

public class DigestEntryModel {
    private final String tipoDigest;
    private final String digestHex;


    public DigestEntryModel(String tipoDigest, String digestHex) {
        this.tipoDigest = tipoDigest;
        this.digestHex = digestHex;
    }

    public String getTypeDigest() {
        return tipoDigest;
    }

    public String getDigestHex() {
        return digestHex;
    }
}
