/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package textextractor;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
public class WordManager {

    private ArrayList listDoc;
    private ArrayList listDocx;

    public ArrayList extractDoc(FileInputStream fis) throws IOException {
        HWPFDocument doc = new HWPFDocument(fis);
        Range range = doc.getRange();
        for (int i = 0; i < range.numParagraphs(); i++) {
            Paragraph p = range.getParagraph(i);
            StyleDescription style = doc.getStyleSheet().getStyleDescription(p.getStyleIndex());
            if (!"Normal".equals(style.getName())) {
                System.out.println(style.getName());
            }
            String[] ary = p.text().split(" ");
            System.out.println(p.text());
            listDoc = new ArrayList();
            listDoc.addAll(Arrays.asList(ary));
        }
        return listDoc;

    }

    public ArrayList extractDocx(FileInputStream fis) throws IOException {
                    listDocx = new ArrayList();
        XWPFDocument docx = new XWPFDocument(fis);
        List<XWPFParagraph> pragraphList = docx.getParagraphs();
        pragraphList.stream().forEach((pragraph) -> {
            if (pragraph.getStyle() != null) {
                System.out.println(pragraph.getStyle());
            }
            System.out.println(pragraph.getText());
            String[] ary = pragraph.getText().split(" ");
            listDocx.addAll(Arrays.asList(ary));
        });
        return listDocx;
    }
}
