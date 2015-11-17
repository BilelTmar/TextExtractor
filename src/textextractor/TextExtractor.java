package textextractor;

import com.itextpdf.text.DocumentException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.StyleDescription;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

/**
 *
 * @author Tmar bilel
 */
public class TextExtractor {

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     * @throws com.itextpdf.text.DocumentException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, DocumentException {

        FileInputStream fis = new FileInputStream(args[0]);
        if (args[0].contains(".docx")) {
            XWPFDocument docx = new XWPFDocument(fis);
            List<XWPFParagraph> pragraphList = docx.getParagraphs();
            pragraphList.stream().forEach((pragraph) -> {
                if (pragraph.getStyle() != null) {
                    System.out.println(pragraph.getStyle());
                }
                System.out.println(pragraph.getText());
            });
        } else if (args[0].contains(".doc")) {
            HWPFDocument doc = new HWPFDocument(fis);
            Range range = doc.getRange();
            for (int i = 0; i < range.numParagraphs(); i++) {
                Paragraph p = range.getParagraph(i);
                StyleDescription style = doc.getStyleSheet().getStyleDescription(p.getStyleIndex());
                if (!"Normal".equals(style.getName())) {
                    System.out.println(style.getName());
                }
                System.out.println(p.text());
            }

        } else if (args[0].contains(".pdf")) {
            PDFManager pdfManager = new PDFManager();
            pdfManager.inspectPdf(args[0]);
            pdfManager.parsePdf(args[0]);
        }
    }

}
