/*	parses constant defining TJ.fname..TJ.lname which brackets trimmed constant. 
 *	Cursor moved just beyond constant. Returns Type: 
 */
package tg37.tinyc;

public class Const{
	Type konst() {
		int x;  //index into pr
		while(pr[TJ.cursor]==' ')++TJ.cursor;
		char c = pr[TJ.cursor];
		if( c=='+' || c=='-' || (c>='0'&&c<='9') ) {
			TJ.fname = TJ.cursor;
			do{
				++TJ.cursor; c=pr[TJ.cursor];
			} while(c>='0'&&c<='9');
			TJ.lname=TJ.cursor-1;
			return TJ.INT;
	
		} else if(lit("\"")) {
			TJ.fname=TJ.cursor;
			/* set TJ.lname = last char, TJ.cursor = TJ.lname+2 (past the quote) */
			if( (x = findEOS(TJ.fname)) ) {
				TJ.lname = x-1; /*before the quote */
				TJ.cursor = x+1; /*after the quote */
//				*x = 0;  // we don't do this anymore.
			}
			else { eset(TJ.CURSERR); return TJ.TYPEERR; }
			return TJ.STRING;
	
		} else if(lit("\'")) {
			TJ.fname=TJ.cursor;
			/* TJ.lname = last char, TJ.cursor = TJ.lname+2 (past the quote) */
			if( (x=mustFind(TJ.fname+1,TJ.fname+2,'\'',TJ.CURSERR)) ) {
				TJ.lname = x-1; 
				TJ.cursor = x+1;
			}
			else { eset(TJ.CURSERR); return -1; }
			return TJ.CHAR;
		
		} else return TJ.TYPEERR;  /* no match, TJ.TYPEERR==0 */
	}
}