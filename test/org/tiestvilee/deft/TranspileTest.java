package org.tiestvilee.deft;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class TranspileTest {

    @Test
    public void converts_from_xslt_to_deft_and_back() throws Exception {
        String xsltViaDocument = serialise(documentFrom(testFile()));

        String result = Transpile.transpileToXslt(Transpile.transpileToDeft(xsltViaDocument));

        Assert.assertEquals(stripWhitespaceFrom(xsltViaDocument), stripWhitespaceFrom(result));
        assertThat(stripWhitespaceFrom(xsltViaDocument), is(stripWhitespaceFrom(result)));
    }

    @Test
    public void deft_replaces_angles_with_squares() throws Exception {
        assertThat(Transpile.transpileToDeft("<xslt:something/>"), is("[xslt:something]"));
        assertThat(Transpile.transpileToXslt("[xslt:something]"), is("<xslt:something/>"));
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
