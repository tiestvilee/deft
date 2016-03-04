package org.tiestvilee.deft;

import com.googlecode.totallylazy.Characters;
import com.googlecode.totallylazy.parser.Parser;
import com.googlecode.totallylazy.parser.Parsers;
import com.googlecode.totallylazy.parser.ReferenceParser;
import com.googlecode.totallylazy.parser.Result;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import static com.googlecode.totallylazy.parser.Parsers.isChar;
import static com.googlecode.totallylazy.parser.Parsers.string;
import static com.googlecode.totallylazy.parser.Parsers.ws;
import static java.lang.String.format;

public class Transpile {
    public static String transpileToDeft(String xslt) throws Exception {
        StringBuilder result = new StringBuilder();

        SAXParserFactory.newInstance().newSAXParser().parse(
            new ByteArrayInputStream(xslt.getBytes()),
            new DefaultHandler() {
                @Override
                public InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException {
                    return super.resolveEntity(publicId, systemId);
                }

                @Override
                public void notationDecl(String name, String publicId, String systemId) throws SAXException {
                    super.notationDecl(name, publicId, systemId);
                }

                @Override
                public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException {
                    super.unparsedEntityDecl(name, publicId, systemId, notationName);
                }

                @Override
                public void setDocumentLocator(Locator locator) {
                    super.setDocumentLocator(locator);
                }

                @Override
                public void startDocument() throws SAXException {
                    super.startDocument();
                }

                @Override
                public void endDocument() throws SAXException {
                    super.endDocument();
                }

                @Override
                public void startPrefixMapping(String prefix, String uri) throws SAXException {
                    super.startPrefixMapping(prefix, uri);
                }

                @Override
                public void endPrefixMapping(String prefix) throws SAXException {
                    super.endPrefixMapping(prefix);
                }

                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    result.append(format("[%s", qName));
                }

                @Override
                public void endElement(String uri, String localName, String qName) throws SAXException {
                    result.append("]");
                }

                @Override
                public void characters(char[] ch, int start, int length) throws SAXException {
                    super.characters(ch, start, length);
                }

                @Override
                public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
                    super.ignorableWhitespace(ch, start, length);
                }

                @Override
                public void processingInstruction(String target, String data) throws SAXException {
                    super.processingInstruction(target, data);
                }
            }
        );
        return result.toString();
    }

    private static final ReferenceParser<Tag> tagContents = Parsers.reference();

    public static final Parser<String> tagName = string(Characters.notAmong("[] ")).many().map(Parsers.toString);

    public static final Parser<Tag> tag = Parsers.between(
        isChar('['),
        Parsers.tuple(ws(tagName), tagContents.sepBy(isChar(Characters.whitespace).many())),
        isChar(']'))
        .map(tagName -> {
            List<Tag> second = tagName.second();
            return new Tag(tagName.first(), second.toArray(new Tag[second.size()]));
        });

    static {
        tagContents.set(tag);
    }

    public static String transpileToXslt(String deft) {
        Result<Tag> result = tag.parse(deft);
        if (result.failure()) {
            throw new RuntimeException(result.message());
        }
        return result.option().get().toString();
    }

    public static class Tag extends WithReflectiveToStringEqualsAndHashCode {
        public final String tagName;
        private final Tag[] children;

        public Tag(String tagName, Tag... children) {
            this.tagName = tagName;
            this.children = children;
        }
    }
}
