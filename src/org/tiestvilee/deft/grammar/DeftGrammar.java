package org.tiestvilee.deft.grammar;

import com.googlecode.totallylazy.Characters;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.parser.Parse;
import com.googlecode.totallylazy.parser.Parser;
import com.googlecode.totallylazy.parser.Parsers;
import com.googlecode.totallylazy.parser.ReferenceParser;
import org.tiestvilee.deft.ast.*;

import java.util.List;

import static com.googlecode.totallylazy.Characters.whitespace;
import static com.googlecode.totallylazy.parser.Parsers.*;

public class DeftGrammar {
    static final ReferenceParser<Node> tagContents = Parsers.reference();

    private static Parse<String> escapedChar = isChar('\\').next(string(Characters.among("'\\")));
    public static final Parser<Text> textContents = string(Characters.notAmong("'\\")).or(escapedChar).many().map(text -> new Text(Parsers.toString.apply(text)));
    public static final Parser<Text> text = between(isChar('\''), textContents, isChar('\''));

    private static Parse<String> escapedCommentChar = isChar('\\').next(string(Characters.among("}\\")));
    public static final Parser<Comment> commentContents = string(Characters.notAmong("}\\")).or(escapedCommentChar).many().map(text -> new Comment(Parsers.toString.apply(text)));
    public static final Parser<Comment> comment = between(isChar('{'), commentContents, isChar('}'));

    private static Parse<String> escapedXPathChar = isChar('\\').next(string(Characters.among("`\\")));
    public static final Parser<XPath> xPathContents = string(Characters.notAmong("`\\")).or(escapedXPathChar).many().map(text -> new XPath(Parsers.toString.apply(text)));
    public static final Parser<XPath> xPath = between(isChar('`'), xPathContents, isChar('`'));

    public static final Parser<String> tagName = string(Characters.notAmong("[] ")).many().map(Parsers.toString);
    public static final Parser<Node> tag = between(
        isChar('['),
        tuple(ws(tagName), tagContents.sepBy(isChar(whitespace).many())),
        isChar(']'))
        .map(DeftGrammar::makeTag);

    private static Node makeTag(Pair<String, List<Node>> tagDefinition) {
        if (tagDefinition.first().startsWith("@")) {
            return new Attribute(tagDefinition.first().substring(1), tagDefinition.second().get(0));
        }
        return new Tag(tagDefinition.first(), tagDefinition.second());
    }

    static {
        tagContents.set(Parsers.or(tag, text, comment, xPath));
    }

}
