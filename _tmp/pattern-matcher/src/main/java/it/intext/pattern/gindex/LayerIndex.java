package it.intext.pattern.gindex;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.index.TermPositionVector;
import org.apache.lucene.index.TermPositions;
import org.apache.lucene.index.TermVectorMapper;
import org.apache.lucene.index.TermVectorOffsetInfo;
import org.apache.lucene.search.IndexSearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.TreeMultimap;

/*
 * Semantic layer
 * memory Index
 */
public class LayerIndex {

	private static final Logger logger = LoggerFactory
	.getLogger(LayerIndex.class);

	private IndexChangeObserver observer;

	public LayerIndex(IndexChangeObserver observer)
	{
		this.observer = observer;
	}

	public enum Confidence{
		UNSET,
		LOW,
		NORMAL,
		HIGH,
		FULL			
	};

	public static final int MAX_CONFIDENCE = 4;
	public static final int MIN_CONFIDENCE = 1;
	public boolean isGreaterOrEqual(Confidence first, Confidence other)
	{
		return first.ordinal() >= other.ordinal();
	}

	public class Position implements Comparable<Position>{
		int position;
		Confidence confidence = Confidence.UNSET;

		public Position(int position){
			this.position = position;
		}

		public Position(int position, Confidence confidence) {
			this.position = position;
			this.confidence = confidence;
		}

		public Confidence getConfidence() {
			return confidence;
		}

		protected void setConfidence(Confidence confidence) {
			this.confidence = confidence;
		}

		public int getPosition() {
			return position;
		}

		public int compareTo(Position arg0) {
			return Double.compare(this.position, arg0.position);
		}

		protected Position incrementPosition()
		{
			this.position++;
			return this;
		}

		protected Position decrementPosition()
		{
			this.position--;
			return this;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + position;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Position other = (Position) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (position != other.position)
				return false;
			return true;
		}

		private LayerIndex getOuterType() {
			return LayerIndex.this;
		}
		
		public String toString(){
			return String.valueOf(position) + (confidence == null ?  "" : "[" + confidence + "]"); 
		}

	}


//	private final TreeMultimap<String,Position> positions = TreeMultimap.create();
//	private final TreeMultimap<Integer,String> inversePositions = TreeMultimap.create();
	
	private final TreeMultimap<String,Position> positions = new TreeMultimap<String,Position>();
	private final TreeMultimap<Integer,String> inversePositions = new TreeMultimap<Integer,String>();

	private String name = "LAYER";
	private boolean changed = false;
	LayerIndexReader reader = null;
	private void indexChanged(){
		if (this.observer != null && this.observer.isLockChanges())
			return;
		changed = true;
		if (this.observer != null)
			observer.indexChanged();
	}
	
	public void lockChanges(){
		if (this.observer != null)
			this.observer.lockChanges();
	}
	
	public void unlockChanges(){
		if (this.observer != null)
			this.observer.unlockChanges();
	}
	
	public Set<Integer> getAllPositions()
	{
		return inversePositions.keySet();
	}
	
	public Collection<Position> getPositionsInfo()
	{
		return positions.values();
	}
	
	public Set<String> getAllTerms()
	{
		return positions.keySet();
	}

	public LayerIndex(){}

	public LayerIndex(String name){
		this.name = name;
	}

	public LayerIndex(String name, IndexChangeObserver observer){
		this(observer);
		this.name = name;
	}

	public boolean addTerm(String term, int position)
	{
		if (term.contains("."))
		{
			String tt = term.substring(0,term.lastIndexOf("."));
			String conf = term.substring(term.lastIndexOf(".")+1);
			try{
				Confidence cf = Confidence.valueOf(conf.toUpperCase());
				return addTerm(tt,position,cf);
			}catch(Exception e){}
		}		
		boolean added = positions.put(term, new Position(position));
		added &= inversePositions.put(position, term);
		if (added) {
			indexChanged();
			logger.trace("Adding term {} - {}",term,position);
		}
		return added;
	}

	public boolean addTerm(String term, int position, Confidence confidence)
	{

		boolean added = positions.put(term, new Position(position,confidence));
		added &= inversePositions.put(position, term);
		if (added){
			logger.trace("Adding term {} - {}",term+"{"+confidence.name()+"}",position);
			indexChanged();
		}
		return added;
	}

