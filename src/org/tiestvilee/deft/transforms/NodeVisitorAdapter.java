package org.tiestvilee.deft.transforms;

import org.tiestvilee.deft.ast.*;

import static com.googlecode.totallylazy.Sequences.sequence;
import static org.tiestvilee.deft.ast.Tag.tag;

public class NodeVisitorAdapter implements NodeVisitor<Node> {
    @Override
    public Node visit(Tag tag) {
        return tag(tag.tagName,
            sequence(tag.children)
                .map(node -> node.visit(this))
                .toList());
    }

    @Override
    public Node visit(Text text) {
        return text;
    }

    @Override
    public Node visit(Attribute attribute) {
        return attribute;
    }

    @Override
    public Node visit(Comment comment) {
        return comment;
    }

    @Override
    public Node visit(XPath xPath) {
        return xPath;
    }
}
