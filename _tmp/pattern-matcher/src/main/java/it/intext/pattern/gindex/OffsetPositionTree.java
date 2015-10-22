package it.intext.pattern.gindex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Utility class to
 * construct a tree for 
 * offset-to-position
 * and position-to-offset
 * resolver
 */
public class OffsetPositionTree {

	static final Logger logger = LoggerFactory.getLogger(OffsetPositionTree.class);
	private int[] positionsVector = new int[0];//Linearized stride = 2..
	private static int jumpf = 2;
	public OffsetPositionTree(){}
	
	protected void addSequenceTerm(int position, int offset, int end) throws OffsetPositionException
	{
		int idx = position*2;
		if (positionsVector.length == idx)
		{
			positionsVector = addToArray(positionsVector, offset);
			positionsVector = addToArray(positionsVector, end);
			return;
		}
		else
			throw new OffsetPositionException("Unable to add non-subsequent term!");
	}
	
	public static class OffsetPositionException extends Exception{

		private static final long serialVersionUID = 5754465915779600854L;

		public OffsetPositionException(String string) {
			super(string);
		}
		
	}

	public OffsetEndPair getOffsetEnd(int position){
		return position < 0 || position > positionsVector.length/2 ? null 
				: new OffsetEndPair(positionsVector[position*2],
						positionsVector[position*2+1]);
	}

	public static class OffsetEndPair{
		protected int offset;
		protected int end;
		public OffsetEndPair(int offset,int end){
			this.offset = offset;
			this.end = end;
		}
		public int getOffset() {
			return offset;
		}
		public void setOffset(int offset) {
			this.offset = offset;
		}
		public int getEnd() {
			return end;
		}
		public void setEnd(int end) {
			this.end = end;
		}		
	}

	protected int[] getPositions(int offset, int end)
	{
		int[] ret = new int[0];
		int startPosition = 2*seekOffset(offset);
		if (startPosition < 0)
			startPosition = 0;
		if (startPosition >= 0)
		{
			for (int j=startPosition+1; j < positionsVector.length; j+=jumpf){
				if (positionsVector[j] <= end){
					ret = addToArray(ret, (j-1)/2);
				}else
					break;
			}
		}
		return ret;
	}

	private int[] addToArray(int[] arr, int value)
	{
		int[] tmp = new int[arr.length];
		System.arraycopy(arr, 0, tmp, 0, arr.length);
		arr = new int[tmp.length+1];
		System.arraycopy(tmp, 0, arr, 0, tmp.length);
		arr[arr.length-1] = value;
		return arr;
	}

	/*
	 * Mono-thread binary
	 * search implementation
	 */
	private int seekOffset(int offset)
	{
		int lastStartIdx = positionsVector.length-2;
		int sidx = 0;
		int eidx = lastStartIdx;
		int currLow = positionsVector[sidx];		
		int currHigh = positionsVector[eidx];
		if (currLow == offset)
			return sidx;
		if (currHigh == offset)
			return eidx;
		int mid = -1;
		boolean deadEnd = false;
		boolean end = false;
		while(!(deadEnd || end))
		{
			mid = (eidx-sidx)/2 + sidx;
			mid -= mid%2;
			int midStart = positionsVector[mid];
			sidx = (midStart <= offset && midStart >= currLow) ? mid : sidx;
			currLow = (midStart <= offset && midStart >= currLow) ? midStart : currLow;
			eidx = (midStart >= offset && midStart <= currHigh) ? mid : eidx;
			currHigh = (midStart >= offset && midStart <= currHigh) ? midStart : currHigh;
			
			deadEnd = (currLow > offset || currHigh < offset);
			end = currLow == offset || currHigh == offset || eidx - sidx <= jumpf;
		}
		return deadEnd ? -1 : (
			currLow == offset ?
			sidx/2 : 
			eidx/2 
			)
			;
	}
	
	public void clear()
	{
		this.positionsVector = new int[0];
	}

