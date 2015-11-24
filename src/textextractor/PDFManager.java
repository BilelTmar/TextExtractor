/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package textextractor;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.SimpleBookmark;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Tmar bilel
 */
class PDFManager {

    ArrayList listPdf = new ArrayList();

    public PDFManager() {
    }

    public void inspectPdf(String filename) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(filename);
        List<HashMap<String, Object>> list = SimpleBookmark.getBookmark(reader);
        if (list != null) {
            list.stream().forEach((list1) -> {
                showTitle(list1);
            });
//        SimpleBookmark.exportToXML(list,
//                new FileOutputStream("Bookmark.txt"), "ISO8859-1", true);
        }
        reader.close();
    }

    public void showTitle(HashMap<String, Object> bm) {
        System.out.println((String) bm.get("Title"));
        List<HashMap<String, Object>> kids = (List<HashMap<String, Object>>) bm.get("Kids");
        if (kids != null) {
            kids.stream().forEach((kid) -> {
                showTitle(kid);
            });
        }
    }

    /**
     * Parses a PDF to a plain text file.
     *
     * @param pdf the original PDF
     * @throws IOException
     */
    public ArrayList parsePdf(String pdf) throws IOException {
        PdfReader reader = new PdfReader(pdf);
        PdfReaderContentParser parser = new PdfReaderContentParser(reader);
        TextExtractionStrategy strategy;
        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            strategy = parser.processContent(i, new SimpleTextExtractionStrategy());
            System.out.println(strategy.getResultantText());
            listPdf.add(strategy.getResultantText());
        }
        return listPdf;
    }
}
