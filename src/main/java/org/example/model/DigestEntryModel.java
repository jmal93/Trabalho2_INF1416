package org.example.model;

public class DigestEntryModel {
    private String tipoDigest;
    private String digestHex;


    public DigestEntryModel(String tipoDigest, String digestHex) {
        this.tipoDigest = tipoDigest;
        this.digestHex = digestHex;
    }

    public String getTipoDigest() {
        return tipoDigest;
    }

    public String getDigestHex() {
        return digestHex;
    }
}