	public boolean addTerm(String term, int[] pss)
	{
		boolean added = false;
		for (int ps : pss)
			added |= addTerm(term,ps);
		if (added) {
			indexChanged();
			logger.trace("Adding term {} - {}",term,Arrays.toString(pss));
		}
		return added;
	}

	public boolean addTerm(String term, int[] pss, Confidence confidence)
	{
		boolean added = false;
		for (int ps : pss)
			added |= addTerm(term,ps,confidence);
		if (added){
			indexChanged();
			logger.trace("Adding term {} - {}",term,Arrays.toString(pss));
		}
		return added;
	}

	public boolean addTerm(String term, int startPosition, int endPosition)
	{
		boolean done = false;
		if (startPosition <= endPosition){
			for (int p = startPosition; p <= endPosition; p++)
				done |= addTerm(term,p);
			if (done) {
				indexChanged();
				logger.trace("Adding term {} - {}",term,new String(startPosition+","+endPosition));
			}
			return done;
		}
		else
			return false;

	}

	public boolean updateConfidence(String term, int[] positions, Confidence confidence)
	{
		boolean updated = false;
		for (int ps : positions)
			updated |= updateConfidence(term,ps,confidence);
		//if (updated) indexChanged();
		return updated;
	}

	public boolean updateConfidence(String term, int startPosition, int endPosition, Confidence confidence)
	{
		boolean done = false;
		if (startPosition <= endPosition){
			for (int p = startPosition; p <= endPosition; p++)
				done |= updateConfidence(term,p,confidence);
			//if (done) indexChanged();
			return done;
		}
		else
			return false;
	}

	public boolean updateConfidence(String term, int position, Confidence confidence)
	{
		try{
			Position p = positions.get(term).tailSet(new Position(position)).first();
			if (p.position == position)
				p.setConfidence(confidence);
			else
				return false;
			//indexChanged();
			return true;
		}catch(Exception e)
		{
			return false;
		}
	}

	public boolean increaseConfidence(String term, int[] positions)
	{
		boolean updated = false;
		for (int ps : positions)
			updated |= increaseConfidence(term,ps);
		//if (updated) indexChanged();
		return updated;
	}

	public boolean increaseConfidence(String term, int startPosition, int endPosition)
	{
		boolean done = false;
		if (startPosition <= endPosition){
			for (int p = startPosition; p <= endPosition; p++)
				done |= increaseConfidence(term,p);
			//if (done) indexChanged();
			return done;
		}
		else
			return false;
	}

	public boolean increaseConfidence(String term, int position)
	{
		term = cleanTerm(term);
		try{
			Position p = positions.get(term).tailSet(new Position(position)).first();
			if (p.position == position)
			{
				int conf = p.confidence.ordinal();
				if (conf < MAX_CONFIDENCE)
					conf++;
				p.setConfidence(Confidence.values()[conf]);
				logger.debug("Confidence set to {} on {}",conf,p);
			}
			else
				return false;
			//indexChanged();
			return true;
		}catch(Exception e)
		{
			return false;
		}
	}

	private String cleanTerm(String term) {
		if (term.contains(".")){
			try{
				Confidence.valueOf(term.substring(term.lastIndexOf(".")+1).toUpperCase());
				term = term.substring(0,term.lastIndexOf("."));				
			}catch(Exception e){}
		}
		return term;
	}

	public boolean decreaseConfidence(String term, int[] positions)
	{
		boolean updated = false;
		for (int ps : positions)
			updated |= decreaseConfidence(term,ps);
		//if (updated) indexChanged();
		return updated;
	}

	public boolean decreaseConfidence(String term, int startPosition, int endPosition)
	{
		boolean done = false;
		if (startPosition <= endPosition){
			for (int p = startPosition; p <= endPosition; p++)
				done |= decreaseConfidence(term,p);
			//if (done) indexChanged();
			return done;
		}
		else
			return false;
	}

	public boolean decreaseConfidence(String term, int position)
	{
		try{
			Position p = positions.get(term).tailSet(new Position(position)).first();
			if (p.position == position)
			{
				int conf = p.confidence.ordinal();
				if (conf == 0)
					conf = MIN_CONFIDENCE;
				else
					if (conf > MIN_CONFIDENCE)
						conf--;
				p.setConfidence(Confidence.values()[conf]);
				logger.debug("Confidence set to {} on {}",conf,p);
			}
			else
				return false;
			//indexChanged();
			return true;
		}catch(Exception e)
		{
			return false;
		}
	}

