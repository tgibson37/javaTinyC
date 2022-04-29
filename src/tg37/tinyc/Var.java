/*	Variables and var tables: libs, globals, list of locals. 
 *	Search order is active local, globals, libs. 
 */
package tg37.tinyc;
import java.util.*;

public class Var extends PT {
    String name; boolean isArray; Stuff.Type type; int len; 
    Stuff value;
// containers:
	static HashMap<String,Var> libs = new HashMap<String,Var>();
	static HashMap<String,Var> globals = new HashMap<String,Var>();
	static List<Map<String,Var>> locals = new LinkedList<Map<String,Var>>();
	static Map<String,Var> curfun = null;

	public String toString(){
		return name+": "+value;
	}

/* SITUATION: Function call. Push a new vartab for the functions locals */
	static void newfun() {
		curfun = new HashMap<String,Var>();
		locals.add(curfun);
	}

/* SITUATION: function completed. Pop the locals stack, adjust curfun */
	static void fundone() {
		int lsize = locals.size();
        locals.remove(lsize-1);
        if (lsize>1) curfun =  locals.get(lsize-2);
		else curfun = null;
	}

/*********** var tools ****************/
/* copy the argument value into the new local place */
/*	int copyArgValue(struct var *v, int class, Type type, union stuff *passed ) {
			if(passed && class){                                    /* passed pointer
					(*v).value.up = (*passed).up;
			} else if( passed && !class ) {                 /* passed datum
					switch(type){
					case Int:
							put_int( (*v).value.up, (*passed).ui );
							break;
					case Char:
							put_char( (*v).value.up, (*passed).uc );
							break;
					default:
							eset(TYPEERR);
							return TYPEERR;
					}
			}
			return 0;
	}
*/

/* SITUATION: Declaration is parsed, and its descriptive data known. Create
	a Stuff and add it to curfun. Formerly newvar. */
	public Var( boolean isArray, Stuff.Type type, int len, Stuff passed ) {
		this.isArray = isArray;
		this.type = type;
		this.len  = len;
		this.name = pr.substring(fname,lname);
		if(passed!=null)this.value = passed.klone();   // function arg
		else{     // declaration incl function parameter
			if(type==Stuff.Type.INT){ this.value = Stuff.createIval(0); }
			else if(type==Stuff.Type.CHAR){ this.value = Stuff.createCval((char)0); }
			else if(type==Stuff.Type.FCN){ this.value = Stuff.createFvar(cursor); }
		}
		curfun.put(name, this); 
	}
/* looks up a symbol at one level */
	static Var addrval_all(String sym) {
		Var s = curfun.get(sym);
		if(s==null)s = globals.get(sym);
		if(s==null)s = libs.get(sym);
		return s;
	}
/* looks up a symbol pointed to by fname,lname: 
     locals, globals, library levels in that order */
	static Var addrval() {
			String sym = pr.substring(fname,lname);
			return addrval_all(sym);
	}
    static private Map<String,Var> peekTop() {
        if (locals.isEmpty()) { eset(TJ.VARERR); }
        Map<String,Var> var = locals.get(locals.size() - 1);
        return var;
    }

// dumps...
	static void dumpMap(Map m) {
		Set mes = m.entrySet();
//System.err.println("Var~89:  map size: "+m.size());
		Iterator sit = mes.iterator();
		while(sit.hasNext()) { 
			System.err.print(sit.next()+" "); 
		}
		System.err.println();
	}
	static void dumpVarTab(){
		Map lmap;
		System.out.println("  libs"); dumpMap(libs); 
		System.out.println("  globals"); dumpMap(globals);
		System.out.println("  locals"); 
		Iterator it = locals.iterator();
		while(it.hasNext()){ lmap=(Map)it.next(); dumpMap(lmap); } 
	}
/*	Checks for balanced brackets, cursor to stop. */
	static int checkBrackets(int stop) {
//System.err.println("IN checkBrackets, cursor,stop: "+cursor+" "+stop);
//boolean dump = (cursor>=5400);
			int err;
			int save=endapp;  /* _skip uses endapp as limit */
			endapp=stop;
			while(cursor<stop) {
//if(dump)System.err.print(""+pr.charAt(cursor));
//if(dump)System.err.println("==>"+pr.substring(cursor-9,cursor+2)+"<==");
					while(pr.charAt(cursor++) != '[' && cursor<stop) ;
//if(dump)
//System.err.println("Var~114, cursor,pr[cursor]: "+cursor+" "+pr.charAt(cursor));
					if(cursor<stop) {
						err=ST.skip('[',']');
						if( err != 0 )return err;
					}
			}
			endapp=save;
			return 0;
	}

public static void tclink() {
        int x;
        int savedCursor=cursor;
        cursor=0;
        if(checkBrackets(lpr)!=0)eset(RBRCERR+1000);
		if(checkBrackets(apr)!=0)eset(RBRCERR+2000);
		if(checkBrackets(EPR-1)!=0)eset(RBRCERR+3000);
		if(error!=0)Dialog.whatHappened();
        cursor=lpr;
        curfun = libs;
//System.err.println("Var~139 cursor,endapp,EPR,error"+" "
//						+cursor+" "+endapp+" "+EPR+" "+error);
        while(cursor<endapp && error==0){
//System.err.print("    141: "+cursor);
                int lastcur = cursor;
                rem();
//System.err.println("    143: "+cursor);
                if(lit(xlb)){
//System.err.println("Var~144 bracket");
                	ST.skip('[',']');
                }
                else if(ST.decl()) {
//System.err.println("Var~152 decl");
                }
                else if(lit(xendlib)){
//System.err.println("Var~155 endlib");
                        if(curfun==libs) {   /* 1st endlib, assume globals follow */
                                curfun = globals;
                        }
                        else {        // subsequent endlib, merge into libs
                        	//cufun = CODE LATER
                        }
                }
                else if(symName()) {     /* fctn decl */
//System.err.println("Var~164 symbol");
                        cursor = lname+1;   // Parse args and body later
                        Stuff kursor = new Pval(lname);
                        new Var(false, Stuff.Type.FCN, 1, kursor); // ~65: self installed
                        int xxx = mustFind(cursor, endapp, '[',LBRCERR);
//System.err.println("    Var~169 xxx");
                        if(xxx>0) { 
                        	cursor=xxx+1;    // just past the found [
                        	if(ST.skip('[',']')!=0)eset(RBRCERR);
//System.err.println("    Var~173 cursor: "+cursor);
//System.err.println("    Var~174 pr+cursor...: "+pr.substring(cursor,cursor+99));
                        }
                }
                else if(pr.charAt(cursor)=='#'){
//System.err.println("Var~175 #");
                		char s;
                        while(++cursor<endapp) {
							char c = pr.charAt(cursor);
							if( (c==0x0d)||(c=='\n') )break;
                        }
                }
//System.err.println("Var~185 cursor,lastcur: "+cursor+" "+lastcur);
                if(cursor==lastcur)eset(LINKERR);
//System.err.println("Var~187 error: "+error);
        }
        cursor = savedCursor;
}

// tests...
	public static void main(String[] args){
pr="  L1   L2   G3   G4   a1   a2   b1   b2   ..   ..   ..   ..   ..   ..   ..   ";
//  01234567890123456789012345678901234567890123456789012345678901234567890123456789
//            1         2         3         4         5         6         7
		curfun = libs;    // should get L1,L2
		fname=2; lname=4;                        // L1
		new Var(true, Stuff.Type.INT, 1, null);
		for(int i=1; i<8; ++i ) {                // L2...b2
			if(i==2) curfun = globals;			//  G3...G2
			else if(i%2==0) {          // a1,a2 Then b1,b2 in second local frame
				newfun(); 
				curfun=peekTop();      // peekTop retrieves empty 2nd level
			}
			fname += 5; lname += 5;    // a1...b2
			new Var(true, Stuff.Type.INT, 1, null);
		}
		dumpVarTab();
		Var v; Stuff s;
		fname=2; lname=4; v=addrval(); s= v.value;    // L1
		System.out.println(s+" should be 0");
		
		fname=17; lname=19; v=addrval(); s= v.value;   // G4
		System.out.println(s+" should be 0");
		
		fname=32; lname=34;  v=addrval(); s= v.value;   // b1
		System.out.println(s+" should be 0");
	}
}
