
package net.sf.classifier4J.vector;

import net.sf.classifier4J.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;


public class VectorClassifier extends AbstractCategorizedTrainableClassifier {
    public static double DEFAULT_VECTORCLASSIFIER_CUTOFF = 0.80d;
    
    
    private int numTermsInVector = 25;
    private ITokenizer tokenizer;
    private IStopWordProvider stopWordsProvider;
    private TermVectorStorage storage;    
    
    public VectorClassifier() {
        tokenizer = new DefaultTokenizer();
        stopWordsProvider = new DefaultStopWordsProvider();
        storage = new HashMapTermVectorStorage();
        
        setMatchCutoff(DEFAULT_VECTORCLASSIFIER_CUTOFF);
    }
    
    public VectorClassifier(TermVectorStorage storage) {
        this();
        this.storage = storage;
    }
    
    /**
     * @see net.sf.classifier4J.ICategorisedClassifier#classify(String, String)
     */
    public double classify(String category, String input) throws ClassifierException {

        // Create a map of the word frequency from the input
        Map wordFrequencies = Utilities.getWordFrequency(input, false, tokenizer, stopWordsProvider);

        TermVector tv = storage.getTermVector(category);
        if (tv == null) {
            return 0;
        } else {
            int[] inputValues = generateTermValuesVector(tv.getTerms(), wordFrequencies);

            return VectorUtils.cosineOfVectors(inputValues, tv.getValues());
        }
    }


    /**
     * @see net.sf.classifier4J.ICategorisedClassifier#isMatch(String, String)
     */
    public boolean isMatch(String category, String input) throws ClassifierException {
        return (getMatchCutoff() < classify(category, input));
    }



    /**
     * @see net.sf.classifier4J.ITrainable#teachMatch(String, String)
     */
    public void teachMatch(String category, String input) throws ClassifierException {
        // Create a map of the word frequency from the input
        Map wordFrequencies = Utilities.getWordFrequency(input, false, tokenizer, stopWordsProvider);

        // get the numTermsInVector most used words in the input
        Set mostFrequentWords = Utilities.getMostFrequentWords(numTermsInVector, wordFrequencies);

        String[] terms = (String[]) mostFrequentWords.toArray(new String[mostFrequentWords.size()]);
        Arrays.sort(terms);
        int[] values = generateTermValuesVector(terms, wordFrequencies);

        TermVector tv = new TermVector(terms, values);

        storage.addTermVector(category, tv);

        return;
    }

    /**
     * @param terms
     * @param wordFrequencies
     * @return
     */
    protected int[] generateTermValuesVector(String[] terms, Map wordFrequencies) {
        int[] result = new int[terms.length];
        for (int i = 0; i < terms.length; i++) {
            Integer value = (Integer)wordFrequencies.get(terms[i]);
            if (value == null) {
                result[i] = 0;
            } else {
                result[i] = value.intValue();
            }

        }
        return result;
    }


    /**
     * @see net.sf.classifier4J.ITrainable#teachNonMatch(String, String)
     */
    public void teachNonMatch(String category, String input) throws ClassifierException {
        return; // this is not required for the VectorClassifier        
    }
}
