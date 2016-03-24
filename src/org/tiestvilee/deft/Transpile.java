package org.tiestvilee.deft;

import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.parser.Result;
import org.tiestvilee.deft.ast.Node;
import org.tiestvilee.deft.ast.Tag;
import org.tiestvilee.deft.grammar.DeftGrammar;
import org.tiestvilee.deft.transforms.ExchangeTagNames;
import org.tiestvilee.deft.transforms.ExchangeXPathAndAttribute;
import org.tiestvilee.deft.transforms.Production;
import org.tiestvilee.deft.xslt.XsltToAst;

import java.util.List;

import static com.googlecode.totallylazy.Sequences.sequence;
import static java.util.Arrays.asList;

public class Transpile {

    private static List<Production> productions = asList(
        new ExchangeTagNames("xsl:template", "deft"),
        new ExchangeXPathAndAttribute("deft", "match"),
        new ExchangeTagNames("xsl:value-of", "value-of"),
        new ExchangeXPathAndAttribute("value-of", "select"),
        new ExchangeXPathAndAttribute("xsl:if", "test"),
        new ExchangeTagNames("xsl:choose", "choose"),
        new ExchangeTagNames("xsl:when", "when"),
        new ExchangeXPathAndAttribute("when", "test"),
        new ExchangeTagNames("xsl:otherwise", "otherwise"),
        new ExchangeXPathAndAttribute("xsl:for-each", "select")
    );
    private static final Sequence<Production> productionsTowardsDeft = sequence(productions);
    private static final Sequence<Production> productionsTowardsXslt = sequence(productions).reverse();

    public static String transpileToDeft(String xslt) throws Exception {
        Tag deft = productionsTowardsDeft.foldLeft(
            (Tag) XsltToAst.transpileToDeftAst(xslt),
            (t, production) -> production.towardsDeft(t));
        return DeftSerialiser.serialise(deft);
    }

    public static String transpileToXslt(String deft) {
        Result<Node> result = DeftGrammar.tag.parse(deft);
        if (result.failure()) {
            throw new RuntimeException(result.message() + "\nremainaing:" + result.remainder());
        }
        Tag toXslt = productionsTowardsXslt.foldLeft(
            (Tag) result.option().get(),
            (t, production) -> production.towardsXslt(t));
        return new XsltSerialiser().visit(toXslt);
    }

}
