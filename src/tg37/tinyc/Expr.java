/*	Tests for and parses constant. Cursor moved just beyond constant.
 */
package tg37.tinyc;

public class Expr extends PT {
    static boolean trace=false;
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
        int arg = stk.size();    // needed BELOW
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
trace=true;
//System.err.println("Expr~69, haveArgs, error"+tj.error);
//System.err.println("Expr~70, nargs,arg,stk.size: "+nargs+" "+arg+" "+stk.size());
//System.err.println("== cursor+-9 ==>>"+tj.prog.substring(tj.cursor-9,tj.cursor+9)+"<<--");
                if(tj.error!=0)return;
                if( asgn()) ++nargs;
                else break;  // break on error
if(trace)System.err.println("Expr~75, parsed an arg, nargs: "+nargs);
if(trace)System.err.println("   cursor-->"+tj.prog.substring(tj.cursor,tj.cursor+9));
			} while( lit(xcomma) );
        }
if(trace)System.err.println("Expr~77 AFTER args parsed: nargs,arg,stk.size: "+nargs+" "+arg+" "+stk.size());
        if(tj.error!=0)return;
        lit(xrpar);   // optional )
        rem();
        if(where==0) {
            if(stk.size()>0) {
                        MC.machinecall( nargs );
//                        varargs=0;
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
System.err.println("Expr~104, int setArg");
				  } while(lit(xcomma));
				  lit(xsemi);    // optional
			}
			else if ( lit(xchar)) {
System.err.println("Expr~106, char arg");
if(trace)System.err.println("Expr~109, nargs,arg,stk.size: "+nargs+" "+arg+" "+stk.size());
				do {
					setArg(TJ.Type.CHAR, arg);
					arg++;
				} while(lit(xcomma));
				lit(xsemi);
//System.err.println("Expr~108, char arg");
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
//System.err.println("Expr~125, nargs,arg,stk.size: "+nargs+" "+arg+" "+stk.size());
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
//System.err.println("Expr~131, error: "+tj.error);
//if(trace)System.err.println("Expr~120, nargs,arg,stk.size: "+nargs+" "+arg+" "+stk.size());
//System.err.println("Expr~141, nargs,arg,stk.size: "+nargs+" "+arg+" "+stk.size());
		if(tj.error==0)stmt.st();     //  <<-- execute fcn's body
		else {
			dl.whatHappened();
			System.exit(1);
		}
//System.err.println("Expr~100, char arg");
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
        if(trace)System.err.println("reln~187: " + tj.prog.charAt(tj.cursor) );
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
        if(trace)System.err.println("expr~233: " + tj.prog.charAt(tj.cursor) );
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
        if(trace)System.err.println("term~270: " + tj.prog.charAt(tj.cursor) );
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
        	int integer, character;
        	Boolean isArray;
            int where, len, obsize, stuff;
            TJ.Type type;
            tj.cursor = tj.lname+1;
            if( symNameIs("MC") ) {
                enter(0);
                return;
            } else {
                Var v = vt.addrval();  /* looks up symbol */
                if( v==null ) {
                    tj.eset(tj.SYMERR);    /* not declared */
                    return;
                }

				integer =  v.value.getInt();
//				character = v.value.uc;
				isArray=v.isArray;
				type=v.type;
				obsize = typeToSize(isArray,type);
				len=v.len;

                if( v.value.isFcn() ) {
System.err.println("Expr~343 is function");
                    where = v.value.getInt();
                    enter(where);
                }
                else {   /* is var name */
System.err.println("Expr~347 is var");
System.err.println("   cursor-->"+tj.prog.substring(tj.cursor,tj.cursor+9));
                    if( v.isArray ) {
if(trace)System.err.println("Expr~350 is array");
/*
						// reduce the class and recompute obsize
						obsize = typeToSize(--class,type);
						// increment where by subscript*obsize
						asgn(); if( error!=0 )return;
						lit(xrpar);
						int subscript = toptoi();
						if(len-1)if( subscript<0 || subscript>=len )eset(RANGERR);
						where += subscript * obsize;
						foo.up = where;
						pushst( class, 'L', type, &foo);
						return;
*/
                    } else {
if(trace)System.err.println("Expr~365 is simple");
				// is simple. Must push as 'L', &storagePlace.
						if(v.isArray){
	//						foo.up = &((*v).value.up);
						}
						else{
	//						foo.up = where;
						}
//						pushst( class, 'L', type, &foo);
if(trace)System.err.println("Expr~372:   <<== pushst");  // didn't get here

                    }
                }
            }
        }
        else {
            tj.eset(tj.SYNXERR);
        }
    }

    public Stuff konst() {
		int x;  //index into prog
        rem();
        char c = tj.prog.charAt(tj.cursor);
if(trace)System.err.println("Expr~390(konst), cursor-->"+c);
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
if(trace)System.err.println("Expr~402");
            tj.fname=tj.cursor;
            x = findEOS(tj.fname);
            if( x>0 ) {
                /* set lname = last char, cursor = lname+2 (past the quote) */
                tj.lname = x; /*at the quote */
                tj.cursor = x+1; /*after the quote */
if(trace)System.err.println("Expr~409, x: should be quote: "
	+x+"-->"+tj.prog.charAt(x));
            }
            else {
if(trace)System.err.println("Expr~413");
                tj.eset(tj.CURSERR);
                return null;
            }
System.err.println("Expr~417 -->"
	+tj.prog.substring(tj.fname,tj.lname)+"<--");
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

// Unit tests...
    public static void main(String args[]) {
    	System.out.println("Use another main, Expr's is closed");
/*        exp = new Expr();
        stk = new Stack();
        String pr0 ="  77  \"foo\"    7<9  7>9  7<=9  7>=9  7==9  7!=9    88  "; //77foo 101001 88
        String pr1 ="  77  \"foo\"    7<7  7>7  7<=7  7>=7  7==7  7!=7    88  "; //77foo 001110 88
        String pr2 ="         (1+2)*3  3*(1+2)  (1+2*3)  1+(2*3) 88 ";       // null null 9 9 7 7 88
//           012345678901234567890123456789012345678901234567890123456789
//                     1         2         3         4         5
        prog = pr2;
// tests...
        endapp = prog.length();
        cursor = 0;
        System.out.println("running Expr.main");
        System.out.println("prog: -->>"+prog+"<<--");
        Stuff con;
        con = expr.konst();
        System.out.println("constant is "+con);
        con = expr.konst();
        System.out.println("constant is "+con);
        while(cursor<endapp-2) {
            boolean b = expr.asgn();
            int x = stk.toptoi();
            System.out.println("error: "+b + ", expression is " + x);
        }
 */
    }
}
