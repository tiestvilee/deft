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
        DeftAstSaxHandler deftAstSaxHandler = new DeftAstSaxHandler();

        SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
        saxParser.setProperty("http://xml.org/sax/properties/lexical-handler", deftAstSaxHandler.lexicalHandler);
        saxParser.parse(
            new ByteArrayInputStream(xslt.getBytes()),
            deftAstSaxHandler
        );
        return deftAstSaxHandler.ast();
    }

}
