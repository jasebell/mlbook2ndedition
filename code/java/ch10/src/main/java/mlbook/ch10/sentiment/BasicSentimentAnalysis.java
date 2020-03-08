package mlbook.ch10.sentiment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class BasicSentimentAnalysis {

    public BasicSentimentAnalysis() {}

    public void runSentimentAnalysis(List<String> sentences) {
        Set<String> pwords = loadWords("/path/to/data/ch10/sentiment/positive-words.txt");
        Set<String> nwords = loadWords("/path/to/data/ch10/sentiment/negative-words.txt");

        for(String s : sentences) {
            System.out.println("Sentence: " + s);
            System.out.println("Score: " + calculateSentimentScore(s, pwords, nwords));
            System.out.println("*******");
        }

    }

    public int calculateSentimentScore(String sentence, Set<String> pwords, Set<String> nwords) {
        int score = 0;
        String[] words = sentence.split(" ");
        for (int i = 0; i < words.length; i++) {
            if(pwords.contains(words[i])) {
                System.out.println("Contains the positive word: " + words[i]);
                score = score + 1;
            } else if (nwords.contains(words[i])) {
                System.out.println("Contains the negative word: " + words[i]);
                score = score - 1;
            }
        }
        return score;
    }


    public List<String> loadSentences(String filepath) {
        List<String> sentences = new ArrayList<String>();
        try {
            File file = new File(filepath);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s;
            while ((s = br.readLine()) != null) {
                sentences.add(s);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return sentences;
    }


    public Set<String> loadWords(String filepath) {
        Set<String> words = new HashSet<String>();
        try {
            File file = new File(filepath);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s;
            while ((s = br.readLine()) != null) {
               if(!s.startsWith(";")) {
                   words.add(s);
               }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return words;
    }


    public static void main(String[] args) {
        BasicSentimentAnalysis bsa = new BasicSentimentAnalysis();
        List<String> sentences = bsa.loadSentences("/path/to/data/ch10/sentiment/sentences.txt");
        bsa.runSentimentAnalysis(sentences);
    }


}
