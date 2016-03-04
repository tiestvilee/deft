package org.tiestvilee.deft.xslt;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import static java.lang.String.format;

class DeftSaxHandler extends DefaultHandler {
    private final StringBuilder result;

    public DeftSaxHandler(StringBuilder result) {this.result = result;}

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
}
