package tg37.tinyc;

public class Dialog extends PT {
    static TJ tj = TC.tj;   // Dialog's copy, declared up in base PT
	
    private static Dialog instance;
    private Dialog(){}
    public static synchronized Dialog getInstance(){
        if(instance == null){
            instance = new Dialog();
        }
        return instance;
    }
    
    public int countch(int f, int t, char c) {
        int k=1;   /* start on line 1 */
        while( f++ <= t) if(tj.prog.charAt(f)==c) ++k;
        return k;
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
        do {
            c = tj.prog.charAt(k);
            if(c==0x0a||c==0x0d)break;
        } while( --k >= -1);
        return k;
    }
    /* returns index to last character of the current line */
    public int lchar(int k) {
        char c;
        do {
            c = tj.prog.charAt(k);
            if(c==0x0a||c==0x0d)break;
        } while( ++k < tj.endapp);
        return k-1;
    }

    /*      Prints end of program message, "done" if no error, else code and
     *      line with error and carot under.
     */
    public void whatHappened() {
        if(tj.error==tj.KILL) errToWords();
        else if(tj.error!=0) {
            int fc, lc;
            int firstSignif=0, blanks, lineno;
System.err.println("Dialog~72: errat,EPR = "+tj.errat+" "+tj.EPR);
            char e = tj.prog.charAt(tj.errat);
            if(e==0x0a||e==0x0d)--tj.errat;
            if(tj.errat<tj.lpr) {
                System.out.println("\nseed ");
                lineno=0;
            }
            else if(tj.errat<tj.apr) {
                lineno = countch(tj.lpr,tj.errat,(char)0x0a);
                if(lineno<=0)lineno = countch(0,tj.errat,(char)0x0d);
                System.out.print("\nlib ");
            }
            else {
                lineno = countch(tj.apr,tj.errat,(char)0x0a);
                if(lineno<=0)lineno = countch(tj.apr,tj.errat,(char)0x0d);
                System.out.print("\napp ");
            }
            System.out.print("line "+lineno+" (cursor prog["+tj.errat+"])");
//
//                errToWords();
//                fc=fchar(errat);
//                while(fc+firstSignif < EPR){
//                	char c = prog.charAt(fc+firstSignif);
//                	if(c==' ' || c=='\t') ++firstSignif;
//                }
//                lc=lchar(errat);
//                pft(fc,lc);
//                System.out.println("");
//                pft(fc,fc+firstSignif-1);        /* leading whitespace */
//                blanks=errat-fc-firstSignif-1;   /* blanks to carot */
//                while(--blanks >= 0) System.out.print(" ");
//                System.out.println("^");

        }
        else {
            if(!tj.quiet)System.out.println("\ndone");
        }
    }

    void showLine(int line) {
        int fc, lc;
        fc=fchar(line);
        lc=lchar(line);
        pft(fc,lc);
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
        System.out.print(tj.prog.substring(f,t));
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