	public static void main(String[] args) throws OffsetPositionException{
		OffsetPositionTree tree = new OffsetPositionTree();
		String sent = "Recensioni e consigli per gli acquistiNegozi Prenatal a prezzi scontatiEcco una delle marche che piacciono di più alle mamme per vestire i loro bambini: la Prenatal. Ma dove trovare negozi a prezzi scontati? A Torino, in piazza Derna, si trova al primo piano del negozio un outlet con prezzi più bassi rispetto a quelli che trovate negli altri piani. Articoli per neonato ed abbigliamento premaman fortemente scontati. Oltre all abbigliamento anche costumi da bagno premaman, lettini, carrozzine neonato, passeggini, articoli e abbigliamento fino a 11 anni. Nel negozio di Torino della Prenatal troverete anche tanti cestini con offerte a prezzi stracciati. Un altro outlet di Prenatal si trova invece al sud, a Napoli in via Egiziaca: uno spazio di 200mq con vasto assortimento di abbigliamento tessibile e puericultura fortemente scontato. Costumi da bagno per future mamme, lettini, carrozzine, abbigliamento per bambini fino all adolescenza.Negozi e abbigliamento PrenatalLa Prenatal è una grande catena di negozi specializzata in abbigliamento per bambino e per le mamme. E una delle marche certamente più note in Italia per questo segmento. E possibile trovare accessori e scarpe della Chicco, e abbigliamento monomarca. Nei negozi Prenatal si trovano anche biscotti e alimentari Plasmon oltre ad una serie di articoli dedicati alla sicurezza dei bambini. Insomma andare da Prenatal significa anche avere una grande scelta di prodotti per voi e i vostri piccoli. Per trovare gli sconti a Napoli e Torino:Prenatal - Napolivia Egiziaca, 8/10 80132 NapoliAperto 9,30 - 13,30 16 -19,30 Chiuso Lunedì, sabato solo la mattinaPer arrivare: autostrada A1 uscita Napoli Centro, seguire per P.zza Garibaldi, imboccare corso Umberto I , 100 m sulla destra. Si trovano l ospedale Annunciata vicino a Piazza GaribaldiPrenatal - TorinoPiazza Derna, 248 TorinoOrario: 9 - 20 continuatoPer arrivare Tram 2, Bus 50,62. Parcheggio interno e copertoRegistrati e DAI IL TUO GIUDIZIO/RECENSIONEAltri consigli per gli acquisti:Nessun commentoSolo gli utenti registrati possono lasciare commentiFeedsIscriviti al Feed di Shopping NotizieCommenti recenticommercio in Commercio.com: il personal shopperNikon Coolpix : in Fotocamere reflex in HD professionaLa nuova Peugeo in La nuova Peugeot 5008 al Salone diOutlet Kookai a in Comprare articoli Hello Kitty a preSpaccio azienda in Nero Giardini Outlet in provincia dIl nuovo Nokia in Un cellulare LG touch screen: Kp 50Un SUV dal prez in Un SUV dal prezzo basso e buona qua";
		Matcher mtch = Pattern.compile("\\s+").matcher(sent);
		int pos = 0;
		int lastStart = 0;
		while(mtch.find())
		{
			String tok = sent.substring(lastStart,mtch.start());
			
			int offset = lastStart;			
			int end = mtch.start();
			lastStart = mtch.end();
			System.out.println("Adding : ["+tok+"] ("+offset+","+end+","+pos+")");
			tree.addSequenceTerm(pos, offset, end);
			pos++;
		}
		if (lastStart != sent.length())
		{
			String tok = sent.substring(lastStart,sent.length());
			tree.addSequenceTerm(pos, lastStart, sent.length());
			System.out.println("Adding : ["+tok+"] ("+lastStart+","+sent.length()+","+pos+")");
		}
		
		for (int j = 0; j < 200; j++){
		Long cTime = System.currentTimeMillis();
		@SuppressWarnings("unused")
		int[] posses = tree.getPositions(new Double(Math.random()*sent.length()).intValue(), new Double(Math.random()*sent.length()).intValue());
		cTime = System.currentTimeMillis() - cTime;
		System.out.println("Computing time : "+cTime+" ms.");
		}	
	}
	
	
	

}
