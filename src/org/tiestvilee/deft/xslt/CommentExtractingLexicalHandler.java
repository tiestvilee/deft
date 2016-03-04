package org.tiestvilee.deft.xslt;

import com.sun.xml.internal.rngom.xml.sax.AbstractLexicalHandler;
import org.xml.sax.SAXException;

import static java.lang.String.format;

class CommentExtractingLexicalHandler extends AbstractLexicalHandler {
    private final StringBuilder result;

    public CommentExtractingLexicalHandler(StringBuilder result) {this.result = result;}

    @Override
    public void comment(char[] ch, int start, int length) throws SAXException {
        result.append(format(" {%s}", new String(ch, start, length).replace("}", "\\}")));
    }
}
