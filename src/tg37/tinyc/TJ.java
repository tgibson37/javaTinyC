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
static int lpr, apr, endapp, prused, EPR;
/* EPR is end of program SPACE. 
 *      pr starts with startSeed, then libs, then app, then values
 *      lpr is start of libraries
 *      apr is start of application program
 *      endapp is end of ALL program text, 
 *      endapp+10 start of value space
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