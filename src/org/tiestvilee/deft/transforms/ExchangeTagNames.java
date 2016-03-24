package org.tiestvilee.deft.transforms;

import org.tiestvilee.deft.ast.Tag;

public class ExchangeTagNames implements Production {
    private final String xsltTagName;
    private final String deftTagName;

    public ExchangeTagNames(String xsltTagName, String deftTagName) {
        this.xsltTagName = xsltTagName;
        this.deftTagName = deftTagName;
    }

    @Override
    public Tag towardsDeft(Tag rootNode) {
        return (Tag) new RenameTag(xsltTagName, deftTagName).transform(rootNode);
    }

    @Override
    public Tag towardsXslt(Tag rootNode) {
        return (Tag) new RenameTag(deftTagName, xsltTagName).transform(rootNode);
    }
}
