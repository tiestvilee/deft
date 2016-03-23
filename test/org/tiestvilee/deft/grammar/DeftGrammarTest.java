package org.tiestvilee.deft.grammar;

import com.googlecode.totallylazy.parser.Result;
import junit.framework.Assert;
import org.junit.Test;
import org.tiestvilee.deft.ast.*;

import static com.googlecode.totallylazy.Option.some;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DeftGrammarTest {

    @Test
    public void can_parse_tagname() throws Exception {
        assertThat(DeftGrammar.tagName.parse("hello").option().get(), is("hello"));
        assertThat(DeftGrammar.tagName.parse("hel]lo").remainder().toString(), is("]lo"));
    }

    @Test
    public void can_parse_simple_tag() throws Exception {
        assertDeft("[xsl:hello]", new Tag("xsl:hello"));
    }

    @Test
    public void can_parse_string_in_tag() throws Exception {
        assertThat(DeftGrammar.text.parse("'asdfasd'").option(), is(some(new Text("asdfasd"))));
        assertThat(DeftGrammar.text.parse("'asd\\'fgh'").option(), is(some(new Text("asd'fgh"))));

        assertDeft(
            "[xsl:hello 'a string']",
            new Tag("xsl:hello", new Text("a string")));
        assertDeft(
            "[xsl:hello 'an escaped \\' string\\\\']",
            new Tag("xsl:hello", new Text("an escaped ' string\\")));
    }

    @Test
    public void can_parse_comment_in_tag() throws Exception {
        assertDeft(
            "[xsl:hello {\\\\a comment\\}}]",
            new Tag("xsl:hello", new Comment("\\a comment}")));
    }

    @Test
    public void can_parse_xpath_in_tag() throws Exception {
        assertDeft(
            "[xsl:hello `\\\\a path\\``]",
            new Tag("xsl:hello", new XPath("\\a path`")));
    }

    @Test
    public void can_parse_nested_tag() throws Exception {
        assertDeft(
            "[xsl:hello [something]]",
            new Tag("xsl:hello", new Tag("something")));
    }

    @Test
    public void can_parse_attribute_tag() throws Exception {
        assertDeft(
            "[xsl:hello [@something 'of value']]",
            new Tag("xsl:hello", new Attribute("something", new Text("of value"))));
    }

    @Test
    public void doesnt_cope_well_with_unclosed_tags() throws Exception {
        Result<Node> result = DeftGrammar.tag.parse("[xsl:stylesheet [");
        Assert.assertTrue(result.message(), result.failure());
    }

    private void assertDeft(String testString, Tag expectedResult) {
        assertThat(
            DeftGrammar.tag.parse(testString).failure() ? DeftGrammar.tag.parse(testString).message() : "empty",
            DeftGrammar.tag.parse(testString).option(),
            is(some((Node) expectedResult)));
    }
}
