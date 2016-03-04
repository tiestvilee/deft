package org.tiestvilee.deft.xslt;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static java.lang.String.format;

public class XsltDeftSerialiser {
    public static String transpileToDeft(String xslt) throws ParserConfigurationException, SAXException, IOException {
        StringBuilder result = new StringBuilder();

        SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
        saxParser.setProperty("http://xml.org/sax/properties/lexical-handler", new CommentExtractingLexicalHandler(result));
        saxParser.parse(
            new ByteArrayInputStream(xslt.getBytes()),
            new DeftSaxHandler(result)
        );
        return result.toString().trim();
    }

}
