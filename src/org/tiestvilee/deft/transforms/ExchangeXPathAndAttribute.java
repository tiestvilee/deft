package org.tiestvilee.deft.transforms;

import org.tiestvilee.deft.ast.*;

import static org.tiestvilee.deft.ast.Attribute.attr;
import static org.tiestvilee.deft.ast.Text.text;
import static org.tiestvilee.deft.ast.XPath.xpath;

public class ExchangeXPathAndAttribute {
    private final String attrName;

    public ExchangeXPathAndAttribute(String attrName) {this.attrName = attrName;}

    public Node changeAttributeToXPath(Tag ast) {
        return new ChangeAttributeToXPath().visit(ast);
    }

    public Node changeXPathToAttribute(Tag ast) {
        return new ChangeXPathToAttribute().visit(ast);
    }

    private class ChangeAttributeToXPath extends NodeVisitorAdapter {
        @Override
        public Node visit(Attribute attribute) {
            if (attribute.tagName.equals(attrName) && attribute.value instanceof Text) {
                return xpath(((Text) attribute.value).string); // how to do this better?
            }
            return super.visit(attribute);
        }
    }

    private class ChangeXPathToAttribute extends NodeVisitorAdapter {
        @Override
        public Node visit(XPath xpath) {
            return attr(attrName, text(xpath.xPath));
        }
    }
}
