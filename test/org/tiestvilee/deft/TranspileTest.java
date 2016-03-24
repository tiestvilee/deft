package org.tiestvilee.deft;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.junit.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

public class TranspileTest {

    @Test
    public void converts_from_xslt_to_deft_and_back() throws Exception {
        String xsltViaDocument = serialise(documentFrom(testFile()));

        String deft = Transpile.transpileToDeft(xsltViaDocument);
//        String deftOld = Transpile.transpileToDeftOld(xsltViaDocument);
//        assertEquals(deft, deftOld);
        System.out.println("deft = " + deft);
        String andBack = Transpile.transpileToXslt(deft.trim());
        String result = serialise(documentFrom(andBack));

        assertEquals(stripWhitespaceFrom(xsltViaDocument), stripWhitespaceFrom(result));
        assertThat(stripWhitespaceFrom(xsltViaDocument), is(stripWhitespaceFrom(result)));
    }

    @Test
    public void deft_replaces_angles_with_squares() throws Exception {
        assertThat(Transpile.transpileToDeft("<xslt:something></xslt:something>"), is("[xslt:something]"));
        assertThat(Transpile.transpileToXslt("[xslt:something]"), is("<xslt:something></xslt:something>"));
    }

    @Test
    public void deft_respects_text() throws Exception {
        assertThat(Transpile.transpileToDeft("<xslt:something>hello</xslt:something>"), is("[xslt:something 'hello']"));
        assertThat(Transpile.transpileToXslt("[xslt:something 'hello']"), is("<xslt:something>hello</xslt:something>"));
    }

    @Test
    public void deft_understands_attributes() throws Exception {
        assertThat(Transpile.transpileToDeft("<xslt:something attr='value'>hello</xslt:something>"), is("[xslt:something [@attr 'value'] 'hello']"));
        assertThat(Transpile.transpileToXslt("[xslt:something [@attr 'value'] 'hello']"), is("<xslt:something attr=\"value\">hello</xslt:something>"));
    }

    @Test
    public void deft_understands_comments() throws Exception {
        assertThat(Transpile.transpileToDeft("<xslt:something><!--a comment--></xslt:something>"), is("[xslt:something {a comment}]"));
        assertThat(Transpile.transpileToXslt("[xslt:something {a comment}]"), is("<xslt:something><!--a comment--></xslt:something>"));
    }

    @Test
    public void deft_renames_template_tags() throws Exception {
        assertThat(Transpile.transpileToDeft("<xsl:template name=\"mytemplate\">some text</xsl:template>").trim(),
            is("[deft [@name 'mytemplate'] 'some text']"));
        assertThat(Transpile.transpileToXslt("[deft [@name 'mytemplate'] 'some text']"),
            is("<xsl:template name=\"mytemplate\">some text</xsl:template>"));
    }

    private String stripWhitespaceFrom(String originalXslt) {
        return originalXslt.replaceAll("\\s+", "").replaceAll("\\'", "\"");
    }

    private InputStream testFile() {
        return TranspileTest.class.getResourceAsStream("test.xslt");
    }

    private Document documentFrom(InputStream inputStream) throws Exception {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
    }

    private Document documentFrom(String string) throws Exception {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(string.getBytes()));
    }

    private String stringFrom(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        return reader.lines().collect(Collectors.joining("\n"));
    }

    private String serialise(Document document) throws IOException {
        OutputFormat format = new OutputFormat(document);
        format.setLineWidth(65);
        format.setIndenting(true);
        format.setIndent(2);
        format.setPreserveSpace(true);
        Writer stringWriter = new StringWriter();
        XMLSerializer xmlSerializer = new XMLSerializer(stringWriter, format);
        xmlSerializer.asDOMSerializer().serialize(document);
        return stringWriter.toString();
    }
}
