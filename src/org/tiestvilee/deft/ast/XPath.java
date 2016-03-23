package org.tiestvilee.deft.ast;

public class XPath extends Node {
    public final String xPath;

    public static XPath xpath(String xpath) {
        return new XPath(xpath);
    }

    public XPath(String xPath) {this.xPath = xPath;}

    public <T> T visit(NodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
