package org.example.service;

import org.example.model.FileEntryModel;
import org.example.model.CatalogModel;
import org.example.model.DigestEntryModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CatalogService {
    DocumentBuilder documentBuilder;

    public CatalogService() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilder = documentBuilderFactory.newDocumentBuilder();
    }

    public CatalogModel read(String filePath) throws Exception {
        Document document = loadDocument(filePath);
        return parseFile(document);
    }

    private Document loadDocument(String filePath) throws Exception {
        File file = new File(filePath);
        validDocument(file);
        Document document = documentBuilder.parse(file);
        document.normalize();

        return document;
    }

    private void validDocument(File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException("Arquivo não encontrado");
        }

        if (!file.isFile()) {
            throw new IllegalArgumentException("Caminho não é um arquivo");
        }
    }

    private CatalogModel parseFile(Document document) {
        NodeList fileEntryNodeList = document.getElementsByTagName("FILE_ENTRY");
        List<FileEntryModel> fileEntryList = new ArrayList<FileEntryModel>();

        for (int i = 0; i < fileEntryNodeList.getLength(); i++) {
            Node node = fileEntryNodeList.item(i);
            Element fileEntryElement = (Element) node;
            FileEntryModel fileEntry = parseFileEntry(fileEntryElement);
            fileEntryList.add(fileEntry);
        }

        return new CatalogModel(fileEntryList);
    }

    private FileEntryModel parseFileEntry(Element fileEntryElement) {
        String fileName = getRequiredTagText(fileEntryElement, "FILE_NAME");

        NodeList digestEntryNodes = fileEntryElement.getElementsByTagName("DIGEST_ENTRY");
        List<DigestEntryModel> digestEntryList = new ArrayList<DigestEntryModel>();

        for (int i = 0; i < digestEntryNodes.getLength(); i++) {
            Node node = digestEntryNodes.item(i);
            Element digestEntryElement = (Element) node;
            DigestEntryModel digestEntry = parseDigestEntry(digestEntryElement);
            digestEntryList.add(digestEntry);
        }

        return new FileEntryModel(fileName, digestEntryList);
    }

    private DigestEntryModel parseDigestEntry(Element digestEntryElement) {
        String digestType = getRequiredTagText(digestEntryElement, "DIGEST_TYPE");
        String digestHex = getRequiredTagText(digestEntryElement, "DIGEST_HEX");

        return new DigestEntryModel(digestType, digestHex);
    }

    private String getRequiredTagText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);

        if (nodes.getLength() == 0) {
            throw new IllegalArgumentException("Tag obrigatória não encontrada: " + tagName);
        }

        String text = nodes.item(0).getTextContent();

        if (text == null) {
            throw new IllegalArgumentException("Conteúdo vazio para a tag: " + tagName);
        }

        return text.trim();
    }

    public void addDigest(
            CatalogModel catalogModel,
            String fileName,
            String digestType,
            String digestHex
    ) {
        FileEntryModel fileEntry = catalogModel.findFileEntryByName(fileName);

        if (fileEntry == null) {
            List<DigestEntryModel> digestEntryList = new ArrayList<>();
            digestEntryList.add(new DigestEntryModel(digestType, digestHex));

            FileEntryModel newFileEntry = new FileEntryModel(fileName, digestEntryList);
            catalogModel.addFileEntry(newFileEntry);
            return;
        }

        DigestEntryModel existingDigest = fileEntry.findDigestByType(digestType);

        if (existingDigest == null) {
            fileEntry.addDigestEntry(new DigestEntryModel(digestType, digestHex));
        }
    }

    public void save(CatalogModel catalog, String filePath) throws Exception {
        Document document = documentBuilder.newDocument();

        // raiz <CATALOG>
        Element root = document.createElement("CATALOG");
        document.appendChild(root);

        for (FileEntryModel fileEntry : catalog.getFileEntryList()) {

            // <FILE_ENTRY>
            Element fileEntryElement = document.createElement("FILE_ENTRY");
            root.appendChild(fileEntryElement);

            // <FILE_NAME>
            Element fileNameElement = document.createElement("FILE_NAME");
            fileNameElement.setTextContent(fileEntry.getFileName());
            fileEntryElement.appendChild(fileNameElement);

            // múltiplos DIGEST_ENTRY
            for (DigestEntryModel digestEntry : fileEntry.getDigestEntryList()) {

                Element digestEntryElement = document.createElement("DIGEST_ENTRY");
                fileEntryElement.appendChild(digestEntryElement);

                Element digestTypeElement = document.createElement("DIGEST_TYPE");
                digestTypeElement.setTextContent(digestEntry.getTypeDigest());
                digestEntryElement.appendChild(digestTypeElement);

                Element digestHexElement = document.createElement("DIGEST_HEX");
                digestHexElement.setTextContent(digestEntry.getDigestHex());
                digestEntryElement.appendChild(digestHexElement);
            }
        }

        // escrever no arquivo
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        // formatação (opcional, mas bom)
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(new File(filePath));

        transformer.transform(source, result);
    }
}
