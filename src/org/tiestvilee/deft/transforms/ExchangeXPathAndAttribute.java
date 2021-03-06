package org.tiestvilee.deft.transforms;

import org.tiestvilee.deft.ast.*;

import static org.tiestvilee.deft.ast.Attribute.attr;
import static org.tiestvilee.deft.ast.Text.text;
import static org.tiestvilee.deft.ast.XPath.xpath;

public class ExchangeXPathAndAttribute implements Production {
    private final String tagName;
    private final String attrName;

    public ExchangeXPathAndAttribute(String tagName, String attrName) {
        this.tagName = tagName;
        this.attrName = attrName;
    }

    public Tag towardsDeft(Tag ast) {
        return (Tag) new ChangeAttributeToXPath().visit(ast);
    }

    public Tag towardsXslt(Tag ast) {
        return (Tag) new ChangeXPathToAttribute().visit(ast);
    }

    private class ChangeAttributeToXPath extends TagMatchingNodeVisitor {
        public ChangeAttributeToXPath() { super(tagName); }

        @Override
        public Node visit(Attribute attribute) {
            if (inMatchingTag && attribute.tagName.equals(attrName) && attribute.value instanceof Text) {
                // how to do this better?
                // not happy about the casting/instance ofs or the escaping and unescaping of the quotes.
                return xpath(((Text) attribute.value).string.replace("\\'", "'"));
            }
            return super.visit(attribute);
        }
    }

    private class ChangeXPathToAttribute extends TagMatchingNodeVisitor {
        private ChangeXPathToAttribute() {super(tagName);}

        @Override
        public Node visit(XPath xpath) {
            return inMatchingTag ? attr(attrName, text(xpath.xPath.replaceAll("'", "\\'"))) : xpath;
        }
    }
}
