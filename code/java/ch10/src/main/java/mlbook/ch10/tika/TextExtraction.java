package mlbook.ch10.tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TextExtraction {
    public String toPlainText(String filename) {
        BodyContentHandler handler = new BodyContentHandler();

        AutoDetectParser parser = new AutoDetectParser();
        Metadata metadata = new Metadata();
        String output = "";
        try {
            InputStream stream = new BufferedInputStream(new FileInputStream(filename));
            parser.parse(stream, handler, metadata);
            output =  handler.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TikaException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return output;
    }

    public static void main(String[] args) {
        TextExtraction t = new TextExtraction();
        String output = t.toPlainText("/path/to/a/pdfdoc/addname.pdf");
        System.out.println(output);
    }
}
