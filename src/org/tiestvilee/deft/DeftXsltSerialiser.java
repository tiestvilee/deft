package org.tiestvilee.deft;

import com.googlecode.totallylazy.Predicates;
import com.googlecode.totallylazy.Sequence;
import org.tiestvilee.deft.grammar.Attribute;
import org.tiestvilee.deft.grammar.Node;
import org.tiestvilee.deft.grammar.Tag;
import org.tiestvilee.deft.grammar.Text;

import static com.googlecode.totallylazy.Predicates.instanceOf;
import static com.googlecode.totallylazy.Predicates.not;
import static com.googlecode.totallylazy.Sequences.sequence;
import static java.lang.String.format;

public class DeftXsltSerialiser {
    public static String deftToXslt(Node node) {
        if (node instanceof Text) {
            return deftToXslt((Text) node);
        }
        if (node instanceof Tag) {
            return deftToXslt((Tag) node);
        }
        if (node instanceof Attribute) {
            return deftToXslt((Attribute) node);
        }
        throw new RuntimeException("unknown Node type: " + node.getClass().toString());
    }

    public static String deftToXslt(Tag tag) {
        Sequence<Node> sequence = sequence(tag.children);
        return format("<%s%s>%s</%s>",
            tag.tagName,
            sequence.filter(instanceOf(Attribute.class)).map(DeftXsltSerialiser::deftToXslt),
            sequence.filter(not(instanceOf(Attribute.class))).map(DeftXsltSerialiser::deftToXslt),
            tag.tagName);
    }

    public static String deftToXslt(Attribute attr) {
        return format(" %s='%s'", attr.tagName, deftToXslt(attr.value));
    }

    public static String deftToXslt(Text text) {
        return text.string;
    }
}
