/*
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Nick Lothian. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        developers of Classifier4J (http://classifier4j.sf.net/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The name "Classifier4J" must not be used to endorse or promote 
 *    products derived from this software without prior written 
 *    permission. For written permission, please contact   
 *    http://sourceforge.net/users/nicklothian/.
 *
 * 5. Products derived from this software may not be called 
 *    "Classifier4J", nor may "Classifier4J" appear in their names 
 *    without prior written permission. For written permission, please 
 *    contact http://sourceforge.net/users/nicklothian/.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */

package net.sf.classifier4J;

/** 
 * <p>Defines an interface for the classification of Strings.</p>
 * 
 * <p>Use the isMatch method if you want true/false matching, or use the
 * classify method if you match probability.</p>
 * 
 * <p>The isMatch method will return a boolean representing if a string
 * matches whatever criteria the implmentation is matching on. In the 
 * default implemnetation this is done by calling classify and checking 
 * if the returned match probability is greater than or equal to DEFAULT_CUTOFF.</p>
 * 
 * <p>The classify method will return a double value representing the
 * likelyhood that the string passed to it is a match on whatever 
 * criteria the implmentation is matching on.</p> 
 * 
 * <p>When implementing this class, it is recommended that 
 * the classify method should not return the values 1 or 0 
 * except in the cases there the classifier can guarentee that the string is 
 * a match doe does not match. For non-absolute matching algorithms LOWER_BOUND
 * and UPPER_BOUND should be used.</p> 
 * 
 * @author Nick Lothian
 * @author Peter Leschev
 * 
 * @see AbstractClassifier
 */
public interface IClassifier {
    /**
     * Default value to use if the implementation cannot work out how
     * well a string matches.
     */
    public static double NEUTRAL_PROBABILITY = 0.5d;

    /**
     * The minimum likelyhood that a string matches
     */
    public static double LOWER_BOUND = 0.01d;

    /**
     * The maximum likelyhood that a string matches
     */
    public static double UPPER_BOUND = 0.99d;

    /**
     * Default cutoff value used by defautl implmentation of 
     * isMatch. Any match probability greater than or equal to this
     * value will be classified as a match. 
     * 
     * The value is 0.9d
     * 
     */
    public static double DEFAULT_CUTOFF = 0.9d;

    /**
     * 
     * Sets the cutoff below which the input is not considered a match
     * 
     * @param cutoff the level below which isMatch will return false. Should be between 0 and 1.
     */
    public void setMatchCutoff(double cutoff);

    /**
     *
     * Function to determine the probability string matches a criteria.
     *   
     * @param input the string to classify
     * @return the likelyhood that this string is a match for this net.sf.classifier4J. 1 means 100% likely.
         *
         * @throws ClassifierException If a non-recoverable problem occurs
     */
    public double classify(String input) throws ClassifierException;

    /**
     * 
     * Function to determine if a string matches a criteria.
     * 
     * @param input the string to classify
     * @return true if the input string has a probability >= the cutoff probability of 
     * matching
         *
         * @throws ClassifierException If a non-recoverable problem occurs
     */
    public boolean isMatch(String input) throws ClassifierException;

    /**
     * Convenience method which takes a match probability
     * (calculated by {@link net.sf.classifier4J.IClassifier#classify(String)})
     * and checks if it would be classified as a match or not
     * 
     * @param matchProbability 
     * @return true if match, false otherwise
     */
    public boolean isMatch(double matchProbability);
}
