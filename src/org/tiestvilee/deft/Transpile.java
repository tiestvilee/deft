package org.tiestvilee.deft;

import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.parser.Result;
import org.tiestvilee.deft.ast.Node;
import org.tiestvilee.deft.ast.Tag;
import org.tiestvilee.deft.grammar.DeftGrammar;
import org.tiestvilee.deft.transforms.ExchangeXPathAndAttribute;
import org.tiestvilee.deft.transforms.Production;
import org.tiestvilee.deft.transforms.RenameTag;
import org.tiestvilee.deft.xslt.XsltToAst;

import java.util.List;

import static com.googlecode.totallylazy.Sequences.sequence;
import static java.util.Arrays.asList;

public class Transpile {

    private static List<Production> productions = asList(
        new ExchangeXPathAndAttribute("xsl:if", "test"),
        new ExchangeXPathAndAttribute("xsl:for-each", "select")
    );
    private static final Sequence<Production> productionsTowardsDeft = sequence(productions);
    private static final Sequence<Production> productionsTowardsXslt = sequence(productions).reverse();

    public static String transpileToDeft(String xslt) throws Exception {
        Tag deft = (Tag) new RenameTag("xsl:template", "deft").transform(XsltToAst.transpileToDeftAst(xslt));
        deft = productionsTowardsDeft.foldLeft(deft, (t, production) -> production.towardsDeft(t));
        return new DeftSerialiser().visit(deft);
    }

    public static String transpileToXslt(String deft) {
        Result<Node> result = DeftGrammar.tag.parse(deft);
        if (result.failure()) {
            throw new RuntimeException(result.message() + "\nremainaing:" + result.remainder());
        }
        Tag toXslt = (Tag) new RenameTag("deft", "xsl:template").transform(result.option().get());
        toXslt = productionsTowardsXslt.foldLeft(toXslt, (t, production) -> production.towardsXslt(t));
        return new XsltSerialiser().visit(toXslt);
    }

}
