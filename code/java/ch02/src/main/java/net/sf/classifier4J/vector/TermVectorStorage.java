
package net.sf.classifier4J.vector;


public interface TermVectorStorage {
    public void addTermVector(String category, TermVector termVector);
    public TermVector getTermVector(String category);
}
