package mlbook.ch10.word2vec;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.LineSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class Word2VecExample {

    public Word2VecExample() {
        System.out.println("Creating sentence iterator");
        SentenceIterator iter = createSentenceIterator("/path/to/data/ch10/word2vec/word2vec_test.txt");

        System.out.println("Creating tokenizer.");
        TokenizerFactory t = createTokenizer();

        System.out.println("Creating word2vec model.");
        Word2Vec vec = createWord2VecModel(iter, t);

        System.out.println("Evaluating the model.");
        evaluateModel(vec);

    }

    public Word2Vec createWord2VecModel(SentenceIterator iter, TokenizerFactory t) {
        Word2Vec vec = new Word2Vec.Builder()
                .minWordFrequency(5)
                .layerSize(100)
                .seed(42)
                .windowSize(5)
                .iterate(iter)
                .tokenizerFactory(t)
                .build();
        vec.fit();
        return vec;
    }


    public SentenceIterator createSentenceIterator(String filepath) {
        SentenceIterator iter = new LineSentenceIterator(new File(filepath));
        iter.setPreProcessor(new SentencePreProcessor() {
            public String preProcess(String sentence) {
                return sentence.toLowerCase();
            }
        });
        return iter;
    }

    public TokenizerFactory createTokenizer() {
        // Split on white spaces in the line to get words
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        return t;
    }

    public void evaluateModel(Word2Vec vec) {
        try {
            System.out.println("Serializing the model to disk.");
            WordVectorSerializer.writeWordVectors(vec, "/Users/jasebell/word2vecoutput.txt");
        } catch(IOException e) {
            e.printStackTrace();
        }

        System.out.println("Finding words nearest the word 'data'.");
        Collection<String> lst = vec.wordsNearest("data", 10);
        System.out.println(lst);

        System.out.println("Similarity score for data:machine - " + vec.similarity("data", "machine"));
        System.out.println("Similarity score for retail:machine - " + vec.similarity("retail", "machine"));
        System.out.println("Similarity score for games:machine - " + vec.similarity("games", "machine"));
    }

    public static void main(String[] args) {
        Word2VecExample w2ve = new Word2VecExample();
    }

}
