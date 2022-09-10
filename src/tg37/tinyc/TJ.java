package tg37.tinyc;

/*	Tiny-c, Java version: manifest constants, globals, and eset().
	Accessed from other components via handle 'TJ tj' passed to constructors. 
 */
public class TJ {
// access to components...
	public Expr exp;
	public Stack stk;
	public ST stmt;
	public Vartab vt;
	public Dialog dl;
	public PT pt;
    public enum Type {CHAR, INT, FCN, STR };
    
    private static TJ instance;
    private TJ(){
    	exp=Expr.getInstance();
    	stk=Stack.getInstance();
    	stmt=ST.getInstance();
    	vt=Vartab.getInstance();
    	dl=Dialog.getInstance();
//    	pt=PT.getInstance();
    }
    public static synchronized TJ getInstance(){
        if(instance == null){
            instance = new TJ();
        }
        return instance;
    }

    public void eset(int e) {
//System.err.print("TJ~33 eset: ");
        error = e;
        errat = cursor;
        dl.whatHappened();
//Thread.dumpStack();
        System.exit(0);  // zero because the issue is in the tiny-c code. TC is ok.
    }

    /* Global data */
    static String prog;   // tc program text
    static int  apr, endapp, prused, EPR, lpr;
    /* EPR is end of program SPACE.
     *      pr starts with startSeed, then libs, then app
     *      lpr is start of libraries
     *      apr is start of application program
     *      endapp is end of ALL program text,
     *      prused includes values, moves up/down with fcn entry/leaving
     *      EPR is pointer to last byte of pr array
     */
    static int error;    // from list below. ZERO is good.
    static int errat;	 // where error occurred
    static boolean quiet;
    static int cursor;   // index into pr
    static int stcurs;	 // current statement
    static int fname,lname;  // most recently matched symbol
    static boolean leave;	// set true by return statement
    static boolean brake;	// set true by break statement
// leave and brake are restored to false when their respective action is complete.

    /* type flags */
    static int CHAR = 0;
    static int INT  = 1;
    static int STRING = 2;

    /* error tags */
    int STATERR =      1;
    int CURSERR =      2;
    int SYMERR  =      3;
    int RPARERR =      5;
    int RANGERR =      6;
    int CLASERR =      7;
    int TYPEERR =      8;
    int SYNXERR =      9;
    int LVALERR =      14;
    int POPERR  =      15;
    int PUSHERR =      16;
    int TMFUERR =      17;
    int TMVRERR =      18;
    int TMVLERR =      19;
    int LINKERR =      20;
    int ARGSERR =      21;
    int LBRCERR =      22;
    int RBRCERR =      23;
    int MCERR   =      24;
    int EQERR   =      27;
    int PTRERR  =      28;
    int APPERR  =      29;
    int DIVERR  =      30;
    int VARERR  =      31;

    /* signals */
    int EXIT =         98;
    int KILL =         99;

}