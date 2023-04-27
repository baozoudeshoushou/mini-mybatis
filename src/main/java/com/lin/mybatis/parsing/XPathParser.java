package com.lin.mybatis.parsing;

import com.lin.mybatis.exceptions.MybatisException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author linjiayi5
 * @Date 2023/4/26 16:36:12
 */
public class XPathParser {

    private final Document document;

    private XPath xpath;

    public XPathParser(Reader reader) {
        XPathFactory xPathFactory = XPathFactory.newInstance();
        this.xpath = xPathFactory.newXPath();

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();

            InputSource inputSource = new InputSource(reader);
            this.document = builder.parse(inputSource);
        }
        catch (ParserConfigurationException | IOException | SAXException e) {
            throw new MybatisException("Error creating document instance.", e);
        }
    }

    public List<XNode> evalNodes(Object root, String expression) {
        List<XNode> xnodes = new ArrayList<>();
        NodeList nodes = (NodeList) evaluate(expression, root, XPathConstants.NODESET);
        for (int i = 0; i < nodes.getLength(); i++) {
            xnodes.add(new XNode(this, nodes.item(i)));
        }
        return xnodes;
    }

    public XNode evalNode(String expression) {
        return evalNode(document, expression);
    }

    public XNode evalNode(Object root, String expression) {
        Node node = (Node) evaluate(expression, root, XPathConstants.NODE);
        if (node == null) {
            return null;
        }

        return new XNode(this, node);
    }

    private Object evaluate(String expression, Object root, QName returnType) {
        try {
            return xpath.evaluate(expression, root, returnType);
        }
        catch (Exception e) {
            throw new MybatisException("Error evaluating XPath.  Cause: " + e, e);
        }
    }


}
