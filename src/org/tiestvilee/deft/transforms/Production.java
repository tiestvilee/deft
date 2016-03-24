package org.tiestvilee.deft.transforms;

import org.tiestvilee.deft.ast.Tag;

public interface Production {
    Tag towardsDeft(Tag rootNode);

    Tag towardsXslt(Tag rootNode);
}
