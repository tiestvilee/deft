package org.tiestvilee.deft;

import org.junit.Test;
import org.tiestvilee.deft.ast.Comment;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.tiestvilee.deft.ast.Attribute.attr;
import static org.tiestvilee.deft.ast.Tag.tag;
import static org.tiestvilee.deft.ast.Text.text;
import static org.tiestvilee.deft.ast.XPath.xpath;

public class DeftSerialiserTest {


    private final DeftSerialiser serialiser = new DeftSerialiser();

    @Test
    public void writesAstAsDeft() throws Exception {
        assertThat(
            serialiser.visit(tag("xml")),
            is("[xml]")
        );
        assertThat(
            serialiser.visit(tag("xml", text("hello"))),
            is("[xml 'hello']")
        );
        assertThat(
            serialiser.visit(tag("xml", attr("attr", text("value")), text("hello"))),
            is("[xml [@attr 'value'] 'hello']")
        );
        assertThat(
            serialiser.visit(tag("xml", attr("attr", text("value")), text("hello"), tag("i", text("goodbye")))),
            is("[xml [@attr 'value'] 'hello' [i 'goodbye']]")
        );
        assertThat(
            serialiser.visit(tag("xml", attr("attr", text("value")), new Comment("comment"), text("hello"), tag("i", text("goodbye")))),
            is("[xml [@attr 'value'] {comment} 'hello' [i 'goodbye']]")
        );
    }

    @Test
    public void writesAstAsDeftWithEscapeCharacters() throws Exception {
        assertThat(
            serialiser.visit(tag("xml", text("'hello'"))),
            is("[xml '\\'hello\\'']")
        );
        assertThat(
            serialiser.visit(tag("xml", attr("attr", text("'value'")), text("hello"))),
            is("[xml [@attr '\\'value\\''] 'hello']")
        );
        assertThat(
            serialiser.visit(tag("xml", attr("attr", text("value")), new Comment("{comment}"), text("hello"))),
            is("[xml [@attr 'value'] {{comment\\}} 'hello']")
        );
    }

    @Test
    public void formats_deft_tags_on_next_line() throws Exception {
        assertThat(
            serialiser.visit(tag("stylesheet", tag("deft", attr("name", text("a template"))), tag("deft", attr("name", text("another template"))))),
            is("[stylesheet \n" +
                "[deft [@name 'a template']] \n" +
                "[deft [@name 'another template']]]")
        );
    }

    @Test
    public void formats_when_tags_on_next_line_with_correct_indent() throws Exception {
        assertThat(
            serialiser.visit(tag("stylesheet",
                tag("deft",
                    tag("choose",
                        tag("when", xpath("hello"), tag("choose",
                            tag("when", xpath("sub1"), text("uiop")),
                            tag("when", xpath("sub2"), text("hjkl"))
                        )),
                        tag("when", xpath("goodbye"), text("fdsa")),
                        tag("when", xpath("meh"), text("qwer"))
                    )))),
            is("[stylesheet \n" +
                "[deft [choose \n" +
                "    [when `hello` [choose \n" +
                "        [when `sub1` 'uiop'] \n" +
                "        [when `sub2` 'hjkl']]] \n" +
                "    [when `goodbye` 'fdsa'] \n" +
                "    [when `meh` 'qwer']]]]")
        );
    }

}