package org.example;

import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;

public class CatalogService {
    Document document;
    DocumentBuilder documentBuilder;

    public CatalogService() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilder = documentBuilderFactory.newDocumentBuilder();
    }

    private Document loadDocument(String filePath) throws Exception {
        File file = new File(filePath);

        if (!file.exists()) {
            throw new IllegalArgumentException("Arquivo não encontrado: " + filePath);
        }

        if (!file.isFile()) {
            throw new IllegalArgumentException("Caminho não é um arquivo: " + filePath);
        }

        document = documentBuilder.parse(file);

        return document;
    }
}