	public boolean addTerm(String term, int startPosition, int endPosition, Confidence confidence)
	{
		logger.debug("Adding term {} - {}",term,new String(startPosition+","+endPosition));
		boolean done = false;
		if (startPosition <= endPosition){
			for (int p = startPosition; p <= endPosition; p++)
				done |= addTerm(term,p,confidence);
			if (done) indexChanged();
			return done;
		}
		else
			return false;

	}

	public boolean addTerm(String term, int offset, int end, OffsetPositionTree positionTree)
	{
		int[] poss = positionTree.getPositions(offset, end);
		if (poss != null && poss.length > 0){
			for (int j : poss)
				addTerm(term,j);
			indexChanged();
			return true;
		}else
			return false;
	}

	public boolean removeTerm(String term,int position)
	{
		boolean removed = positions.remove(term, new Position(position));
		removed &= inversePositions.remove(position, term);
		if (removed) indexChanged();
		return removed;
	}

	public boolean removeTerm(String term)
	{
		SortedSet<Position> rems = positions.removeAll(term);
		boolean removed = !rems.isEmpty();
		for (Position rem : rems)
			removed &= inversePositions.remove(rem, term);
		if (removed) indexChanged();
		return removed;
	}

	public boolean removeTerm(String term, int[] pss)
	{
		boolean removed = false;
		for (int ps : pss)
			removed |= removeTerm(term,ps);
		return removed;
	}

	public int getStartPosition(String term, int inPos)
	{
		SortedSet<Position> poss = positions.get(term);
		Position inPosition = new Position(inPos);
		if (poss == null || !poss.contains(inPosition))
			return -1;
		boolean done = false;
		Position currPos = inPosition;
		while(!done)
		{
			try{
				done = (poss.headSet(currPos).last().incrementPosition() == currPos);
				if (!done) currPos.decrementPosition();
			}catch(Exception e){
				return currPos.position;
			}
		}
		return currPos.position;
	}

	public Set<String> termsAt(int position)
	{
		return inversePositions.get(position);
	}

	public Set<String> termsAt(int startPosition, int endPosition)
	{
		Set<String> ret = new HashSet<String>();
		for (int pos : inversePositions.keySet().tailSet(startPosition-1).headSet(endPosition+1))
			ret.addAll(inversePositions.get(pos));
		return ret;
	}

	public int getEndPosition(String term, int inPosition)
	{
		SortedSet<Position> poss = positions.get(term);
		if (poss == null || !poss.contains(new Position(inPosition)))
			return -1;
		boolean done = false;
		Position currPos = new Position(inPosition);
		while(!done)
		{
			try{
				done = (poss.tailSet(currPos).first().decrementPosition() == currPos);
				if (!done) currPos.incrementPosition();
			}catch(Exception e){
				return currPos.position;
			}
		}
		return currPos.position;
	}

	public int[] getInnerPositions(String term, int startPosition, int endPosition)
	{
		Confidence conf = null;
		if (term.contains(".")){
			try{
				conf = Confidence.valueOf(term.substring(term.lastIndexOf(".")+1).toUpperCase());
				term = term.substring(0,term.lastIndexOf("."));
			}catch(Exception e){}
		}
		SortedSet<Position> poss = positions.get(term);
		if (poss == null || endPosition < startPosition)
			return new int[0];
		return conf == null ? convertPosition(poss.subSet(new Position(startPosition), new Position(endPosition+1)))
				: convertPosition(poss.subSet(new Position(startPosition), new Position(endPosition+1)),conf);		
	}

	protected int[] convertPosition(Collection<Position> poss)
	{
		int[] ret = new int[poss.size()];
		Object[] pp = poss.toArray();
		for (int j=0; j < poss.size(); j++)
			ret[j] = ((Position)pp[j]).position;
		return ret;	
	}

	protected int[] convertPosition(Collection<Position> poss, Confidence conf)
	{
		int[] ret = new int[poss.size()];
		Object[] pp = poss.toArray();
		for (int j=0; j < poss.size(); j++){
			if (((Position)pp[j]).confidence == conf)
				ret[j] = ((Position)pp[j]).position;
		}
		return ret;	
	}

	public int[] getPositions(String term)
	{
		SortedSet<Position> poss = positions.get(term);
		return convertPosition(poss);
	}

