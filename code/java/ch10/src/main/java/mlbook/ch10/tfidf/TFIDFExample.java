package mlbook.ch10.tfidf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TFIDFExample {
    public double getTermFrequency(List<String> doc, String term) {
        double result = 0;
        for (String word : doc) {
            if (term.equalsIgnoreCase(word))
                result++;
        }
        return result / doc.size();
    }

    public double getInverseDocumentFrequency(List<List<String>> allDocuments, String term) {
        double wordOccurances = 0;
        for (List<String> document : allDocuments) {
            for (String word : document) {
                if (term.equalsIgnoreCase(word)) {
                    wordOccurances++;
                    break;
                }
            }
        }
        return Math.log(allDocuments.size() / wordOccurances);
    }

    public double computeTfIdf(List<String> doc, List<List<String>> docs, String term) {
        return getTermFrequency(doc, term) * getInverseDocumentFrequency(docs, term);
    }


    public List<String> loadDocToStrings(String filepath) {
        List<String> words = new ArrayList<String>();
        try {
            File file = new File(filepath);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s;
            while ((s = br.readLine()) != null) {
                String[] ws = s.split(" ");
                for (int i = 0; i < ws.length; i++) {
                    words.add(ws[i]);
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return words;
    }

    public static void main(String[] args) {
        String docspath = "/path/to/data/ch10";
        TFIDFExample tfidf = new TFIDFExample();

        List<String> wordDoc1 = tfidf.loadDocToStrings(docspath + "/doc1.txt");
        List<String> wordDoc2 = tfidf.loadDocToStrings(docspath + "/doc2.txt");
        List<String> wordDoc3 = tfidf.loadDocToStrings(docspath + "/doc3.txt");
        List<String> wordDoc4 = tfidf.loadDocToStrings(docspath + "/doc4.txt");
        List<String> wordDoc5 = tfidf.loadDocToStrings(docspath + "/doc5.txt");

        List<List<String>> allDocuments = Arrays.asList(wordDoc1, wordDoc2, wordDoc3, wordDoc4, wordDoc5);

        double score = tfidf.computeTfIdf(wordDoc4, allDocuments, "dapibus");
        System.out.println("Term Frequency for dapibus in wordDoc4 = " + tfidf.getTermFrequency(wordDoc4, "dapibus"));
        System.out.println("Inverse Doc Frequency for dapibus = " + tfidf.getInverseDocumentFrequency(allDocuments, "dapibus"));
        System.out.println("TF-IDF score for the word: dapibus = " + score);
    }
}