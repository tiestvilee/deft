package org.tiestvilee.deft;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.tiestvilee.deft.Transpile.*;

public class DeftGrammarTest {

    @Test
    public void can_parse_tagname() throws Exception {
        assertThat(tagName.parse("hello").option().get(), is("hello"));
        assertThat(tagName.parse("hel]lo").remainder().toString(), is("]lo"));
    }

    @Test
    public void can_parse_simple_tag() throws Exception {
        assertThat(tag.parse("[xsl:hello]").option().get(), is(new Tag("xsl:hello")));
    }

    @Test
    public void can_parse_nested_tag() throws Exception {
        assertThat(
            tag.parse("[xsl:hello [something]]").option().get(),
            is(new Tag("xsl:hello", new Tag("something"))));
    }
}
