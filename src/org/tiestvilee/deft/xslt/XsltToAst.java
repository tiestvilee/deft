package org.tiestvilee.deft.xslt;

import org.tiestvilee.deft.ast.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class XsltToAst {
    public static Node transpileToDeftAst(String xslt) throws ParserConfigurationException, SAXException, IOException {
        SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
        DeftAstSaxHandler deftAstSaxHandler = new DeftAstSaxHandler();
        saxParser.setProperty("http://xml.org/sax/properties/lexical-handler", deftAstSaxHandler);
        saxParser.parse(
            new ByteArrayInputStream(xslt.getBytes()),
            deftAstSaxHandler
        );
        return deftAstSaxHandler.ast();
    }

}
