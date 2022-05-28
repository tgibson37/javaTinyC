/*	Variables and var tables: libs, globals, list of locals.
 *	Search order is active local, globals, libs.
 */
package tg37.tinyc;
import java.util.*;

public class Vartab extends PT {
// variable containers...
    public HashMap<String,Var> libs = new HashMap<String,Var>();
    public HashMap<String,Var> globals = new HashMap<String,Var>();
    public List<Map<String,Var>> locals = new LinkedList<Map<String,Var>>();
    public Map<String,Var> curfun = null;

	ST stmt;
    public  Vartab() {
    	super();
    	this.stmt = tj.stmt;
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
        String sym = tj.prog.substring(fname,lname);
        return addrval_all(sym);
    }
    private Map<String,Var> peekTop() {
        if (locals.isEmpty()) {
            tj.eset(tj.VARERR);
        }
        Map<String,Var> var = locals.get(locals.size() - 1);
        return var;
    }

    void dumpMap(Map m) {
        Set mes = m.entrySet();
        if(m.isEmpty()){
        	System.err.println("empty");
        } else {
			Iterator sit = mes.iterator();
			while(sit.hasNext()) {
				System.err.print(sit.next()+" ");
			}
			System.err.println();
        }
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
        int save=tj.endapp;  /* _skip uses endapp as limit */
        tj.endapp=stop;
        while(tj.cursor<stop) {
            while(tj.prog.charAt(tj.cursor++) != '[' && tj.cursor<stop) ;
            if(tj.cursor<stop) {
                err=stmt.skip('[',']');
                if( err != 0 )return err;
            }
        }
        tj.endapp=save;
        return 0;
    }

    public void tclink() {
        int x;
        int savedCursor=tj.cursor;
        tj.cursor=0;
        if(checkBrackets(tj.lpr)!=0)tj.eset(tj.RBRCERR+1000);
        if(checkBrackets(tj.apr)!=0)tj.eset(tj.RBRCERR+2000);
        if(checkBrackets(tj.EPR-1)!=0)tj.eset(tj.RBRCERR+3000);
        if(tj.error!=0)tj.dl.whatHappened();
        tj.cursor=tj.lpr;
        curfun = libs;
        while(tj.cursor<tj.endapp && tj.error==0) {
//boolean dump = cursor>5450;
//if(dump){
// System.err.println("Var~135, cursor: "+cursor);
// System.err.println(prog.substring(cursor-5,cursor)
// 	 +"==>>"+prog.substring(cursor,cursor+9));
//}
            int lastcur = tj.cursor;
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
                tj.cursor = lname+1;
                Stuff kursor = new Pval(lname);
                new Var(false, TJ.Type.FCN, 1, kursor); // ~65: self installed
                int xxx = mustFind(tj.cursor, tj.endapp, '[',tj.LBRCERR);
                if(xxx>0) {
                    tj.cursor=xxx+1;    // just past the found [
                    if(stmt.skip('[',']')!=0)tj.eset(tj.RBRCERR);
                }
            }
            else if(tj.prog.charAt(tj.cursor)=='#') {
                char s;
                while(++tj.cursor<tj.endapp) {
                    char c = tj.prog.charAt(tj.cursor);
                    if( (c==0x0d)||(c=='\n') )break;
                }
            }
            if(tj.cursor==lastcur) {
                tj.eset(tj.LINKERR);
            }
        }
        tj.cursor = savedCursor;
    }

// tests...
    public static void main(String[] args) {
        System.out.println("Use:   ./r Var");
    }
}
