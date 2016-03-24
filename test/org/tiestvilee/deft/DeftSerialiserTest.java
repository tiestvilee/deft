package org.tiestvilee.deft;

import org.junit.Test;
import org.tiestvilee.deft.ast.Attribute;
import org.tiestvilee.deft.ast.Comment;
import org.tiestvilee.deft.ast.Tag;
import org.tiestvilee.deft.ast.Text;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DeftSerialiserTest {


    @Test
    public void writesAstAsDeft() throws Exception {
        assertThat(
            new DeftSerialiser().visit(new Tag("xml")),
            is("[xml]")
        );
        assertThat(
            new DeftSerialiser().visit(new Tag("xml", new Text("hello"))),
            is("[xml 'hello']")
        );
        assertThat(
            new DeftSerialiser().visit(new Tag("xml", new Attribute("attr", new Text("value")), new Text("hello"))),
            is("[xml [@attr 'value'] 'hello']")
        );
        assertThat(
            new DeftSerialiser().visit(new Tag("xml", new Attribute("attr", new Text("value")), new Text("hello"), new Tag("i", new Text("goodbye")))),
            is("[xml [@attr 'value'] 'hello' [i 'goodbye']]")
        );
        assertThat(
            new DeftSerialiser().visit(new Tag("xml", new Attribute("attr", new Text("value")), new Comment("comment"), new Text("hello"), new Tag("i", new Text("goodbye")))),
            is("[xml [@attr 'value'] {comment} 'hello' [i 'goodbye']]")
        );
    }

    @Test
    public void writesAstAsDeftWithEscapeCharacters() throws Exception {
        assertThat(
            new DeftSerialiser().visit(new Tag("xml", new Text("'hello'"))),
            is("[xml '\\'hello\\'']")
        );
        assertThat(
            new DeftSerialiser().visit(new Tag("xml", new Attribute("attr", new Text("'value'")), new Text("hello"))),
            is("[xml [@attr '\\'value\\''] 'hello']")
        );
        assertThat(
            new DeftSerialiser().visit(new Tag("xml", new Attribute("attr", new Text("value")), new Comment("{comment}"), new Text("hello"))),
            is("[xml [@attr 'value'] {{comment\\}} 'hello']")
        );
    }

}