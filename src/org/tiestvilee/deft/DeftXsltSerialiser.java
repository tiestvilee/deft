package org.tiestvilee.deft;

import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Xml;
import org.tiestvilee.deft.grammar.*;

import static com.googlecode.totallylazy.Predicates.instanceOf;
import static com.googlecode.totallylazy.Predicates.not;
import static com.googlecode.totallylazy.Sequences.sequence;
import static java.lang.String.format;

public class DeftXsltSerialiser implements NodeVisitor<String> {

    public String visit(Tag tag) {
        Sequence<Node> sequence = sequence(tag.children);
        return format("<%s%s>%s</%s>",
            tag.tagName,
            sequence.filter(instanceOf(Attribute.class)).map(node -> node.visit(this)).toString(""),
            sequence.filter(not(instanceOf(Attribute.class))).map(node -> node.visit(this)).toString(""),
            tag.tagName);
    }

    public String visit(Attribute attr) {
        return format(" %s=\"%s\"", attr.tagName, attr.value.visit(this));
    }

    public String visit(Text text) {
        return Xml.escape(text.string);
    }

    public String visit(Comment comment) {
        return format("<!--%s-->", comment.string);
    }
}
