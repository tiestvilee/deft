package org.tiestvilee.deft;

import org.junit.Test;
import org.tiestvilee.deft.grammar.DeftGrammar;
import org.tiestvilee.deft.grammar.Node;
import org.tiestvilee.deft.grammar.Tag;
import org.tiestvilee.deft.grammar.Text;

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
        assertThat(DeftGrammar.tag.parse("[xsl:hello]").option().get(), is((Node) new Tag("xsl:hello")));
    }

    @Test
    public void can_parse_string_in_tag() throws Exception {
        assertThat(DeftGrammar.tag.parse("[xsl:hello 'a string']").option().get(), is((Node) new Tag("xsl:hello", new Text("a string"))));
    }

    @Test
    public void can_parse_nested_tag() throws Exception {
        assertThat(
            DeftGrammar.tag.parse("[xsl:hello [something]]").option().get(),
            is((Node) new Tag("xsl:hello", new Tag("something"))));
    }
}
