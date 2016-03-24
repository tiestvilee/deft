package org.tiestvilee.deft;

import com.googlecode.totallylazy.Sequence;
import org.tiestvilee.deft.ast.*;

import static com.googlecode.totallylazy.Sequences.sequence;
import static java.lang.String.format;

public class DeftSerialiser implements NodeVisitor<String> {

    public String visit(Tag tag) {
        Sequence<Node> sequence = sequence(tag.children);
        return format("%s[%s%s]",
            tag.tagName.equals("deft") ? "\n" : "",
            tag.tagName,
            sequence.map(node -> " " + node.visit(this)).toString(""));
    }

    public String visit(Attribute attr) {
        return format("[@%s %s]", attr.tagName, attr.value.visit(this));
    }

    public String visit(Text text) {
        return format("'%s'", text.string.replace("'", "\\'"));
    }

    public String visit(Comment comment) {
        return format("{%s}", comment.string.replace("}", "\\}"));
    }

    @Override
    public String visit(XPath xPath) {
        return format("`%s`", xPath.xPath.replace("`", "\\`"));
    }
}
