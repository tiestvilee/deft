package org.tiestvilee.deft;

import org.junit.Test;

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
        assertThat(DeftGrammar.tag.parse("[xsl:hello]").option().get(), is(new DeftGrammar.Tag("xsl:hello")));
    }

    @Test
    public void can_parse_string_in_tag() throws Exception {
        assertThat(DeftGrammar.tag.parse("[xsl:hello 'a string']").option().get(), is(new DeftGrammar.Tag("xsl:hello", new DeftGrammar.Text("a string"))));
    }

    @Test
    public void can_parse_nested_tag() throws Exception {
        assertThat(
            DeftGrammar.tag.parse("[xsl:hello [something]]").option().get(),
            is(new DeftGrammar.Tag("xsl:hello", new DeftGrammar.Tag("something"))));
    }
}
