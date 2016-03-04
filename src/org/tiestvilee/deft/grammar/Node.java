package org.tiestvilee.deft.grammar;

import org.tiestvilee.deft.WithReflectiveToStringEqualsAndHashCode;

public abstract class Node extends WithReflectiveToStringEqualsAndHashCode {
    public abstract <T> T visit(NodeVisitor<T> visitor);
}
