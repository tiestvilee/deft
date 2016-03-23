package org.tiestvilee.deft.transforms;

import org.junit.Test;
import org.tiestvilee.deft.ast.Node;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.tiestvilee.deft.ast.Attribute.attr;
import static org.tiestvilee.deft.ast.Tag.tag;
import static org.tiestvilee.deft.ast.Text.text;

public class RenameTagTest {

    @Test
    public void does_nothing_when_no_matches() throws Exception {
        Node tree = tag("aTag", tag("anotherTag", tag("aDeeperTag")), tag("aSiblingTag", attr("attr", text("value"))));

        assertThat(
            new RenameTag("missingTag", "somethingElse").transform(tree),
            is(tree));
    }

    @Test
    public void renames_a_tag() throws Exception {
        Node tree = tag("aTag", tag("originalTag", tag("aDeeperTag"), attr("attr", text("value"))));

        assertThat(
            new RenameTag("originalTag", "newTag").transform(tree),
            is(((Node) tag("aTag", tag("newTag", tag("aDeeperTag"), attr("attr", text("value")))))));
    }

}