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

import net.sf.classifier4J.util.ToStringBuilder;

import java.util.Arrays;


/**
 * @author Nick Lothian
 * @author Peter Leschev
 */
public class DefaultStopWordsProvider implements IStopWordProvider {
    // This array is sorted in the constructor
    private String[] stopWords = { "a", "and", "the", "me", "i", "of", "if", "it", "is", "they", "there", "but", "or", "to", "this", "you", "in", "your", "on", "for", "as", "are", "that", "with", "have", "be", "at", "or", "was", "so", "out", "not", "an" };
    private String[] sortedStopWords = null;

    public DefaultStopWordsProvider() {
        sortedStopWords = getStopWords();
        Arrays.sort(sortedStopWords);
    }

    /**
     * getter method which can be overridden to 
     * supply the stop words. The array returned by this 
     * method is sorted and then used internally
     * 
     * @return the array of stop words
     */
    public String[] getStopWords() {
        return stopWords;
    }

    /**
     * @see net.sf.classifier4J.IStopWordProvider#isStopWord(String)
     */
    public boolean isStopWord(String word) {
        if (word == null || "".equals(word)) {
            return false;
        } else {
            // search the sorted array for the word, converted to lowercase
            // if it is found, the index will be >= 0
            return (Arrays.binarySearch(sortedStopWords, word.toLowerCase()) >= 0);
        }
    }

    public String toString() {
        return new ToStringBuilder(this).append("stopWords.size()", sortedStopWords.length).toString();
    }
}