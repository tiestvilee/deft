package org.tiestvilee.deft.transforms;

import org.tiestvilee.deft.ast.Node;
import org.tiestvilee.deft.ast.Tag;

import static com.googlecode.totallylazy.Sequences.sequence;
import static org.tiestvilee.deft.ast.Tag.tag;

public class RenameTag extends NodeVisitorAdapter {
    private final String originalTag;
    private final String newTag;

    public RenameTag(String originalTag, String newTag) {
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
        return super.visit(tag);
    }
}
