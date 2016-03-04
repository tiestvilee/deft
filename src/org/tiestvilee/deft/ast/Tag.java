package org.tiestvilee.deft.ast;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class Tag extends Node {
    public final String tagName;
    public final List<Node> children;

    public Tag(String tagName, Node... children) {
        this.tagName = tagName;
        this.children = asList(children);
    }

    public Tag(String tagName, List<Node> children) {
        this.tagName = tagName;
        this.children = children;
    }

    public <T> T visit(NodeVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public Tag append(Node newNode) {
        List<Node> newChildren = new ArrayList<>(children);
        newChildren.add(newNode);
        return new Tag(tagName, newChildren);
    }
}
