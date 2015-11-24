package textextractor;

import com.itextpdf.text.DocumentException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.index.TermPositionVector;
import org.apache.lucene.search.Hit;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

/**
 *
 * @author Tmar bilel
 */
public class TextExtractor {

    private static final Directory directory = new RAMDirectory();
    private static final Analyzer analyzer = new StandardAnalyzer();
    private static final IndexWriter.MaxFieldLength mlf = IndexWriter.MaxFieldLength.UNLIMITED;

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     * @throws com.itextpdf.text.DocumentException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, DocumentException {

        IndexWriter writer = new IndexWriter(directory, analyzer, true, mlf);

        WordManager wordManager = new WordManager();
        ArrayList list = new ArrayList<>();

        FileInputStream fis = new FileInputStream(args[0]);
        if (args[0].contains(".docx")) {
            list = wordManager.extractDocx(fis);
        } else if (args[0].contains(".doc")) {
            list = wordManager.extractDoc(fis);

        } else if (args[0].contains(".pdf")) {
            PDFManager pdfManager = new PDFManager();
            pdfManager.inspectPdf(args[0]);
            list = pdfManager.parsePdf(args[0]);
        }
        String text = " ";
        for (Object elm : list) {
            String mot = (String) elm;
            text = text.concat(" ").concat(mot);

        }
        writer.addDocument(createDocument("1", text));
        writer.close();
        try (BufferedReader br = new BufferedReader(new FileReader("drugs.txt"))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String texts = sb.toString();
            String[] drugs = texts.split("\r\n");
            for (String drug : drugs) {
                //       System.out.println(drug);
                searchIndex(drug.toLowerCase());
            }
        }

    }

    private static Document createDocument(String id, String content) {
        Document doc = new Document();
        doc.add(new Field("id", id, Store.YES, Index.NOT_ANALYZED));
        doc.add(new Field("contents", content, Store.YES, Index.ANALYZED,
                Field.TermVector.WITH_POSITIONS_OFFSETS));
        return doc;
    }

    public static void searchIndex(String drug) throws IOException {
        IndexReader reader;
        reader = IndexReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        PhraseQuery query = new PhraseQuery();
        String[] words = drug.split(" ");
        for (String word : words) {
            query.add(new Term("contents", word));
        }
        Hits hits;
        hits = searcher.search(query);
        if (hits.length() > 0) {
            System.out.println("Existing drug: "+drug);
        }
        Iterator<Hit> it = hits.iterator();
        while (it.hasNext()) {
            Hit hit = it.next();
            int docId = hit.getId();
            TermFreqVector tfvector = reader.getTermFreqVector(docId, "contents");
            TermPositionVector tpvector = (TermPositionVector) tfvector;
            int termidx = tfvector.indexOf(drug);
            int[] termposx = tpvector.getTermPositions(termidx);

            for (int j = 0; j < termposx.length; j++) {
                System.out.println("term position : " + termposx[j]);
            } 
        }
    }
}
