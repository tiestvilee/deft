package org.tiestvilee.deft.ast;

public class Attribute extends Node {
    public final String tagName;
    public final Node value;

    public static Attribute attr(String tagName, Node value) {
        return new Attribute(tagName, value);
    }

    public Attribute(String tagName, Node value) {
        this.tagName = tagName;
        this.value = value;
    }

    public <T> T visit(NodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
