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

/** 
 * @author Peter Leschev
 */
public class DefaultTokenizer implements ITokenizer {

    /**
     * Use a the "\W" (non-word characters) regexp to split the string passed to classify
     */
    public static int BREAK_ON_WORD_BREAKS = 1;

    /**
     * Use a the "\s" (whitespace) regexp to split the string passed to classify
     */
    public static int BREAK_ON_WHITESPACE = 2;

    private int tokenizerConfig = -1;
    private String customTokenizerRegExp = null;

    /**
     * Constructor that using the BREAK_ON_WORD_BREAKS tokenizer config by default
     */
    public DefaultTokenizer() {
        this(BREAK_ON_WORD_BREAKS);
    }

    public DefaultTokenizer(int tokenizerConfig) {
        setTokenizerConfig(tokenizerConfig);
    }

    public DefaultTokenizer(String regularExpression) {
        setCustomTokenizerRegExp(regularExpression);
    }

    /**
     * @return the custom regular expression to use for {@link #tokenize(String)}
     */
    public String getCustomTokenizerRegExp() {
        return customTokenizerRegExp;
    }

    /**
     * @return The configuration setting used by {@link #tokenize(String)}.
     */
    public int getTokenizerConfig() {
        return tokenizerConfig;
    }

    /**
     * <p>Allows the use of custom regular expressions to split up the input to {@link net.sf.classifier4J.IClassifier#classify(String)}.
     * Note that this regular expression will only be used if tokenizerConfig is set to
     * {@link #BREAK_ON_CUSTOM_REGEXP }</p>
     *
     * @param string set the custom regular expression to use for {@link #tokenize(String)}. Must not be null.
     */
    public void setCustomTokenizerRegExp(String string) {

        if (string == null) {
            throw new IllegalArgumentException("Regular Expression string must not be null");
        }

        customTokenizerRegExp = string;
    }

    /**
     * @param tokConfig The configuration setting for use by {@link #tokenize(String)}.
     * Valid values are {@link #BREAK_ON_CUSTOM_REGEXP}, {@link #BREAK_ON_WORD_BREAKS}
     * and {@link #BREAK_ON_WHITESPACE}
     */
    public void setTokenizerConfig(int tokConfig) {

        if (tokConfig != BREAK_ON_WORD_BREAKS && tokConfig != BREAK_ON_WHITESPACE) {
            throw new IllegalArgumentException("tokenConfiguration must be either BREAK_ON_WORD_BREAKS or BREAK_ON_WHITESPACE");
        }

        tokenizerConfig = tokConfig;
    }

    public String[] tokenize(String input) {

        String regexp = "";

        if (customTokenizerRegExp != null) {
            regexp = customTokenizerRegExp;
        } else if (tokenizerConfig == BREAK_ON_WORD_BREAKS) {
            regexp = "\\W";
        } else if (tokenizerConfig == BREAK_ON_WHITESPACE) {
            regexp = "\\s";
        } else {
            throw new IllegalStateException("Illegal tokenizer configuration. customTokenizerRegExp = null & tokenizerConfig = " + tokenizerConfig);
        }

        if (input != null) {
            String[] words = input.split(regexp);
            return words;

        } else {
            return new String[0];
        }
    }

    public String toString() {

        ToStringBuilder toStringBuilder = new ToStringBuilder(this);

        if (customTokenizerRegExp != null) {
            toStringBuilder = toStringBuilder.append("customTokenizerRegExp", customTokenizerRegExp);
        } else if (tokenizerConfig == BREAK_ON_WORD_BREAKS) {
            toStringBuilder = toStringBuilder.append("tokenizerConfig", "BREAK_ON_WORD_BREAKS");
        } else if (tokenizerConfig == BREAK_ON_WHITESPACE) {
            toStringBuilder = toStringBuilder.append("tokenizerConfig", "BREAK_ON_WHITESPACE");
        }

        return toStringBuilder.toString();
    }
}
