package org.tiestvilee.deft;

import com.googlecode.totallylazy.parser.Result;
import org.tiestvilee.deft.grammar.DeftGrammar;
import org.tiestvilee.deft.grammar.Node;
import org.tiestvilee.deft.grammar.Tag;
import org.tiestvilee.deft.xslt.XsltDeftSerialiser;

public class Transpile {
    public static String transpileToDeft(String xslt) throws Exception {
        return XsltDeftSerialiser.transpileToDeft(xslt);
    }

    public static String transpileToXslt(String deft) {
        Result<Node> result = DeftGrammar.tag.parse(deft);
        if (result.failure()) {
            throw new RuntimeException(result.message());
        }
        return new DeftXsltSerialiser().visit((Tag) result.option().get());
    }

}
