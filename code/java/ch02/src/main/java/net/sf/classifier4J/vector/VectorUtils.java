
package net.sf.classifier4J.vector;


public class VectorUtils {
    public static int scalarProduct(int[] one, int[] two) throws IllegalArgumentException {
        if ((one == null) || (two == null)) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        
        if (one.length != two.length) {
            throw new IllegalArgumentException("Arguments of different length are not allowed");
        }
        
        int result = 0;
        for (int i = 0; i < one.length; i++) {
            result += one[i] * two[i];
        }
        return result;
    }
    
    public static double vectorLength(int[] vector) throws IllegalArgumentException {
        if (vector == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        
        double sumOfSquares = 0d;
        for (int i = 0; i < vector.length; i++) {
            sumOfSquares = sumOfSquares + (vector[i] * vector[i]);
        }
        
        return Math.sqrt(sumOfSquares);
    }
    
    public static double cosineOfVectors(int[] one, int[] two) throws IllegalArgumentException {
        if ((one == null) || (two == null)) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        
        if (one.length != two.length) {
            throw new IllegalArgumentException("Arguments of different length are not allowed");
        }     
        double denominater = (vectorLength(one) * vectorLength(two));
        if (denominater == 0) {
            return 0;
        } else {
            return (scalarProduct(one, two)/denominater);
        }
    }
}
