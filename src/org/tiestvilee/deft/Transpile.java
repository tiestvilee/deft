package org.tiestvilee.deft;

import com.googlecode.totallylazy.parser.Result;
import org.tiestvilee.deft.ast.Node;
import org.tiestvilee.deft.ast.Tag;
import org.tiestvilee.deft.grammar.DeftGrammar;
import org.tiestvilee.deft.transforms.RenameTag;
import org.tiestvilee.deft.xslt.XsltToAst;

public class Transpile {

    public static String transpileToDeft(String xslt) throws Exception {
        return new DeftSerialiser().visit((Tag) new RenameTag("xsl:template", "deft").transform(XsltToAst.transpileToDeftAst(xslt)));
    }

    public static String transpileToXslt(String deft) {
        Result<Node> result = DeftGrammar.tag.parse(deft);
        if (result.failure()) {
            throw new RuntimeException(result.message());
        }
        return new XsltSerialiser().visit((Tag) new RenameTag("deft", "xsl:template").transform(result.option().get()));
    }

}
