package org.tiestvilee.deft.grammar;

import com.googlecode.totallylazy.Characters;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.parser.Parser;
import com.googlecode.totallylazy.parser.Parsers;
import com.googlecode.totallylazy.parser.ReferenceParser;

import java.util.List;

import static com.googlecode.totallylazy.Characters.whitespace;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.parser.Parsers.*;
import static com.googlecode.totallylazy.parser.Parsers.isChar;
import static java.lang.String.format;

public class DeftGrammar {
    static final ReferenceParser<Node> tagContents = Parsers.reference();

    public static final Parser<Text> textContents = string(Characters.notAmong("'\\")).many().map(text -> new Text(Parsers.toString.apply(text)));
    public static final Parser<Text> text = Parsers.between(isChar('\''), textContents, isChar('\''));
    public static final Parser<String> tagName = string(Characters.notAmong("[] ")).many().map(Parsers.toString);
    public static final Parser<Node> tag = Parsers.between(
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
        DeftGrammar.tagContents.set(Parsers.or(DeftGrammar.tag, DeftGrammar.text));
    }

}
