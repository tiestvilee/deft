package org.tiestvilee.deft.xslt;

import org.junit.Test;
import org.tiestvilee.deft.ast.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class XsltToAstTest {

    @Test
    public void parsesXmlIntoAst() throws Exception {
        assertThat(
            XsltToAst.transpileToDeftAst("<xml/>"),
            is((Node) new Tag("xml"))
        );
        assertThat(
            XsltToAst.transpileToDeftAst("<xml>hello</xml>"),
            is((Node) new Tag("xml", new Text("hello")))
        );
        assertThat(
            XsltToAst.transpileToDeftAst("<xml attr='value'>hello</xml>"),
            is((Node) new Tag("xml", new Attribute("attr", new Text("value")), new Text("hello")))
        );
        assertThat(
            XsltToAst.transpileToDeftAst("<xml attr='value'>hello<i>goodbye</i></xml>"),
            is((Node) new Tag("xml", new Attribute("attr", new Text("value")), new Text("hello"), new Tag("i", new Text("goodbye"))))
        );
        assertThat(
            XsltToAst.transpileToDeftAst("<xml attr='value'><!--comment-->hello<i>goodbye</i></xml>"),
            is((Node) new Tag("xml", new Attribute("attr", new Text("value")), new Comment("comment"), new Text("hello"), new Tag("i", new Text("goodbye"))))
        );
    }

    @Test
    public void parsesXmlIntoAstWithEscapeCharacters() throws Exception {
        assertThat(
            XsltToAst.transpileToDeftAst("<xml>'hello'</xml>"),
            is((Node) new Tag("xml", new Text("'hello'")))
        );
        assertThat(
            XsltToAst.transpileToDeftAst("<xml attr=\"'value'\">hello</xml>"),
            is((Node) new Tag("xml", new Attribute("attr", new Text("'value'")), new Text("hello")))
        );
        assertThat(
            XsltToAst.transpileToDeftAst("<xml attr='value'><!--{comment}-->hello<i>goodbye</i></xml>"),
            is((Node) new Tag("xml", new Attribute("attr", new Text("value")), new Comment("{comment}"), new Text("hello"), new Tag("i", new Text("goodbye"))))
        );
    }

}