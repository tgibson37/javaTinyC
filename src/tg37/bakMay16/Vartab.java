/*	Variables and var tables: libs, globals, list of locals.
 *	Search order is active local, globals, libs.
 */
package tg37.tinyc;
import java.util.*;

public class Vartab extends PT {
    ST stmt = ST.getInstance();
// var containers:
    public static HashMap<String,Var> libs = new HashMap<String,Var>();
    public static HashMap<String,Var> globals = new HashMap<String,Var>();
    public static List<Map<String,Var>> locals = new LinkedList<Map<String,Var>>();
    public static Map<String,Var> curfun = null;

// Singleton
    private static Vartab instance;
    private Vartab() {}
    public static synchronized Vartab getInstance() {
        if(instance==null)instance=new Vartab();
        return instance;
    }

    /* SITUATION: Function call. Push a new vartab for the functions locals */
    void newfun() {
        curfun = new HashMap<String,Var>();
        locals.add(curfun);
    }

    /* SITUATION: function completed. Pop the locals stack, adjust curfun */
    void fundone() {
        int lsize = locals.size();
        locals.remove(lsize-1);
        if (lsize>1) curfun =  locals.get(lsize-2);
        else curfun = null;
    }

    /* looks up a symbol at one level */
    Var addrval_all(String sym) {
        Var s = curfun.get(sym);
        if(s==null)s = globals.get(sym);
        if(s==null)s = libs.get(sym);
        return s;
    }
    /* looks up a symbol pointed to by fname,lname:
         locals, globals, library levels in that order */
    Var addrval() {
        String sym = pr.substring(fname,lname);
        return addrval_all(sym);
    }
    private Map<String,Var> peekTop() {
        if (locals.isEmpty()) {
            eset(TJ.VARERR);
        }
        Map<String,Var> var = locals.get(locals.size() - 1);
        return var;
    }

    void dumpMap(Map m) {
        Set mes = m.entrySet();
        Iterator sit = mes.iterator();
        while(sit.hasNext()) {
System.err.print(sit.next()+" ");
        }
System.err.println();
    }
    void dumpVarTab() {
        Map lmap;
        System.out.println("  libs");
        dumpMap(libs);
        System.out.println("  globals");
        dumpMap(globals);
        System.out.println("  locals");
        Iterator it = locals.iterator();
        while(it.hasNext()) {
            lmap=(Map)it.next();
            dumpMap(lmap);
        }
    }
    /*	Checks for balanced brackets, cursor to stop. */
    int checkBrackets(int stop) {
        int err;
        int save=endapp;  /* _skip uses endapp as limit */
        endapp=stop;
        while(cursor<stop) {
            while(pr.charAt(cursor++) != '[' && cursor<stop) ;
            if(cursor<stop) {
                err=stmt.skip('[',']');
                if( err != 0 )return err;
            }
        }
        endapp=save;
        return 0;
    }

    public void tclink() {
        int x;
        int savedCursor=cursor;
        cursor=0;
        if(checkBrackets(lpr)!=0)eset(RBRCERR+1000);
        if(checkBrackets(apr)!=0)eset(RBRCERR+2000);
        if(checkBrackets(EPR-1)!=0)eset(RBRCERR+3000);
        if(error!=0)Dialog.whatHappened();
        cursor=lpr;
        curfun = libs;
        while(cursor<endapp && error==0) {
//boolean dump = cursor>5450;
//if(dump){
// System.err.println("Var~135, cursor: "+cursor);
// System.err.println(pr.substring(cursor-5,cursor)
// 	 +"==>>"+pr.substring(cursor,cursor+9));
//}
            int lastcur = cursor;
//if(dump)System.err.println("Var139, error: "+error);
            rem();
//if(dump)System.err.println("Var141, error: "+error);
            if(lit(xlb)) {
                stmt.skip('[',']');
            }
            else if(stmt.decl()) {
            }
            else if(lit(xendlib)) {
//System.err.println("Var148: "+xendlib);
                if(curfun==libs) {   /* 1st endlib, assume globals follow */
                    curfun = globals;
                }
                else {        // subsequent endlib, merge into libs
                    //cufun = CODE LATER
                }
            }
            else if(symName()) {     /* fctn decl */
                cursor = lname+1;
                Stuff kursor = new Pval(lname);
                new Var(false, Stuff.Type.FCN, 1, kursor); // ~65: self installed
                int xxx = mustFind(cursor, endapp, '[',LBRCERR);
                if(xxx>0) {
                    cursor=xxx+1;    // just past the found [
                    if(stmt.skip('[',']')!=0)eset(RBRCERR);
                }
            }
            else if(pr.charAt(cursor)=='#') {
                char s;
                while(++cursor<endapp) {
                    char c = pr.charAt(cursor);
                    if( (c==0x0d)||(c=='\n') )break;
                }
            }
            if(cursor==lastcur) {
                eset(LINKERR);
            }
        }
        cursor = savedCursor;
    }

// tests...
    public static void main(String[] args) {
        System.out.println("Use:   ./r Var");
    }
}
