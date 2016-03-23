package org.tiestvilee.deft.transforms;

import org.tiestvilee.deft.ast.*;

import static org.tiestvilee.deft.ast.Attribute.attr;
import static org.tiestvilee.deft.ast.Text.text;
import static org.tiestvilee.deft.ast.XPath.xpath;

public class ExchangeXPathAndAttribute {
    private final String tagName;
    private final String attrName;

    public ExchangeXPathAndAttribute(String tagName, String attrName) {
        this.tagName = tagName;
        this.attrName = attrName;
    }

    public Node changeAttributeToXPath(Tag ast) {
        return new ChangeAttributeToXPath().visit(ast);
    }

    public Node changeXPathToAttribute(Tag ast) {
        return new ChangeXPathToAttribute().visit(ast);
    }

    private class ChangeAttributeToXPath extends TagMatchingNodeVisitor {
        public ChangeAttributeToXPath() { super(tagName); }

        @Override
        public Node visit(Attribute attribute) {
            if (inMatchingTag && attribute.tagName.equals(attrName) && attribute.value instanceof Text) {
                return xpath(((Text) attribute.value).string); // how to do this better?
            }
            return super.visit(attribute);
        }
    }

    private class ChangeXPathToAttribute extends TagMatchingNodeVisitor {
        private ChangeXPathToAttribute() {super(tagName);}

        @Override
        public Node visit(XPath xpath) {
            return inMatchingTag ? attr(attrName, text(xpath.xPath)) : xpath;
        }
    }
}
