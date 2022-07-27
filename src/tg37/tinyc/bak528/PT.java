/*	Parse tools: boolean lit, boolean symNameIs, int find, int mustFind,
 *	int findEOS, void rem. Returned ints are indexes into prog. Only lit and rem
 *	bump the cursor.
 */

package tg37.tinyc;

public class PT {
	TJ tj;
    public PT(TJ tj) {
    	this.tj = tj;
    }

    /************** literals **************/
    final String xif = "if";
    final String xelse = "else";
    final String xint = "int";
    final String xchar = "char";
    final String xwhile = "while";
    final String xreturn = "return";
    final String xbreak = "break";
    final String xendlib = "endlibrary";
    final String xr = "r";
    final String xg = "g";
    final String xlb = "[";
    final String xrb = "]";
    final String xlpar = "(";
    final String xrpar = ")";
    final String xcomma = ",";
    final String newline = "\n";
    final String xcmnt = "/*";
    final String xcmnt2 = "//";
    final String xstar = "*";
    final String xsemi = ";";
    final String xpcnt = "%";
    final String xslash = "/";
    final String xplus = "+";
    final String xminus = "-";
    final String xlt = "<";
    final String xgt = ">";
    final String xnoteq = "!=";
    final String xeqeq = "==";
    final String xeq = "=";
    final String xge = ">=";
    final String xle = "<=";
    final String xnl = "\n";
    final String xvarargs = "...";
    final String xquote = "\"";

    int fname,lname;

    /* Bump cursor over whitespace. Then return true on match and advance
       cursor beyond the literal else false and do not advance cursor
     */
    boolean lit(String s) {
        int first,last;
        first = last = tj.cursor;
        int stop = tj.endapp-s.length();
        while(first < stop) {
            char c = tj.prog.charAt(first);
            if( c == ' ' || c == '\t' ) ++first;
            else break;
        }
        tj.cursor = first;
        last = first+s.length();
        if( last<tj.endapp && s.equals(tj.prog.substring(first,last)) ) {
            tj.cursor += s.length();
            return true;
        }
        return false;
    }
    /* Parse a symbol defining fname, lname. ret: true if symbol.
     *      Advances the cursor to but not over the symbol,
     */
    boolean symName() {
        int temp;
        char c = tj.prog.charAt(tj.cursor);
        while( c == ' ' || c == '\t' ) c = tj.prog.charAt(++tj.cursor);
        temp=tj.cursor;
        if( Character.isLetter(c) || c=='_') fname = temp;
        else return false;
        while( Character.isLetterOrDigit(c=tj.prog.charAt(++temp)) || c=='_') ;
        lname = temp;
        return true;  /* good, fname and lname defined */
    }

    /*	return true if symname matches arg, no state change
     */
    boolean symNameIs(String name) {
        String tok = tj.prog.substring(tj.fname, tj.lname);
        return tok.equals(name);
    }

    /*	State is not changed by find or mustFind. Returned value is
    	sole purpose of find. That plus setting err for mustFind.
     */
    int find( int from, int upto, char c) {
        int x = from;
        int stop = tj.endapp-1;
        while(x<stop) {
            if( tj.prog.charAt(x) != c && x<upto) ++x;
            else break;
        }
        return x<upto ? x : 0;
    }

    /*	same as find but sets err on no match
     */
    int mustFind( int from, int upto, char c, int err ) {
        int x = find(from, upto, c);
        if( x!=0 ) return x;
        else {
            tj.eset(err);
            return 0;
        }
    }

    /*	special find for end of string. Minds the old null ending.
     */
    int findEOS( int x ) {
        while( x<tj.endapp) {
            if( tj.prog.charAt(x)==0 || tj.prog.charAt(x)==0x22 ) return x;
            ++x;
        }
        tj.eset(tj.CURSERR);
        return 0;
    }

    /*	skip over comments and/or white space in any order, new version
    	tolerates 0x0d's, and implements // as well as old slash-star comments.
     */
    void rem() {
        char c;
        while(tj.cursor<tj.endapp-2) {
            c = tj.prog.charAt(tj.cursor);
            // skip whitespace
            while( tj.cursor<tj.endapp-2 &&
                    ( c==' ' || c==0x09 || c==0x0a || c==0x0d )  ) {
                c = tj.prog.charAt(++tj.cursor);
            }
            // if not comment flag it must be code
            if( !( lit(xcmnt)||lit(xcmnt2) ) ) {
                return;
            }
            // eat text to beginning of next line
            while(tj.cursor<tj.endapp-1) {
                c = tj.prog.charAt(tj.cursor);
                if( c!=0x0a && c!=0x0d ) ++tj.cursor;
                else break;
            }
            // repeat the above for more comments
        }
        return;
    }
    /*
    	public void main(String args[]) {
    prog="   foo   ";
    		symName();
    		System.out.println("PT~154 first,last = "+fname+", "+lname);
    	}
     */
}
