package org.tiestvilee.deft;

import com.googlecode.totallylazy.parser.Result;
import org.tiestvilee.deft.grammar.DeftGrammar;
import org.tiestvilee.deft.grammar.Node;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

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

        SAXParserFactory.newInstance().newSAXParser().parse(
            new ByteArrayInputStream(xslt.getBytes()),
            new DefaultHandler() {
                @Override
                public InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException {
                    return super.resolveEntity(publicId, systemId);
                }

                @Override
                public void notationDecl(String name, String publicId, String systemId) throws SAXException {
                }

                @Override
                public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException {
                }

                @Override
                public void setDocumentLocator(Locator locator) {
                }

                @Override
                public void startDocument() throws SAXException {
                }

                @Override
                public void endDocument() throws SAXException {
                }

                @Override
                public void startPrefixMapping(String prefix, String uri) throws SAXException {
                }

                @Override
                public void endPrefixMapping(String prefix) throws SAXException {
                }

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
                }

                @Override
                public void processingInstruction(String target, String data) throws SAXException {
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
