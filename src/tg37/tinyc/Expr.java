/*	Tests for and parses constant. Cursor moved just beyond constant.
 */
package tg37.tinyc;

public class Expr extends PT {
//    static boolean trace=false;
	static Stack stk;
	static ST stmt;
	static Vartab vt;
	static Dialog dl;

    private static Expr instance;
    private Expr(){}
    public static synchronized Expr getInstance(){
        if(instance == null){
            instance = new Expr();
			stk = Stack.getInstance();
			stmt = ST.getInstance();
			vt = Vartab.getInstance();
			dl = Dialog.getInstance();
        }
        return instance;
    }
 void at(int line){
// 	 System.err.println("Expr at: "+line+" lpr: "+tj.lpr);
 }

 /* Situation: parsing argument declarations, passed values are on the stack.
 * argp points into stack to an argument of type. 
 * Gets actual value of arg, calls valloc which parses and sets
 * up local with the passed value.
 */ 
	void setArg( TJ.Type type, int arg ) {
		Stuff valPassed = stk.peek(arg);
//System.err.println("Expr~35 setArg: "+valPassed);
/*		boolean isarray = valPassed.isArray;
        boolean lvalue = valPassed.lvalue;
        TJ.Type stacktype = valPassed.type;
        if( lvalue) {
            where = valPassed.up;
            if( isarray ) { 
                valPassed.up = *((char**)(*arg).value.up);
                 
            } else
            if( stacktype==Int ) valPassed.ui = get_int(where);
            else if( stacktype==Char) valPassed.ui = get_char(where);
        }
*/
        stmt.varAlloc( type, valPassed);

    }

    public void enter(int where) {   // c code tc~307
        int arg = stk.size();    // index to first parsed arg, if any
        int nargs=0;
        int varargs=0;
        lit(xlpar); 		// optional (
        char x = tj.prog.charAt(tj.cursor);
        boolean haveArgs =  ! ( lit(xrpar)
            	                || x=='['
                                || x==']'
                                || x==0x3b   // ;
                                || x=='\n'
                                || x==0x0d   // <CR>
                                || x=='/'
                          );
        if ( haveArgs ) {
            do {
//stk.dump("\n==>> Expr~69, args loop, ");
//dumpSourceLine("");
                if(tj.error!=0)return;
                if( asgn()) ++nargs;
                else break;  // break on error
			} while( lit(xcomma) );
        }
//stk.dump("\nExpr~76");
        if(tj.error!=0)return;
        lit(xrpar);   // optional )
        rem();
        if(where==0) {
            if(stk.size()>0) {
                MC.machinecall( nargs );
//              varargs=0;
            }
            else tj.eset(tj.MCERR);
            return;
        }
// ABOVE  ^^^^   parse the args, set cursor
// BELOW  vvvv   parse the parameters, declare/set their values, st() the body

		int localstcurs=tj.stcurs, localcurs=tj.cursor;
        tj.cursor = where;
        vt.newfun();

        for(;;) {
            rem();
			if(lit(xint)) {
				  do {
					  setArg(TJ.Type.INT, arg);
					  arg++;
				  } while(lit(xcomma));
				  lit(xsemi);    // optional
			}
			else if ( lit(xchar)) {
				do {
					setArg(TJ.Type.CHAR, arg);
					arg++;
				} while(lit(xcomma));
				lit(xsemi);
		   }
//		   else if ( lit(xvarargs) ){
//			   varargs=nargs+1;
//			   break;
//		   }
		   else {
			   break;
		   }
	   }
// assure number of args == number of parms, clean up...
	   if(varargs==0) {
		   if(arg != stk.size()) {
				tj.cursor=localcurs;
				tj.stcurs=localstcurs;
				tj.eset(tj.ARGSERR);
			}
			while(nargs>0){
				stk.popst();
				--nargs;
			}
		}
// */
		if(tj.error==0)stmt.st();     //  <<-- execute fcn's body
		else {
			dl.whatHappened();
			System.exit(1);
		}
		if(!tj.leave)pushzero();
		tj.leave=false;
		tj.cursor=localcurs;
		tj.stcurs=localstcurs;
		vt.fundone();
    }




    /* An ASGN is a reln or an lvalue = asgn.
     */
    public boolean asgn() {
        if(reln()) {
            if(lit(xeq)) {
                asgn();
                //			if(error==0)eq();      actions covered for now
            }
        }
//System.err.println("Expr~168, returning from asgn, error: "+tj.error);
        return tj.error==0;
    }

    private int topdiff() {
        return stk.topdiff();
    }
    private void pushone() {
        stk.pushone();
    }
    private void pushzero() {
        stk.pushzero();
    }

    /* a RELN is an expr or a comparison of exprs
     */
    boolean reln() {
        if(expr()) {
            if(lit(xle)) {
                if(expr()) {
                    if(topdiff()<=0)pushone();
                    else pushzero();
                }
            }
            else if(lit(xge)) {
                if(expr()) {
                    if(topdiff()>=0)pushone();
                    else pushzero();
                }
            }
            else if(lit(xeqeq)) {
                if(expr()) {
                    if(topdiff()==0)pushone();
                    else pushzero();
                }
            }
            else if(lit(xnoteq)) {
                if(expr()) {
                    if(topdiff()!=0)pushone();
                    else pushzero();
                }
            }
            else if(lit(xgt)) {
                if(expr()) {
                    if(topdiff()>0)pushone();
                    else pushzero();
                }
            }
            else if(lit(xlt)) {
                if(expr()) {
                    if(topdiff()<0)pushone();
                    else pushzero();
                }
            }
            else return true;  /* just expr is a reln */
        }
        return false;   /* not an expr is not a reln */
    }

