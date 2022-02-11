/*	Parse tools: boolean symNameIs, int find, int mustFind, findEOS, rem.
 *	returned ints are indexes into pr.
 */

package tg37.tinyc;

public class PT extends TJ {

 /************** literals **************/
String xif = "if";
String xelse = "else";
String xint = "int";
String xchar = "char";
String xwhile = "while";
String xreturn = "return";
String xbreak = "break";
String xendlib = "endlibrary";
String xr = "r";
String xg = "g";
String xlb = "[";
String xrb = "]";
String xlpar = "(";
String xrpar = ")";
String xcomma = ",";
String newline = "\n";
String xcmnt = "/*";
String xcmnt2 = "//";
String xstar = "*";
String xsemi = ";";
String xpcnt = "%";
String xslash = "/";
String xplus = "+";
String xminus = "-";
String xlt = "<";
String xgt = ">";
String xnoteq = "!=";
String xeqeq = "==";
String xeq = "=";
String xge = ">=";
String xle = "<=";
String xnl = "\n";
String xvarargs = "...";

/*	set error unless already set, capture cursor in errat */
void eset( int err ){
	if(error != 0){
		error = err; errat = cursor;
	}
}

/* Bump cursor over whitespace. Then return true on match and advance
   cursor beyond the literal else false and do not advance cursor
 */
boolean lit(String s){
	int first,last;
	first=last=cursor;
	while( pr.charAt(first) == ' ' 
		|| pr.charAt(first) == '\t' ) ++first;
	last = first;
	if( s.equals(pr.substring(first,last)) ) {
		cursor += s.length();
		return true;
	}
	return false;
}

/*	return true if symname matches arg, no state change 
 */
	boolean symNameIs(String name){
		String tok = pr.substring(fname, lname);
		return tok.equals(name);
	}

/*	State is not changed by find or mustFind. Returned value is
	sole purpose of find. That plus setting err for mustFind. 
 */
	int find( int from, int upto, char c) {
		int x = from;
		while( pr.charAt(x) != c && x<upto) {
			++x;
		}
		return x<upto ? x : 0;
	}

/*	same as find but sets err on no match 
 */
	int mustFind( int from, int upto, char c, int err ) {
		int x = find(from, upto, c);
		if( x!=0 ) return x;
		else { eset(err); return 0; }
	}
	
/*	special find for end of string. Minds the old null ending.
	int findEOS( char x ) {
		while( x<endapp) {
			if( pr[x]==0 || pr[x]==0x22 ) return x;
			++x;
		}
		eset(CURSERR);
		return 0;
	}
 */

/*	skip over comments and/or empty lines in any order, new version
	tolerates 0x0d's, and implements // as well as old slash-star comments.
 */
	void rem() {
		for(;;) {
			char c = pr.charAt(cursor);
			while(    c==0x0a
					||c==0x0d
					||c==' '
					||c=='\t'
				  )++cursor;
			if( !(lit(xcmnt)||lit(xcmnt2)) ) return;
			while( c != 0x0a && c != 0x0d && cursor<endapp )
				++cursor;
		}
	}
}