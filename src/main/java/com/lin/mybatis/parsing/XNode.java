package com.lin.mybatis.parsing;

import org.w3c.dom.CharacterData;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @Author linjiayi5
 * @Date 2023/4/26 16:57:14
 */
public class XNode {

    private final Node node;

    private final XPathParser xpathParser;

    private final String body;

    private final Properties attributes;

    public XNode(XPathParser xpathParser, Node node) {
        this.xpathParser = xpathParser;
        this.node = node;
        this.attributes = parseAttributes(node);
        this.body = parseBody(node);
    }

    public List<XNode> evalNodes(String expression) {
        return xpathParser.evalNodes(node, expression);
    }

    public XNode evalNode(String expression) {
        return xpathParser.evalNode(node, expression);
    }

    public String getStringAttribute(String name) {
        return getStringAttribute(name, (String) null);
    }

    public String getStringAttribute(String name, String def) {
        String value = attributes.getProperty(name);
        return value == null ? def : value;
    }

    public List<XNode> getChildren() {
        List<XNode> children = new ArrayList<>();
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                children.add(new XNode(xpathParser, node));
            }
        }
        return children;
    }

    public Properties getChildrenAsProperties() {
        Properties properties = new Properties();
        for (XNode child : getChildren()) {
            String name = child.getStringAttribute("name");
            String value = child.getStringAttribute("value");
            if (name != null && value != null) {
                properties.setProperty(name, value);
            }
        }
        return properties;
    }

    private Properties parseAttributes(Node node) {
        Properties attributes = new Properties();
        NamedNodeMap attributesNodes = node.getAttributes();
        if (attributesNodes != null) {
            for (int i = 0; i < attributesNodes.getLength(); i++) {
                Node attribute = attributesNodes.item(i);
                attributes.put(attribute.getNodeName(), attribute.getNodeValue());
            }
        }
        return attributes;
    }

    public String getStringBody() {
        return getStringBody(null);
    }

    public String getStringBody(String def) {
        return body == null ? def : body;
    }

    private String parseBody(Node node) {
        String data = getBodyData(node);
        if (data == null) {
            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                data = getBodyData(child);
                if (data != null) {
                    break;
                }
            }
        }
        return data;
    }

    private String getBodyData(Node child) {
        if (child.getNodeType() == Node.CDATA_SECTION_NODE || child.getNodeType() == Node.TEXT_NODE) {
            String data = ((CharacterData) child).getData();
            if (data == null || data.isEmpty()) {
                return "";
            }
            return data;
        }
        return null;
    }

}
