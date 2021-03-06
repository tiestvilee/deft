package org.tiestvilee.deft.ast;

public class Comment extends Node {
    public final String string;

    public Comment(String string) {
        this.string = string;
    }

    public <T> T visit(NodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
