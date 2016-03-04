package org.tiestvilee.deft;

import com.googlecode.totallylazy.parser.Result;
import com.sun.xml.internal.rngom.xml.sax.AbstractLexicalHandler;
import org.tiestvilee.deft.grammar.DeftGrammar;
import org.tiestvilee.deft.grammar.Node;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

import static java.lang.String.format;
import static org.tiestvilee.deft.DeftXsltSerialiser.deftToXslt;

public class Transpile {
    public static String transpileToDeft(String xslt) throws Exception {
        StringBuilder result = new StringBuilder();

        SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
        saxParser.setProperty("http://xml.org/sax/properties/lexical-handler", new AbstractLexicalHandler() {
            @Override
            public void comment(char[] ch, int start, int length) throws SAXException {
                result.append(format(" {%s}", new String(ch, start, length).replace("}", "\\}")));
            }
        });
        saxParser.parse(
            new ByteArrayInputStream(xslt.getBytes()),
            new DefaultHandler() {
                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    result.append(format(" [%s", qName));
                    for (int i = 0; i < attributes.getLength(); i++) {
                        String value = stupidEscape(attributes.getValue(i));
                        String attributesQName = attributes.getQName(i);

                        result.append(" [@").append(attributesQName).append(" '").append(value).append("']");
                    }
                }

                private String stupidEscape(String value) {
                    return value.replace("'", "\\'");
                }

                @Override
                public void endElement(String uri, String localName, String qName) throws SAXException {
                    result.append("]");
                }

                @Override
                public void characters(char[] ch, int start, int length) throws SAXException {
                    result.append(" '").append(stupidEscape(new String(ch, start, length))).append('\'');
                }

                @Override
                public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
                    System.out.println("new String(ch, start, length) = " + new String(ch, start, length));
                }

                @Override
                public void processingInstruction(String target, String data) throws SAXException {
                    System.out.println("target = " + target);
                    System.out.println("data = " + data);
                }
            }
        );
        return result.toString().trim();
    }

    public static String transpileToXslt(String deft) {
        Result<Node> result = DeftGrammar.tag.parse(deft);
        if (result.failure()) {
            throw new RuntimeException(result.message());
        }
        return deftToXslt(result.option().get());
    }

}
