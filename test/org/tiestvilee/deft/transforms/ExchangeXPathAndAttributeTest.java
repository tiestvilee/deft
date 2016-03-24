package org.tiestvilee.deft.transforms;

import org.junit.Test;
import org.tiestvilee.deft.ast.Node;
import org.tiestvilee.deft.ast.Tag;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.tiestvilee.deft.ast.Attribute.attr;
import static org.tiestvilee.deft.ast.Tag.tag;
import static org.tiestvilee.deft.ast.Text.text;
import static org.tiestvilee.deft.ast.XPath.xpath;

public class ExchangeXPathAndAttributeTest {
    ExchangeXPathAndAttribute exchangeXPathAndAttribute = new ExchangeXPathAndAttribute("matchTag", "select");

    @Test
    public void does_nothing_when_nothing_to_do() throws Exception {
        Tag ast = tag("aTag", attr("anAttr", text("aValue")), attr("anotherAttr", text("anotherValue")), tag("child"));

        assertThat(exchangeXPathAndAttribute.towardsDeft(ast), is((Node) ast));
        assertThat(exchangeXPathAndAttribute.towardsXslt(ast), is((Node) ast));
    }

    @Test
    public void transform_appropriate_attribute_into_xpath_and_back() throws Exception {
        Tag originalAst = tag("matchTag", attr("select", text("xpath string")), attr("anotherAttr", text("anotherValue")), tag("child"));
        Tag updatedAst = tag("matchTag", xpath("xpath string"), attr("anotherAttr", text("anotherValue")), tag("child"));

        assertThat(exchangeXPathAndAttribute.towardsDeft(originalAst), is((Node) updatedAst));
        assertThat(exchangeXPathAndAttribute.towardsXslt(updatedAst), is((Node) originalAst));
    }

    @Test
    public void transform_tag_specific_attribute_into_xpath_and_back() throws Exception {
        Tag originalAst = tag("superTag",
            tag("matchTag",
                attr("select", text("xpath string")),
                tag("child", attr("select", text("xpath string")))),
            tag("doNotMatch", attr("select", text("xpath string"))));
        Tag updatedAst = tag("superTag",
            tag("matchTag",
                xpath("xpath string"),
                tag("child", attr("select", text("xpath string")))),
            tag("doNotMatch", attr("select", text("xpath string"))));

        assertThat(exchangeXPathAndAttribute.towardsDeft(originalAst), is((Node) updatedAst));
        assertThat(exchangeXPathAndAttribute.towardsXslt(updatedAst), is((Node) originalAst));
    }

}