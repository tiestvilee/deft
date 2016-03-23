package org.tiestvilee.deft;

import com.googlecode.totallylazy.parser.Result;
import org.tiestvilee.deft.ast.Node;
import org.tiestvilee.deft.ast.Tag;
import org.tiestvilee.deft.grammar.DeftGrammar;
import org.tiestvilee.deft.transforms.ExchangeXPathAndAttribute;
import org.tiestvilee.deft.transforms.RenameTag;
import org.tiestvilee.deft.xslt.XsltToAst;

public class Transpile {

    public static String transpileToDeft(String xslt) throws Exception {
        return new DeftSerialiser().visit((Tag)
            new ExchangeXPathAndAttribute("xsl:if", "test").changeAttributeToXPath(
                (Tag) new RenameTag("xsl:template", "deft").transform(XsltToAst.transpileToDeftAst(xslt))
            ));
    }

    public static String transpileToDeftOld(String xslt) throws Exception {
        return new DeftSerialiser().visit(
            (Tag) new RenameTag("xsl:template", "deft").transform(XsltToAst.transpileToDeftAst(xslt))
        );
    }

    public static String transpileToXslt(String deft) {
        Result<Node> result = DeftGrammar.tag.parse(deft);
        if (result.failure()) {
            throw new RuntimeException(result.message() + "\nremainaing:" + result.remainder());
        }
        return new XsltSerialiser().visit((Tag)
            new ExchangeXPathAndAttribute("xsl:if", "test").changeXPathToAttribute(
                (Tag) new RenameTag("deft", "xsl:template").transform(result.option().get())
            ));
    }

}