	public IndexReader getReader(){
		LayerIndexReader rreader = (changed || reader == null) ? 
				new LayerIndexReader() : reader;
				reader = rreader;
				changed = false;
				return reader;
	}

	public IndexSearcher getSearcher(){
		return new IndexSearcher(getReader());
	}

	public void clear()
	{
		this.positions.clear();
		this.inversePositions.clear();
	}

	public final class LayerIndexReader extends IndexReader{

		private final Term MATCH_ALL_TERM = new Term("", "");

		@Override
		protected void doClose() throws IOException {
			logger.warn("doClose() called on LayerIndex[{}]",name);			
		}

		@Override
		protected void doCommit() throws IOException {
			logger.warn("doCommit() called on LayerIndex[{}]",name);				
		}

		@Override
		protected void doDelete(int docNum) throws CorruptIndexException,
		IOException {
			logger.warn("doDelete(int docNum) called on LayerIndex[{}]",name);				
		}

		@Override
		protected void doSetNorm(int doc, String field, byte value)
		throws CorruptIndexException, IOException {
			logger.warn("doSetNorm(..) called on LayerIndex[{}]",name);			
		}

		@Override
		protected void doUndeleteAll() throws CorruptIndexException,
		IOException {
			logger.warn("doUndeleteAll() called on LayerIndex[{}]",name);				
		}

		@Override
		public int docFreq(Term t) throws IOException {
			return positions.containsKey(t.text()) ? 1 : 0;
		}

		@Override
		public Document document(int n, FieldSelector fieldSelector)
		throws CorruptIndexException, IOException {
			logger.warn("document(int n, FieldSelector fieldSelector) called on LayerIndex[{}]",name);
			return null;
		}

		@Override
		public Collection<String> getFieldNames(FieldOption fldOption) {
			logger.warn("getFieldNames(FieldOption fldOption) called on LayerIndex[{}]",name);
			return null;
		}

		@Override
		public TermFreqVector getTermFreqVector(int docNumber, String field)
		throws IOException {
			return new TermPositionVector() {

				public TermVectorOffsetInfo[] getOffsets(int index) {
					return null;
				}

				public int[] getTermPositions(int index) {
					String term = getTerms()[index];
					SortedSet<Position> ret = positions.get(term);
					return convertPosition(ret);					
				}

				public String getField() {
					return name;
				}

				public int[] getTermFrequencies() {
					SortedSet<String> terms = positions.keySet();
					int[] vals = new int[terms.size()];
					Iterator<String> tIt = terms.iterator();
					for (int j = 0; j < terms.size(); j++)
					{
						vals[j] = positions.get(tIt.next()).size();
					}
					return vals;
				}

				public String[] getTerms() {
					SortedSet<String> terms = positions.keySet();
					return toStringArray(terms);

				}

				public int indexOf(String term) {
					SortedSet<Position> poss = positions.get(term);
					return poss == null ? -1 : poss.first().position;
				}

				public int[] indexesOf(String[] terms, int start, int len) {
					int[] indexes = new int[len];
					for (int i = 0; i < len; i++) {
						indexes[i] = indexOf(terms[start++]);
					}
					return indexes;
				}

				public int size() {
					return positions.keySet().size();
				}

			};			
		}

		@Override
		public void getTermFreqVector(int docNumber, String field,
				TermVectorMapper mapper) throws IOException {
			getTermFreqVector(docNumber, mapper);
		}

		@Override
		public void getTermFreqVector(int docNumber, TermVectorMapper mapper)
		throws IOException {
			if (docNumber == 0){
				mapper.setExpectations(name, positions.size(), false, true);
				for (String term : positions.keySet())
				{
					mapper.map(term, positions.get(term).size(), null, 
							convertPosition(positions.get(term)));
				}
			}
		}

		@Override
		public TermFreqVector[] getTermFreqVectors(int docNumber)
		throws IOException {
			TermFreqVector[] vectors = new TermFreqVector[1];
			vectors[0] = getTermFreqVector(docNumber, new String());
			return vectors;
		}

		@Override
		public boolean hasDeletions() {
			logger.warn("hasDeletions() called on LayerIndex[{}]",name);		
			return false;
		}

		@Override
		public boolean isDeleted(int n) {
			logger.warn("isDeleted(int n) called on LayerIndex[{}]",name);	
			return false;
		}

		@Override
		public int maxDoc() {
			return 1;
		}

