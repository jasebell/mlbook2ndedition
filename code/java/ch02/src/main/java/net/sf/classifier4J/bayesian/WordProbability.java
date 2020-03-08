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

package net.sf.classifier4J.bayesian;

import net.sf.classifier4J.ICategorisedClassifier;
import net.sf.classifier4J.IClassifier;
import net.sf.classifier4J.util.*;
import net.sf.classifier4J.util.CompareToBuilder;
import net.sf.classifier4J.util.EqualsBuilder;
import net.sf.classifier4J.util.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;

/**
 * Represents the probability of a particular word. The user of this object
 * can either:
 * <ol>
 * 		<li>Set a specific probability for a particular word <I>or</I></li>
 * 		<li>Define the matching and non-matching counts for the particular word. 
 *        This class then calculates the probability for you.</li>
 * </ol>
 * 
 * @author Nick Lothian
 * @author Peter Leschev
 */
public class WordProbability implements Comparable, Serializable {

    private static final int UNDEFINED = -1;

    private String word = "";
    private String category = ICategorisedClassifier.DEFAULT_CATEGORY;

    private long matchingCount = UNDEFINED;
    private long nonMatchingCount = UNDEFINED;

    private double probability = IClassifier.NEUTRAL_PROBABILITY;

    public WordProbability() {
        setMatchingCount(0);
        setNonMatchingCount(0);
    }

    public WordProbability(String w) {
        setWord(w);
        setMatchingCount(0);
        setNonMatchingCount(0);
    }

    public WordProbability(String c, String w) {
        setCategory(c);
        setWord(w);
        setMatchingCount(0);
        setNonMatchingCount(0);
    }

    public WordProbability(String w, double probability) {
        setWord(w);
        setProbability(probability);
    }

    public WordProbability(String w, long matchingCount, long nonMatchingCount) {
        setWord(w);
        setMatchingCount(matchingCount);
        setNonMatchingCount(nonMatchingCount);
    }

    public void setWord(String w) {
        this.word = w;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setProbability(double probability) {
        this.probability = probability;
        this.matchingCount = UNDEFINED;
        this.nonMatchingCount = UNDEFINED;
    }

    public void setMatchingCount(long matchingCount) {
        if (matchingCount < 0) {
            throw new IllegalArgumentException("matchingCount must be greater than 0");
        }
        this.matchingCount = matchingCount;
        calculateProbability();
    }

    public void setNonMatchingCount(long nonMatchingCount) {
        if (nonMatchingCount < 0) {
            throw new IllegalArgumentException("nonMatchingCount must be greater than 0");
        }
        this.nonMatchingCount = nonMatchingCount;
        calculateProbability();
    }

    public void registerMatch() {
        if (matchingCount == Long.MAX_VALUE) {
            throw new UnsupportedOperationException("Long.MAX_VALUE reached, can't register more matches");
        }
        matchingCount++;
        calculateProbability();
    }

    public void registerNonMatch() {
        if (nonMatchingCount == Long.MAX_VALUE) {
            throw new UnsupportedOperationException("Long.MAX_VALUE reached, can't register more matches");
        }
        nonMatchingCount++;
        calculateProbability();
    }

    private void calculateProbability() {
        // the logger can't be a field because this class might be serialized 
        Log log = LogFactory.getLog(this.getClass());

        String method = "calculateProbability() ";

        if (log.isDebugEnabled()) {
            log.debug(method + "START");

            log.debug(method + "matchingCount = " + matchingCount);
            log.debug(method + "nonMatchingCount = " + nonMatchingCount);
        }

        double result = IClassifier.NEUTRAL_PROBABILITY;

        if (matchingCount == 0) {
            if (nonMatchingCount == 0) {
                result = IClassifier.NEUTRAL_PROBABILITY;
            } else {
                result = IClassifier.LOWER_BOUND;
            }
        } else {
            result = BayesianClassifier.normaliseSignificance((double) matchingCount / (double) (matchingCount + nonMatchingCount));
        }

        probability = result;

        if (log.isDebugEnabled()) {
            log.debug(method + "END Calculated [" + probability + "]");
        }
    }

    /**
         * @return
         */
    public double getProbability() {
        return probability;
    }

    public long getMatchingCount() {

        if (matchingCount == UNDEFINED) {
            throw new UnsupportedOperationException("MatchingCount has not been defined");
        }

        return matchingCount;
    }

    public long getNonMatchingCount() {

        if (nonMatchingCount == UNDEFINED) {
            throw new UnsupportedOperationException("nonMatchingCount has not been defined");
        }

        return nonMatchingCount;
    }

    /**
     * @return
     */
    public String getWord() {
        return word;
    }

    public String getCategory() {
        return category;
    }

    public boolean equals(Object o) {
        if (!(o instanceof WordProbability)) {
            return false;
        }
        WordProbability rhs = (WordProbability) o;
        return new EqualsBuilder().append(getWord(), rhs.getWord()).append(getCategory(), rhs.getCategory()).isEquals();
    }

    public int compareTo(Object o) {
        if (!(o instanceof WordProbability)) {
            throw new ClassCastException(o.getClass() + " is not a " + this.getClass());
        }
        WordProbability rhs = (WordProbability) o;
        return new CompareToBuilder().append(this.getCategory(), rhs.getCategory()).append(this.getWord(), rhs.getWord()).toComparison();
    }

    public String toString() {
        return new ToStringBuilder(this).append("word", word).append("category", category).append("probability", probability).append("matchingCount", matchingCount).append("nonMatchingCount", nonMatchingCount).toString();
    }

    public int hashCode() {
        // you pick a hard-coded, randomly chosen, non-zero, odd number
        // ideally different for each class
        return new HashCodeBuilder(17, 37).append(word).append(category).toHashCode();
    }
}
