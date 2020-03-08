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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;

/**
 * 
 * <p>A JDBC based datasource. It requires a table of the following structure (tested in MySQL 4):
 * 
 * <pre>
 * CREATE TABLE word_probability (
 *	word			VARCHAR(255) NOT NULL,
 *	category		VARCHAR(20) NOT NULL,
 *	match_count		INT DEFAULT 0 NOT NULL,
 *	nonmatch_count	INT DEFAULT 0 NOT NULL,
 *	PRIMARY KEY(word, category)
 * )
 * </pre>
 *
 *</p>
 *<p>It will truncate any word longer than 255 characters to 255 characters</p>
 *
 * @author Nick Lothian
 * @author Peter Leschev
 *  
 */
public class JDBCWordsDataSource implements ICategorisedWordsDataSource {

    IJDBCConnectionManager connectionManager;

    private Log log = LogFactory.getLog(this.getClass());

    /**
     * Create a JDBCWordsDataSource using the DEFAULT_CATEGORY ("DEFAULT")
     * 
     * @param cm The connection manager to use
     */
    public JDBCWordsDataSource(IJDBCConnectionManager cm) throws WordsDataSourceException {
        this.connectionManager = cm;
        createTable();
    }

    public WordProbability getWordProbability(String category, String word) throws WordsDataSourceException {

        WordProbability wp = null;
        String method = "getWordProbability()";

        int matchingCount = 0;
        int nonMatchingCount = 0;

        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT match_count, nonmatch_count FROM word_probability WHERE word = ? AND category = ?");
            ps.setString(1, word);
            ps.setString(2, category);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                matchingCount = rs.getInt("match_count");
                nonMatchingCount = rs.getInt("nonmatch_count");
            }
            wp = new WordProbability(word, matchingCount, nonMatchingCount);

        } catch (SQLException e) {
            throw new WordsDataSourceException("Problem obtaining WordProbability from database", e);
        } finally {
            if (conn != null) {
                try {
                    connectionManager.returnConnection(conn);
                } catch (SQLException e1) {
                    // ignore
                }
            }
        }

        if (log.isDebugEnabled()) {
            log.debug(method + " WordProbability loaded [" + wp + "]");
        }

        return wp;

    }

    public WordProbability getWordProbability(String word) throws WordsDataSourceException {
        return getWordProbability(ICategorisedClassifier.DEFAULT_CATEGORY, word);
    }

    private void updateWordProbability(String category, String word, boolean isMatch) throws WordsDataSourceException {
        String fieldname = "nonmatch_count";
        if (isMatch) {
            fieldname = "match_count";
        }

        // truncate word at 255 characters
        if (word.length() > 255) {
            word = word.substring(0, 254);
        }

        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            PreparedStatement insertStatement = conn.prepareStatement("INSERT INTO word_probability (word, category) VALUES (?, ?)");
            PreparedStatement selectStatement = conn.prepareStatement("SELECT 1 FROM word_probability WHERE word = ? AND category = ?");
            PreparedStatement updateStatement = conn.prepareStatement("UPDATE word_probability SET " + fieldname + " = " + fieldname + " + 1 WHERE word = ? AND category = ?");

            selectStatement.setString(1, word);
            selectStatement.setString(2, category);
            ResultSet rs = selectStatement.executeQuery();
            if (!rs.next()) {
                // word is not in table
                // insert the word
                insertStatement.setString(1, word);
                insertStatement.setString(2, category);
                insertStatement.execute();
            }
            // update the word count
            updateStatement.setString(1, word);
            updateStatement.setString(2, category);
            updateStatement.execute();

        } catch (SQLException e) {
            throw new WordsDataSourceException("Problem updating WordProbability", e);
        } finally {
            if (conn != null) {
                try {
                    connectionManager.returnConnection(conn);
                } catch (SQLException e1) {
                    // ignore
                }
            }
        }

    }

    public void addMatch(String category, String word) throws WordsDataSourceException {
        if (category == null) {
            throw new IllegalArgumentException("category cannot be null");
        }
        updateWordProbability(category, word, true);
    }

    public void addMatch(String word) throws WordsDataSourceException {
        updateWordProbability(ICategorisedClassifier.DEFAULT_CATEGORY, word, true);
    }

    public void addNonMatch(String category, String word) throws WordsDataSourceException {
        if (category == null) {
            throw new IllegalArgumentException("category cannot be null");
        }
        updateWordProbability(category, word, false);
    }

    public void addNonMatch(String word) throws WordsDataSourceException {
        updateWordProbability(ICategorisedClassifier.DEFAULT_CATEGORY, word, false);
    }

    /**
     * Create the word_probability table if it does not already
     * exist. Tested successfully with MySQL 4 & HSQLDB. See
     * comments in code for Axion 1.0M1 issues. 
     *   
     * 
     * @throws WordsDataSourceException
     */
    private void createTable() throws WordsDataSourceException {
        Connection con = null;
        try {
            con = connectionManager.getConnection();

            // check if the word_probability table exists 
            DatabaseMetaData dbm = con.getMetaData();
            ResultSet rs = dbm.getTables(null, null, "word_probability", null);
            if (!rs.next()) {
                // the table does not exist
                Statement stmt = con.createStatement();
                //	Under Axion 1.0M1, use 			
                //	 stmt.executeUpdate( "CREATE TABLE word_probability ( "
                //			+ " word			VARCHAR(255) NOT NULL,"
                //			+ " category		VARCHAR(20) NOT NULL,"
                //			+ " match_count		INTEGER NOT NULL,"
                //			+ " nonmatch_count	INTEGER NOT NULL, "
                //			+ " PRIMARY KEY(word, category) ) ");				
                stmt.executeUpdate("CREATE TABLE word_probability ( " 
                            + " word			VARCHAR(255) NOT NULL," 
                            + " category		VARCHAR(20) NOT NULL," 
                            + " match_count		INT DEFAULT 0 NOT NULL," 
                            + " nonmatch_count	INT DEFAULT 0 NOT NULL, " 
                            + " PRIMARY KEY(word, category) ) ");
            }
        } catch (SQLException e) {
            throw new WordsDataSourceException("Problem creating table", e); // we can't recover from this				
        } finally {
            if (con != null) {
                try {
                    connectionManager.returnConnection(con);
                } catch (SQLException e1) {
                    // ignore
                }
            }
        }
    }
}
