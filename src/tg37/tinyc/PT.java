/*	Parse tools: boolean lit, boolean symNameIs, int find, int mustFind,
 *	int findEOS, void rem. Returned ints are indexes into prog. Only lit and rem
 *	bump the cursor.
 */

package tg37.tinyc;

public class PT {
	public static TJ tj;
//	public static Dialog dl;
	public static Eq01 eq;
	
	public PT(){ 
		tj = TC.tj;
		eq = Eq01.getInstance();
	}
		
    /************** literals **************/
    static String xif = "if";
    static String xelse = "else";
    static String xint = "int";
    static String xchar = "char";
    static String xwhile = "while";
    static String xreturn = "return";
    static String xbreak = "break";
    static String xendlib = "endlibrary";
    static String xr = "r";
    static String xg = "g";
    static String xlb = "[";
    static String xrb = "]";
    static String xlpar = "(";
    static String xrpar = ")";
    static String xcomma = ",";
    static String newline = "\n";
    static String xcmnt = "/*";
    static String xcmnt2 = "//";
    static String xstar = "*";
    static String xsemi = ";";
    static String xpcnt = "%";
    static String xslash = "/";
    static String xplus = "+";
    static String xminus = "-";
    static String xlt = "<";
    static String xgt = ">";
    static String xnoteq = "!=";
    static String xeqeq = "==";
    static String xeq = "=";
    static String xge = ">=";
    static String xle = "<=";
    static String xnl = "\n";
    static String xvarargs = "...";
    static String xquote = "\"";

//    static int fname,lname;

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
        if( Character.isLetter(c) || c=='_') tj.fname = temp;
        else return false;
        while( Character.isLetterOrDigit(c=tj.prog.charAt(++temp)) || c=='_') ;
        tj.lname = temp;
        return true;  /* good, fname and lname defined */
    }
    /* dump the most recently parsed symbol (or constant) */
    public static void dumpSym(String msg){
    	System.out.print(msg + ": ->"+tj.prog.substring(tj.fname,tj.lname)+"<-\n");
    }
/* dump current line with ^ under cursor
    void dumpLine(String msg){
System.err.println("PT~94 ");
    	System.out.print(msg+"PT94");
    	dl.dumpLine(msg);
    }
*/
    /* dump the current line number. 
    	Ref: https://www.recitalsoftware.com/blogs/152-howto-use-file-and-line-in-java-programs
     */
	public static void dumpSourceLine(String msg) {
		System.out.print("\n"+msg);
		System.out.print(" at " + 
					   " "+new Throwable().getStackTrace()[1].getFileName() +
//					   " "+new Throwable().getStackTrace()[1].getClassName() +
					   " "+new Throwable().getStackTrace()[1].getMethodName() +
				  " line "+new Throwable().getStackTrace()[1].getLineNumber());
	}
    /*	return true if symname matches arg, no state change
     */
    boolean symNameIs(String name) {
        String tok = tj.prog.substring(tj.fname, tj.lname);
//System.err.println("PT~96 symNameIs: name,tok: "+name+" "+tok+"<--");
        return tok.equals(name);
    }

    /*	State is not changed by find or mustFind. Returned value is
    	sole purpose of find.
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

    /*	Same as find but sets err on no match.
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
}