    /* an EXPR is a term or sum (diff) of terms.
     */
    boolean expr() {
        if(lit(xminus)) {   /* unary minus */
            term();
            stk.pushk(-stk.toptoi());
        }
        else if(lit(xplus)) {
            term();
            stk.pushk(stk.toptoi());
        }
        else term();
        while(tj.error==0) {   /* rest of the terms */
            boolean leftIsArray = (stk.peekTop()).isArray;
            boolean rightIsArray;
            if(lit(xminus)) {
                term();
                rightIsArray = stk.peekTop().isArray;
                int b=stk.toptoi();
                int a=stk.toptoi();
                if( rightIsArray || leftIsArray ) stk.pushPtr(a-b);
                else stk.pushk(a-b);
            }
            else if(lit(xplus)) { // ISSUE: merge these 2 cases ???
                term();
                rightIsArray = stk.peekTop().isArray;
                int b=stk.toptoi();
                int a=stk.toptoi();
                if( rightIsArray || leftIsArray ) stk.pushPtr(a+b);
                else stk.pushk(a+b);
            }
            else return true;   /* is expression, all terms done */
        }
        return false;   /* error code, set down deep */
    }

    /* a TERM is a factor or a product of factors.
     */
    boolean term() {
        factor();
        while(tj.error==0) {
            if(lit(xstar)) {
                factor();
                if(tj.error==0)stk.pushk( stk.toptoi() * stk.toptoi() );
            }
            else if(lit(xslash)) {
                if(tj.prog.charAt(tj.cursor)=='*' || tj.prog.charAt(tj.cursor)=='/') {
                    --tj.cursor;    /* opps, its a comment */
                    return true;
                }
                factor();
                int denom = stk.toptoi();
                int numer = stk.toptoi();
                if(denom != 0) {
                    int div = numer/denom;
                    if(tj.error==0)stk.pushk(div);
                }
                else tj.eset(tj.DIVERR);
            }
            else if(lit(xpcnt)) {
                factor();
                int b=stk.toptoi();
                int a=stk.toptoi();
                if(b>0) {
                    int pct = a%b;
                    if(tj.error==0)stk.pushk(pct);
                }
                else tj.eset(tj.DIVERR);
            }
            else return true;  /* no more factors */
        }
        return false;
    }
    /* a FACTOR is a ( asgn ), or a constant, or a variable reference, or a function
        reference. NOTE: factor must succeed or it esets SYNXERR. Callers test error
        instead of a returned true/false. This varies from the rest of the expression
        stack.
     */
    void factor() {
        int cur;
        if(lit(xlpar)) {
            asgn();
            cur=mustFind( tj.cursor, tj.cursor+5, ')', tj.RPARERR );
            if(cur>0) tj.cursor = cur+1; /*after the paren */
            return;
        }

        Stuff kon=konst();
        if( kon!=null ) stk.pushStuff(kon);
        else if( symName() ) {
//dumpSourceLine("parsed symName:");dumpSym(" ");    // <<== parsed 'b' OK
            tj.cursor = tj.lname;
            if( symNameIs("MC") ) {
                enter(0);
                return;
            } else {
                Var v = vt.addrval();  /* looks up symbol */
                if( v==null ) {
                    tj.eset(tj.SYMERR);    /* not declared */
                    return;
                }
				TJ.Type type=v.type;
                if( v.value.isFcn() ) {
                    int where = v.value.getInt();
                    enter(where);
                }
                else {   /* is var name */
                    if( v.value.isArray ) {
                    	Stuff element = resolve(v.value);
						stk.pushStuff( element );
                    } else {
						stk.pushStuff( v.value );
                    }
                }
            }
        }
        else {
            tj.eset(tj.SYNXERR);
        }
    }
	/* If ( return the subscripted element, else the array itself as lvalue. */
    Stuff resolve(Stuff array) {
System.err.println("Expr~337: not coded yet");
return null;
    }

    public Stuff konst() {
		int x;  //index into prog
        rem();
        char c = tj.prog.charAt(tj.cursor);
        if( c=='+' || c=='-' || (c>='0'&&c<='9') ) {
            tj.fname = tj.cursor;
            do {
                ++tj.cursor;
                c = tj.prog.charAt(tj.cursor);
            } while(c>='0'&&c<='9' && tj.cursor<tj.endapp);
            tj.lname = tj.cursor;
            String s = tj.prog.substring(tj.fname,tj.lname);
            int i = Integer.parseInt(s);
            return new Ival(i);
        } else if(lit("\"")) {
            tj.fname=tj.cursor;
            x = findEOS(tj.fname);
            if( x>0 ) {
                /* set lname = last char, cursor = lname+2 (past the quote) */
                tj.lname = x; /*at the quote */
                tj.cursor = x+1; /*after the quote */
            }
            else {
                tj.eset(tj.CURSERR);
                return null;
            }
            return new Sval(tj.prog.substring(tj.fname,tj.lname));

        } else if(lit("\'")) {
            tj.fname=tj.cursor;
            /* lname = last char, cursor = lname+2 (past the quote) */
            x=mustFind(tj.fname+1,tj.fname+2,'\'',tj.CURSERR);
            if( x>0 ) {
                tj.lname = x-1;
                tj.cursor = x+1;
            }
            else {
                tj.eset(tj.CURSERR);
                return null;
            }
            return new Cval(tj.prog.charAt(tj.fname));

        } else return null;  /* no match */
    }

/* stored size of one datum */
	int typeToSize( Boolean isArray, TJ.Type type ) {
			if(type==TJ.Type.CHAR)return 1;
			else if(type==TJ.Type.INT)return 4;
			else tj.eset(tj.TYPEERR);
			return 0;
	}
}
