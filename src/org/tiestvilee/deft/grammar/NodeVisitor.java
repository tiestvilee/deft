package org.tiestvilee.deft.grammar;

public interface NodeVisitor<T> {
    T visit(Tag tag);

    T visit(Text text);

    T visit(Attribute attribute);

    T visit(Comment comment);
}
