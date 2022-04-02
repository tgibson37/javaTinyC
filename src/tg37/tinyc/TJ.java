/* Tiny-c, Java version: manifest constants and globals.
 */
package tg37.tinyc;

public class TJ {
	public static void eset(int e){
		error = e; errat = cursor;
	}
//sizes
//static int STACKSIZE =    100;
static int PRSIZE    =  10000;

/* Global data */
static String pr;
static int error;    // from list below. ZERO is good.
static int errat;
static int cursor;   // index into pr
static int fname,lname;  // most recently matched symbol
static int endapp;

// booleans
static int TRUE = 1;
static int FALSE = 0;

/* type flags */
static int CHAR = 0;
static int INT  = 1;
static int STRING = 2;

/* error tags */
static int STATERR =      1;
static int CURSERR =      2;
static int SYMERR  =      3;
static int RPARERR =      5;
static int RANGERR =      6;
static int CLASERR =      7;
static int TYPEERR =      8;
static int SYNXERR =      9;
static int LVALERR =      14;
static int POPERR  =      15;
static int PUSHERR =      16;
static int TMFUERR =      17;
static int TMVRERR =      18;
static int TMVLERR =      19;
static int LINKERR =      20;
static int ARGSERR =      21;
static int LBRCERR =      22;
static int RBRCERR =      23;
static int MCERR   =      24;
static int EQERR   =      27;
static int PTRERR  =      28;
static int APPERR  =      29;
static int DIVERR  =      30;
static int VARERR  =      31;

/* signals */
static int EXIT =         98;
static int KILL =         99;

}