package tg37.tinyc;

public class Dialog extends PT {

void at(int line){ System.err.println("at Dialog line "+line); }
	
    private static Dialog instance;
    private Dialog(){
    }
    public static synchronized Dialog getInstance(){
        if(instance == null){
            instance = new Dialog();
        }
        return instance;
    }
    /* count occurances of c from prog[f] to prog[t] inclusive */
    public int countch(int f, int t, char c) {
        int k=1;   /* start on line 1 */
        while( f++ <= t) if(tj.prog.charAt(f)==c) ++k;
        return k;
    }
    /* return code area name at prog[kursor] */
    public String codeArea(int kursor) {
		if(kursor < tj.lpr) return "\nseed ";
		else if(kursor < tj.apr) return "\nlib ";
		else return "\napp ";
    }
/* return line number relative to beginning of code area */
    public int codeLineInArea(int kursor) {
		if(kursor < tj.lpr) return 0;
		else if(kursor < tj.apr) return countch(tj.lpr,kursor,(char)0x0a);
		else {
			int lineno = countch(tj.apr,kursor,(char)0x0a);
			if(lineno<=0)lineno = countch(tj.apr,kursor,(char)0x0d);
			return lineno;
		}
    }
    public void errToWords() {
        String x="";
        switch(tj.error) {
        case 2:
            x="CURSERR, cursor out of range";
            break;
        case 3:
            x="SYMERR, decl needed";
            break;
        case 1:
            x="STATERR";
            break;
        case 5:
            x="RPARERR, ) missing";
            break;
        case 6:
            x="RANGERR, subscript out of range";
            break;
        case 7:
            x="CLASERR";
            break;
        case 8:
            x="TYPEERR";
            break;
        case 9:
            x="SYNXERR";
            break;
        case 14:
            x="LVALERR, not assignable";
            break;
        case 15:
            x="POPERR, nothing to pop";
            break;
        case 16:
            x="PUSHERR, overflowed stack area";
            break;
        case 17:
            x="TMFUERR, overflowed function table";
            break;
        case 18:
            x="TMVRERR, overflowed variable table";
            break;
        case 19:
            x="TMVLERR, overflowed available space for values";
            break;
        case 20:
            x="LINKERR";
            break;
        case 21:
            x="ARGSERR, args don't match";
            break;
        case 22:
            x="LBRCERR, [ required";
            break;
        case 23:
            x="RBRCERR, ] required somewhere";
            break;
        case 24:
            x="MCERR, no such MC";
            break;
        case 26:
            x="SYMERRA, decl needed";
            break;
        case 27:
            x="EQERR, illegal assign";
            break;
        case 28:
            x="PTRERR";
            break;
        case 29:
            x="APPERR, app not found";
            break; // lrb
        case 30:
            x="DIVERR, divide by zero";
            break;
        case 98:
            x="EXIT, stopped by exit call";
            break;
        case 99:
            x="KILL, stopped by user";
            break;
        case 1023:
            x="RBRCERR, ] required in -r ";
            break;
        case 2023:
            x="RBRCERR, ] required in library";
            break;
        case 3023:
            x="RBRCERR, ] required in app code";
            break;
        }
        System.out.println(x);
    }
    /* returns index to first character of the current line */
    public int fchar(int k) {
        char c;
        while( --k >= 0){
            c = tj.prog.charAt(k);
            if(c==0x0a||c==0x0d)break;
        }
        return k==0 ? 0 : k+1;
    }
    /* returns index to last character of the current line */
    public int lchar(int k) {
        char c;
        do {
            c = tj.prog.charAt(k);
            if(c==0x0a||c==0x0d)break;
        } while( ++k < tj.endapp);
        return k;
    }

    /*      Prints end of program message, "done" if no error, else code and
     *      line with error and carot under.
     */
    public void whatHappened() {
        if(tj.error==tj.KILL) errToWords();
        else if(tj.error!=0) {
            int fc, lc;
            int firstSignif=0, blanks, lineno;
            char e = tj.prog.charAt(tj.errat);
            if(e==0x0a||e==0x0d)--tj.errat;
            System.out.println( codeArea(tj.errat) );
            lineno = codeLineInArea(tj.errat);

/*
            if(tj.errat < tj.lpr) {
                System.out.println("\nseed ");
                lineno=0;
            }
            else if(tj.errat < tj.apr) {
                lineno = countch(tj.lpr,tj.errat,(char)0x0a);
                if(lineno<=0)lineno = countch(0,tj.errat,(char)0x0d);
                System.out.print("\nlib ");
            }
            else {
                lineno = countch(tj.apr,tj.errat,(char)0x0a);
                if(lineno<=0)lineno = countch(tj.apr,tj.errat,(char)0x0d);
                System.out.print("\napp ");
            }
*/
            System.out.print("line "+lineno+" (cursor prog["+tj.errat+"]): ");
			errToWords();
			fc=showLine();
			showPosition();
        }
        else {
            if(!tj.quiet)System.out.println("\ndone");
        }
    }
    public void dumpLine(String msg) {
    	System.err.print(msg+"...\n");
    	showLine();
    	showPosition();
    }
    int showLine() {
        int fc, lc;
        fc=fchar(tj.cursor);
        lc=lchar(tj.cursor);
        pft(fc,lc);
        pc('\n');
        return fc;
    }
    void showPosition(){
		int fc=fchar(tj.cursor);
		int lc=lchar(tj.cursor);
		int firstSignif=fc;
		while(true){
			char c = tj.prog.charAt(firstSignif);
			if( (c==' ')||(c=='\t') ) ++firstSignif;
			else break;
		}
		int blanks=tj.errat-firstSignif-1;   /* blanks to carot */
		pft(fc,firstSignif);        /* leading whitespace */
		while(--blanks >= 0) ps(" ");
		ps("^\n");
	}

    /************ simple prints ******************/
    void ps(String s) {
        System.out.print(s);
    }
    void pl(String s) {
        System.out.print("\n"+s);
    }
    int  pn(int n)   {
        System.out.print(n);
        return n;
    }
    void pc(char c)  {
        System.out.print(c);
    }
    void pft(int f,int t) {
		String x = tj.prog.substring(f,t);
        System.out.print(x);
    }
/* show just one char on its own line with arrows to make a tab evident */
    void dumpchar(String label, int pos){
    	char c = tj.prog.charAt(pos);
    	System.err.println("\n"+label+"-->"+c+"<--");
	}

    public void logo() {
        if(tj.quiet)return;
        System.out.println(
            "***  TINY-C VERSION 1.0,  COPYRIGHT 1977, T A GIBSON  ***"
        );
        System.out.println(
            "        This C version copyright 2022, T A Gibson"
        );
    }
}