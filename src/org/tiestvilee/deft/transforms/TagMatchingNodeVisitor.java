package org.tiestvilee.deft.transforms;

import org.tiestvilee.deft.ast.Node;
import org.tiestvilee.deft.ast.Tag;

import static com.googlecode.totallylazy.Sequences.sequence;
import static org.tiestvilee.deft.ast.Tag.tag;

public class TagMatchingNodeVisitor extends NodeVisitorAdapter {
    private String tagName;
    protected boolean inMatchingTag;

    public TagMatchingNodeVisitor(String tagName) {this.tagName = tagName;}

    @Override
    public Node visit(Tag tag) {
        inMatchingTag = tag.tagName.equals(tagName);
        return tag(tag.tagName,
            sequence(tag.children)
                .map(node -> node.visit(this))
                .toList());

    }
}
