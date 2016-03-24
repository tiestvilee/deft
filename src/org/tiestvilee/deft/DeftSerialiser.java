package org.tiestvilee.deft;

import org.tiestvilee.deft.ast.*;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;

public class DeftSerialiser implements NodeVisitor<String> {

    private List<String> tagsThatGoOnANewline = asList("choose", "when", "otherwise");
    private Deque<String> currentIndent = new ArrayDeque<>();
    private boolean lastWasNewline = false;

    public DeftSerialiser() {
        currentIndent.push("");
    }

    public String visit(Tag tag) {
        String preceedingWhitespace = "";
        String followingWhitespace = "";

        if (tag.tagName.equals("deft")) {
            preceedingWhitespace = newline();
            currentIndent.clear();
            currentIndent.push("    ");
        } else if (tagsThatGoOnANewline.contains(tag.tagName)) {
            preceedingWhitespace = newline() + currentIndent.peek();
            currentIndent.push(currentIndent.peek() + "    ");
        } else {
            if (lastWasNewline) {
                preceedingWhitespace = currentIndent.peek();
                currentIndent.push(currentIndent.peek() + "    ");
            } else {
                preceedingWhitespace = " ";
            }
        }

        StringBuilder builder = new StringBuilder();

        lastWasNewline = false;
        for (Node child : tag.children) {
            builder.append(child.visit(this));
        }

        if (tagsThatGoOnANewline.contains(tag.tagName)) {
            currentIndent.pop();
        }

        lastWasNewline = false;
        if (tag.tagName.equals("deft")) {
            currentIndent.clear();
            currentIndent.push("");
        }

        return format("%s[%s%s]%s",
            preceedingWhitespace,
            tag.tagName,
            builder.toString(),
            followingWhitespace);
    }

    private String newline() {return lastWasNewline ? "" : "\n";}

    public String visit(Attribute attr) {
        String preceedingWhitespace = lastWasNewline ? currentIndent.peek() : " ";
        lastWasNewline = false;
        return format("%s[@%s%s]", preceedingWhitespace, attr.tagName, attr.value.visit(this));
    }

    public String visit(Text text) {
        String preceedingWhitespace = lastWasNewline ? currentIndent.peek() : " ";
        lastWasNewline = false;
        return format("%s'%s'", preceedingWhitespace, text.string.replace("'", "\\'"));
    }

    public String visit(Comment comment) {
        String serialised = format("%s%s{%s}\n", newline(), currentIndent.peek(), comment.string.replace("}", "\\}"));
        lastWasNewline = true;
        return serialised;
    }

    @Override
    public String visit(XPath xPath) {
        String preceedingWhitespace = lastWasNewline ? currentIndent.peek() : " ";
        lastWasNewline = false;
        return format("%s`%s`", preceedingWhitespace, xPath.xPath.replace("`", "\\`"));
    }

    public static String serialise(Tag tag) {
        return new DeftSerialiser().visit(tag).trim();
    }
}