		@Override
		public byte[] norms(String field) throws IOException {
			//logger.warn("norms(String field) called on LayerIndex[{}]",name);
			return null;
		}

		@Override
		public void norms(String field, byte[] bytes, int offset)
		throws IOException {
			//logger.warn("norms(String field, byte[] bytes, int offset) called on LayerIndex[{}]",name);			
		}

		@Override
		public int numDocs() {
			logger.warn("numDocs() called on LayerIndex[{}]",name);
			return positions.isEmpty() ? 0 : 1;
		}

		@Override
		public TermDocs termDocs() throws IOException {
			return termPositions();
		}

		@Override
		public TermPositions termPositions() throws IOException {
			return new TermPositions() {

				private Iterator<Position> posit;
				private SortedSet<Position> positionsArr;

				public byte[] getPayload(byte[] data, int offset)
				throws IOException {
					logger.warn("getPayload(...) called on TermPositions[{}]",name);
					return null;
				}

				public int getPayloadLength() {
					return 0;
				}

				public boolean isPayloadAvailable() {
					return false;
				}

				public int nextPosition() throws IOException {
					return posit == null ? -1 : posit.next().position;
				}

				public void close() throws IOException {										
				}

				public int doc() {
					return 0;
				}

				public int freq() {
					return positionsArr.size();
				}

				public boolean next() throws IOException {
					return (posit == null ? false : 
						posit.hasNext());
				}

				public int read(int[] docs, int[] freqs) throws IOException {
					docs[0] = 0;
					freqs[0] = freq();
					return 1;
				}

				public void seek(Term term) throws IOException {
					SortedSet<Position> poss = positions.get(term.text());
					if (poss != null)
					{
						this.positionsArr = poss;
						this.posit = poss.iterator();
					}
				}

				public void seek(TermEnum termEnum) throws IOException {
					seek(termEnum.term());
				}

				public boolean skipTo(int target) throws IOException {
					return next();
				}

			};
		}
		
		private String[] toStringArray(final Collection<String> ct){
			String[] ret = new String[ct.size()];
			Iterator<String> it = ct.iterator();
			for (int j=0; j < ct.size(); j++)
				ret[j] = it.next();
			return ret;
		}

		@Override
		public TermEnum terms() throws IOException {
			return terms(MATCH_ALL_TERM);
		}

		@Override
		public TermEnum terms(Term t) throws IOException {
			final Term ft = t; 
			return !(ft == MATCH_ALL_TERM || positions.containsKey(t.text())) ? null : 
				new TermEnum(){

				private SortedSet<String> poss = ft == MATCH_ALL_TERM ? positions.keySet() :
					positions.keySet().tailSet(ft.text());

				@Override
				public void close() throws IOException {					
				}

				@Override
				public int docFreq() {
					return positions.get(poss.first()).size();
				}

				@Override
				public boolean next() throws IOException {
					if (poss.size() > 0)
					{
						poss = poss.tailSet(poss.first());
						return true;
					}
					return false;
				}

				@Override
				public Term term() {
					return new Term(name,poss.first());
				}

			};
		}	

		public boolean matchConfidence(String term, int position, Confidence lowest)
		{
			return isGreaterOrEqual(
					positions.get(term).tailSet(new Position(position)).first().confidence,
					lowest);
		}

	}

	public String toString(SemanticIndex idx)
	{
		StringBuffer buf = new StringBuffer();

		for (String term : this.positions.keySet())
		{
			buf.append(term+" : ");
			int start = -1;
			int last = -1;

			for (Position pos : this.positions.get(term))
			{
				if (pos.position - last > 1)
				{
					if (start > 0)
						buf.append("["+
								(start >= 0 ? start : last)
								+ ((last > start) ? " - "+last+"] " : "]; "));
					start = pos.position;					
				}
				// VE:2010-06-24 Bug fixing start =-1 and never reassigned
				if (start == -1) start = pos.position;
				last = pos.position;
				buf.append(idx.getTextTermAt(pos.position)+ (
						(pos.confidence != Confidence.UNSET) ? "("+pos.confidence.name().substring(0,1)+") " : " "));
			}
			buf.append("["+
					(start >= 0 ? start : last)
					+ ((last > start) ? " - "+last+"] " : "]; "));
			buf.append("\n");
		}
		return buf.toString().trim();
	}

	protected String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}
}
