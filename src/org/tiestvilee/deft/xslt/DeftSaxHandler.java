package org.tiestvilee.deft.xslt;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayDeque;
import java.util.Deque;

import static java.lang.String.format;

class DeftSaxHandler extends DefaultHandler {
    private final StringBuilder result;
    private Deque<String> tags = new ArrayDeque<>();

    public DeftSaxHandler(StringBuilder result) {this.result = result;}

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equals("xsl:template")) {
            result.append('\n');
        }
        result.append(format(" [%s", qName));
        for (int i = 0; i < attributes.getLength(); i++) {
            String value = stupidEscape(attributes.getValue(i));
            String attributesQName = attributes.getQName(i);

            result.append(" [@").append(attributesQName).append(" '").append(value).append("']");
        }
        tags.addFirst(qName);
    }

    private String stupidEscape(String value) {
        return value.replace("'", "\\'");
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        result.append("]");
        String removed = tags.removeFirst();
        assert removed.equals(qName);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String string = new String(ch, start, length);
        if (tags.peek().equals("xsl:text")) {
            result.append(" '").append(string).append("'");
            return;
        }
        string = string.trim();
        if (string.length() > 0) {
            result.append(" '").append(stupidEscape(string)).append('\'');
        }
    }
}
