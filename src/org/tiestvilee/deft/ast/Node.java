package org.tiestvilee.deft.ast;

import org.tiestvilee.deft.util.WithReflectiveToStringEqualsAndHashCode;

public abstract class Node extends WithReflectiveToStringEqualsAndHashCode {
    public abstract <T> T visit(NodeVisitor<T> visitor);
}
