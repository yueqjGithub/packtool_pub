package com.avalon.packer.utils;

import org.springframework.beans.factory.annotation.Value;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

public class XmlTools {
//    public static void main (String[] args) throws Exception {
//        XmlTools.setXml(
//                "appID1",
//                "5555",
//                "test.ipa",
//                "sdfsfasdfsad"
//        );
//        FileUtils.CopyFile("./src/public/ipa_metadata.xml", "./src/public/ipa_metadata1.xml");
//    }

    public static String SetXml (
            String appleId,
            String fileSize,
            String fileName,
            String md5Str,
            String metaFilePath
    ) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        File f = new File(metaFilePath);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(f);
        // appID注入
        NodeList appIdNode = doc.getElementsByTagName("software_assets");
        NamedNodeMap namedNodeMap = appIdNode.item(0).getAttributes();
        Node appId = namedNodeMap.getNamedItem("apple_id");
        appId.setTextContent(appleId);
        // fileSize等注入
        Node sizeNode = doc.getElementsByTagName("size").item(0);
        sizeNode.setTextContent(fileSize);

        Node nameNode = doc.getElementsByTagName("file_name").item(0);
        nameNode.setTextContent(fileName);

        Node md5Node = doc.getElementsByTagName("checksum").item(0);
        md5Node.setTextContent(md5Str);

        // 创建 TransformerFactory 对象
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        // 创建 Transformer 对象
        Transformer transformer = transformerFactory.newTransformer();
        // 创建 DOMSource 对象
        DOMSource domSource = new DOMSource(doc);
        // 创建 StreamResult 对象
        StreamResult reStreamResult = new StreamResult(f);
        transformer.transform(domSource, reStreamResult);

        return f.getPath();
    }
}
