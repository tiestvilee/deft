package org.tiestvilee.deft.xslt;

import org.tiestvilee.deft.ast.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static java.lang.String.format;

class DeftAstSaxHandler extends DefaultHandler implements LexicalHandler {
    private Deque<Tag> tags = new ArrayDeque<>();

    {
        tags.add(new Tag("root"));
    }

    public Tag ast() {
        return (Tag) tags.getFirst().children.get(0);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        List<Node> attributeList = new ArrayList<>();
        for (int i = 0; i < attributes.getLength(); i++) {
            attributeList.add(new Attribute(attributes.getQName(i), new Text(attributes.getValue(i))));
        }
        tags.addFirst(new Tag(qName, attributeList));
    }

    private String stupidEscape(String value) {
        return value.replace("'", "\\'");
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        Tag removed = tags.removeFirst();
        assert removed.tagName.equals(qName);
        tags.addFirst(tags.removeFirst().append(removed));
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String string = new String(ch, start, length);
        if (tags.peek().tagName.equals("xsl:text")) {
            tags.addFirst(tags.removeFirst().append(new Text(stupidEscape(string))));
            return;
        }
        if (string.trim().length() > 0) {
            tags.addFirst(tags.removeFirst().append(new Text(stupidEscape(string))));
        }
    }

    @Override
    public void startDTD(String name, String publicId, String systemId) throws SAXException { }

    @Override
    public void endDTD() throws SAXException {}

    @Override
    public void startEntity(String name) throws SAXException {}

    @Override
    public void endEntity(String name) throws SAXException {}

    @Override
    public void startCDATA() throws SAXException {}

    @Override
    public void endCDATA() throws SAXException {}

    @Override
    public void comment(char[] ch, int start, int length) throws SAXException {
        tags.addFirst(tags.removeFirst().append(new Comment(new String(ch, start, length))));
    }
}
