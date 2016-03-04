package org.tiestvilee.deft;

import com.googlecode.totallylazy.Characters;
import com.googlecode.totallylazy.parser.Parser;
import com.googlecode.totallylazy.parser.Parsers;
import com.googlecode.totallylazy.parser.ReferenceParser;

import java.util.List;

import static com.googlecode.totallylazy.Characters.whitespace;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.parser.Parsers.*;
import static com.googlecode.totallylazy.parser.Parsers.isChar;
import static java.lang.String.format;
import static java.util.Arrays.asList;

public class DeftGrammar {
    static final ReferenceParser<Node> tagContents = Parsers.reference();

    public static final Parser<Text> textContents = string(Characters.notAmong("'\\")).many().map(text -> new Text(Parsers.toString.apply(text)));
    public static final Parser<Text> text = Parsers.between(isChar('\''), textContents, isChar('\''));
    public static final Parser<String> tagName = string(Characters.notAmong("[] ")).many().map(Parsers.toString);
    public static final Parser<Tag> tag = Parsers.between(
        isChar('['),
        tuple(ws(tagName), tagContents.sepBy(isChar(whitespace).many())),
        isChar(']'))
        .map(tagDefinition -> new Tag(tagDefinition.first(), tagDefinition.second()));

    static {
        DeftGrammar.tagContents.set(Parsers.or(DeftGrammar.tag, DeftGrammar.text));
    }

    public static String deftToXslt(Node node) {
        if (node instanceof Text) {
            return deftToXslt((Text) node);
        }
        if (node instanceof Tag) {
            return deftToXslt((Tag) node);
        }
        throw new RuntimeException("unknown Node type: " + node.getClass().toString());
    }

    public static String deftToXslt(Tag tag) {
        return format("<%s>%s</%s>", tag.tagName, sequence(tag.children).map(DeftGrammar::deftToXslt), tag.tagName);
    }

    public static String deftToXslt(Text text) {
        return text.string;
    }

    public static class Node extends WithReflectiveToStringEqualsAndHashCode {

    }

    public static class Tag extends Node {
        public final String tagName;
        private final List<Node> children;

        public Tag(String tagName, Node... children) {
            this.tagName = tagName;
            this.children = asList(children);
        }

        public Tag(String tagName, List<Node> children) {
            this.tagName = tagName;
            this.children = children;
        }
    }

    public static class Text extends Node {
        public final String string;

        public Text(String string) {
            this.string = string;
        }
    }
}
