package org.tiestvilee.deft.grammar;

public class Text extends Node {
    public final String string;

    public Text(String string) {
        this.string = string;
    }

    public <T> T visit(NodeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
