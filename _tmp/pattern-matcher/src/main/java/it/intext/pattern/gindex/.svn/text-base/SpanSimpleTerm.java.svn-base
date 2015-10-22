package it.intext.pattern.gindex;
/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.surround.query.BasicQueryFactory;
import org.apache.lucene.queryParser.surround.query.DistanceSubQuery;
import org.apache.lucene.queryParser.surround.query.SpanNearClauseFactory;
import org.apache.lucene.queryParser.surround.query.SrndQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

public abstract class SpanSimpleTerm
  extends SrndQuery
  implements DistanceSubQuery, Comparable<SpanSimpleTerm>
{
  public SpanSimpleTerm(boolean q) {quoted = q;}
  
  private boolean quoted;
  boolean isQuoted() {return quoted;}
  
  public String getQuote() {return "\"";}
  public String getFieldOperator() {return "/";}
  
  public abstract String toStringUnquoted();
  
  public int compareTo(SpanSimpleTerm o) {
    /* for ordering terms and prefixes before using an index, not used */
	  SpanSimpleTerm ost = (SpanSimpleTerm) o;
    return this.toStringUnquoted().compareTo( ost.toStringUnquoted());
  }
  
  protected void suffixToString(StringBuffer r) {;} /* override for prefix query */
  
  public String toString() {
    StringBuffer r = new StringBuffer();
    if (isQuoted()) {
      r.append(getQuote());
    }
    r.append(toStringUnquoted());
    if (isQuoted()) {
      r.append(getQuote());
    }
    suffixToString(r);
    weightToString(r);
    return r.toString();
  }
  
  public abstract void visitMatchingTerms(
                            IndexReader reader,
                            String fieldName,
                            MatchingTermVisitor mtv) throws IOException;
  
  public interface MatchingTermVisitor {
    void visitMatchingTerm(Term t)throws IOException;
  }

  public String distanceSubQueryNotAllowed() {return null;}

  
  public Query makeLuceneQueryFieldNoBoost(final String fieldName, final BasicQueryFactory qf) {
    return new Query() {
      
		private static final long serialVersionUID = -5797231896662175334L;

	public String toString(String fn) {
        return getClass().toString() + " " + fieldName + " (" + fn + "?)";
      }
      
      public Query rewrite(IndexReader reader) throws IOException {
        final List<Query> luceneSubQueries = new ArrayList<Query>();
        visitMatchingTerms( reader, fieldName,
            new MatchingTermVisitor() {
              public void visitMatchingTerm(Term term) throws IOException {
                luceneSubQueries.add(qf.newSpanTermQuery(term));
              }
            });
        return  (luceneSubQueries.size() == 0) ? SrndQuery.theEmptyLcnQuery
              : (luceneSubQueries.size() == 1) ? (Query) luceneSubQueries.get(0)
              : makeBooleanQuery(
                  /* luceneSubQueries all have default weight */
                  luceneSubQueries, BooleanClause.Occur.SHOULD); /* OR the subquery terms */ 
      }
    };
  }
  
  public static Query makeBooleanQuery(
          List<Query> queries,
          BooleanClause.Occur occur) {
    if (queries.size() <= 1) {
      throw new AssertionError("Too few subqueries: " + queries.size());
    }
    BooleanQuery bq = new BooleanQuery();
    addQueriesToBoolean(bq, queries.subList(0, queries.size()), occur);
    return bq;
  }
  
  public static void addQueriesToBoolean(
          BooleanQuery bq,
          List<Query> queries,
          BooleanClause.Occur occur) {
    for (int i = 0; i < queries.size(); i++) {
      bq.add( (Query) queries.get(i), occur);
    }
  }
    
  public void addSpanQueries(final SpanNearClauseFactory sncf) throws IOException {
    visitMatchingTerms(
          sncf.getIndexReader(),
          sncf.getFieldName(),
          new MatchingTermVisitor() {
            public void visitMatchingTerm(Term term) throws IOException {
              sncf.addTermWeighted(term, getWeight());
            }
          });
  }
}




