package org.tiestvilee.deft.transforms;

import org.tiestvilee.deft.ast.*;

import static com.googlecode.totallylazy.Sequences.sequence;
import static org.tiestvilee.deft.ast.Tag.tag;

public class Rename implements NodeVisitor<Node> {
    private final String originalTag;
    private final String newTag;

    public Rename(String originalTag, String newTag) {
        this.originalTag = originalTag;
        this.newTag = newTag;
    }

    public Node transform(Node tree) {
        return tree.visit(this);
    }

    @Override
    public Node visit(Tag tag) {
        if (tag.tagName.equals(originalTag)) {
            return tag(newTag,
                sequence(tag.children)
                    .map(node -> node.visit(this))
                    .toList());
        }
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
}
