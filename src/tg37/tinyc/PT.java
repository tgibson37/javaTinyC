/*	Parse tools: boolean lit, boolean symNameIs, int find, int mustFind, 
 *	int findEOS, void rem. Returned ints are indexes into pr. Only lit and rem
 *	bump the cursor.
 */

package tg37.tinyc;

public class PT extends TJ {

 /************** literals **************/
static final String xif = "if";
static final String xelse = "else";
static final String xint = "int";
static final String xchar = "char";
static final String xwhile = "while";
static final String xreturn = "return";
static final String xbreak = "break";
static final String xendlib = "endlibrary";
static final String xr = "r";
static final String xg = "g";
static final String xlb = "[";
static final String xrb = "]";
static final String xlpar = "(";
static final String xrpar = ")";
static final String xcomma = ",";
static final String newline = "\n";
static final String xcmnt = "/*";
static final String xcmnt2 = "//";
static final String xstar = "*";
static final String xsemi = ";";
static final String xpcnt = "%";
static final String xslash = "/";
static final String xplus = "+";
static final String xminus = "-";
static final String xlt = "<";
static final String xgt = ">";
static final String xnoteq = "!=";
static final String xeqeq = "==";
static final String xeq = "=";
static final String xge = ">=";
static final String xle = "<=";
static final String xnl = "\n";
static final String xvarargs = "...";
static final String xquote = "\"";

static int fname,lname;

/* Bump cursor over whitespace. Then return true on match and advance
   cursor beyond the literal else false and do not advance cursor
 */
static boolean lit(String s){
	int first,last;
	first=last=cursor;
	int stop = endapp-s.length();
	while(first < stop) {
		char c = pr.charAt(first);
		if( c == ' ' || c == '\t' ) ++first;
		else break;
	}
	cursor = first;
	last = first+s.length();
	if( last<endapp && s.equals(pr.substring(first,last)) ) {
		cursor += s.length();
		return true;
	}
	return false;
}
/* Parse a symbol defining fname, lname. ret: true if symbol.
 *      Advances the cursor to but not over the symbol,
 */
static boolean symName() {
        int temp;
        char c = pr.charAt(cursor);
        while( c == ' ' || c == '\t' ) c = pr.charAt(++cursor);
        temp=cursor;
        if( Character.isLetter(c) || c=='_') fname = temp;
        else return false;
        while( Character.isLetterOrDigit(c=pr.charAt(++temp)) || c=='_') ;
        lname = temp;
        return true;  /* good, fname and lname defined */
}

/*	return true if symname matches arg, no state change 
 */
	static boolean symNameIs(String name){
		String tok = pr.substring(fname, lname);
		return tok.equals(name);
	}

/*	State is not changed by find or mustFind. Returned value is
	sole purpose of find. That plus setting err for mustFind. 
 */
	static int find( int from, int upto, char c) {
		int x = from;
		int stop = endapp-1;
		while(x<stop) {
			if( pr.charAt(x) != c && x<upto) ++x;
			else break;
		}
		return x<upto ? x : 0;
	}

/*	same as find but sets err on no match 
 */
	static int mustFind( int from, int upto, char c, int err ) {
		int x = find(from, upto, c);
		if( x!=0 ) return x;
		else { eset(err); return 0; }
	}
	
/*	special find for end of string. Minds the old null ending.
 */
	static int findEOS( int x ) {
		while( x<endapp) {
			if( pr.charAt(x)==0 || pr.charAt(x)==0x22 ) return x;
			++x;
		}
		eset(CURSERR);
		return 0;
	}

/*	skip over comments and/or white space in any order, new version
	tolerates 0x0d's, and implements // as well as old slash-star comments.
 */
	static void rem() {
		int c;
		while(cursor<endapp-2) {
			c = pr.charAt(cursor);
			// skip whitespace 
			while( cursor<endapp-2 &&           
						( c==' ' || c==0x09 || c==0x0a || c==0x0d )  ) {
				c = pr.charAt(++cursor);
			}
			// if not comment flag it must be code
			if( !( lit(xcmnt)||lit(xcmnt2) ) ) {
//System.err.println("Expr~129 cursor: "+cursor);
				return;
			}
			// eat text to beginning of next line
			while(cursor<endapp-1) {
				c = pr.charAt(cursor);
				if( c!=0x0a || c!=0x0d ) ++cursor;
				else break;
			}
			// repeat the above for more comments
		}
		return;
	}

	public static void main(String args[]) {
pr="   foo   ";
		symName();
		System.out.println("PT~154 first,last = "+fname+", "+lname);
	}
}
