package org.tiestvilee.deft;

import org.tiestvilee.deft.ast.*;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import static com.googlecode.totallylazy.Sequences.sequence;
import static java.lang.String.format;
import static java.util.Arrays.asList;

public class DeftSerialiser implements NodeVisitor<String> {

    private List<String> tagsThatIndent = asList("when", "otherwise");
    private Deque<String> indent = new ArrayDeque<>();

    public String visit(Tag tag) {
        String serialised = format("%s[%s%s]",
            preceedingWhitespace(tag),
            tag.tagName,
            sequence(tag.children).map(node -> " " + node.visit(this)).toString(""));
        if (tagsThatIndent.contains(tag.tagName)) {
            indent.pop();
        }
        return serialised;
    }

    private String preceedingWhitespace(Tag tag) {
        if (tag.tagName.equals("deft")) {
            indent.clear();
            indent.push("    ");
            return "\n";
        }
        if (tagsThatIndent.contains(tag.tagName)) {
            String whitespace = "\n" + indent.peek();
            indent.push(indent.peek() + "    ");
            return whitespace;
        }
        return "";
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
