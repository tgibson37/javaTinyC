/*	Parse tools: boolean lit, boolean symNameIs, int find, int mustFind,
 *	int findEOS, void rem. Returned ints are indexes into pr. Only lit and rem
 *	bump the cursor.
 */

package tg37.tinyc;

public class PT extends TJ {

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
    boolean symName() {
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
    boolean symNameIs(String name) {
        String tok = pr.substring(fname, lname);
        return tok.equals(name);
    }

    /*	State is not changed by find or mustFind. Returned value is
    	sole purpose of find. That plus setting err for mustFind.
     */
    int find( int from, int upto, char c) {
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
    int mustFind( int from, int upto, char c, int err ) {
        int x = find(from, upto, c);
        if( x!=0 ) return x;
        else {
            eset(err);
            return 0;
        }
    }

    /*	special find for end of string. Minds the old null ending.
     */
    int findEOS( int x ) {
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
    void rem() {
        char c;
        while(cursor<endapp-2) {
            c = pr.charAt(cursor);
            // skip whitespace
            while( cursor<endapp-2 &&
                    ( c==' ' || c==0x09 || c==0x0a || c==0x0d )  ) {
                c = pr.charAt(++cursor);
            }
            // if not comment flag it must be code
            if( !( lit(xcmnt)||lit(xcmnt2) ) ) {
                return;
            }
            // eat text to beginning of next line
            while(cursor<endapp-1) {
                c = pr.charAt(cursor);
                if( c!=0x0a && c!=0x0d ) ++cursor;
                else break;
            }
            // repeat the above for more comments
        }
        return;
    }
    /*
    	public void main(String args[]) {
    pr="   foo   ";
    		symName();
    		System.out.println("PT~154 first,last = "+fname+", "+lname);
    	}
     */
}
