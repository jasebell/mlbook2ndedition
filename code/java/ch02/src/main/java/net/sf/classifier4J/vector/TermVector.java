
package net.sf.classifier4J.vector;

import java.io.Serializable;


public class TermVector implements Serializable {
    private final String terms[];
    private final int values[];
    
    public TermVector(String[] terms, int[] values) {        
        this.terms = terms;
        this.values = values;
    }
    
    public String[] getTerms() {
        return (String[]) terms.clone();
    }
    
    public int[] getValues() {
        return (int[]) values.clone();
    }
    
    public String toString() {
        StringBuffer results = new StringBuffer("{");

        for (int i = 0; i < terms.length; i++) {
            results.append("[");
            results.append(terms[i]);
            results.append(", ");
            results.append(values[i]);
            results.append("] ");
        }
        results.append("}");
        
        return results.toString();
    }
}
