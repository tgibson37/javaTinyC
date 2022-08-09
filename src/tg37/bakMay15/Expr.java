/*	Tests for and parses constant. Cursor moved just beyond constant. 
 */
package tg37.tinyc;

public class Expr extends PT {
	boolean trace=true;
// Singleton
	private static Expr instance;
	private Expr(){}
	public static synchronized Expr getInstance(){
		if(instance==null)instance=new Expr();
		return instance;
	}
	private Stack stk = Stack.getInstance();

	public void enter(int where) {   // c code tc~307


if(trace)System.err.println("Expr~19: enter(" + where + ')' );
if(trace)System.err.println(" -> "+pr.substring(where,where+9));
	    int arg = stk.size();    // needed BELOW
        int nargs=0;
        lit(xlpar); 		// optional (
        boolean haveArgs = ! (  lit(xrpar) );  // NOT empty ()
if(trace)System.err.println("Expr~25");        
        if( !haveArgs ) {           			// AND not one of these...
if(trace)System.err.println("Expr~");        
        	char x = pr.charAt(cursor);
        	haveArgs =  ! (     x=='['
							 || x==']'
							 || x==0x3b   // ;
							 || x=='\n'
							 || x==0x0d   // <CR>
							 || x=='/'
						);
		}
if(trace)System.err.println("Expr~37");

        if ( haveArgs ) {
System.err.println("Expr~40");
                do {
                        if(error!=0)return;
                        if( asgn()) ++nargs;
                        else break;  // break on error
                } while( lit(xcomma) );
        }
if(trace){
System.err.print("Expr~47, haveArgs,error,where: ");
System.err.print(" "+haveArgs);
System.err.print(" "+error);
System.err.println(" "+where);
}
        if(error!=0)return;
        lit(xrpar);   // optional )   
        rem();
        if(where==0) {
                if(stk.size()>0) {
if(trace)System.err.println("Expr~53: NEED machinecall, covered for now");
//                        machinecall( nargs );
//                        varargs=0;
//fprintf(stderr,"\n~336E va %d na %d",varargs,nargs);
                }
                else eset(MCERR);
                return;
        }
if(trace)System.err.println("Expr~61");        
if(trace)System.err.println(" ->"+pr.substring(where,where+9));
// ABOVE  ^^^^      BELOW  vvvv

        int localstcurs=stcurs, localcurs=cursor;
        cursor = where;
        newfun();  
        for(;;) {         
            rem();
/*
			if(lit(xint)) { 
                  do {
                      setArg(Int, &stack[arg]);
                      arg++;
                  } while(lit(xcomma));
                  lit(xsemi);    // optional 
            } 
              else if ( lit(xchar)) {
                  do {
                      setArg(Char, &stack[arg]);
                      arg++;
                  } while(lit(xcomma));
                  lit(xsemi);
               }
               else if ( lit(xvarargs) ){
               	   varargs=nargs+1;
                   break;
               }
           else {
               break;
           }
       }
       if(!varargs) {
           if(arg != nxtstack) {
                cursor=localcurs;
                stcurs=localstcurs;
                eset(ARGSERR);
            }
            while(nargs>0){
                popst();
                --nargs;
            }
        }
        if(!error)st();     /*  <<-- execute fcn's body */
        if(!leave)pushzero();
        leave=0;
        cursor=localcurs;
        stcurs=localstcurs;
        fundone();
    }



	}

/* An ASGN is a reln or an lvalue = asgn. Note that reln can match an lvalue.
 */
	public boolean asgn(){ 
	if(trace)System.err.println("asgn~9: " + pr.charAt(cursor) );
		if(reln()){
			if(lit(xeq)){
				asgn();
	//			if(error==0)eq();      actions covered for now
			}
		}
		return error!=0;
	}
	
	private int topdiff() { return stk.topdiff(); }
	private void pushone() { stk.pushone(); }
	private void pushzero() { stk.pushzero(); }

/* a RELN is an expr or a comparison of exprs
 */
	boolean reln(){
	if(trace)System.err.println("reln~26: " + pr.charAt(cursor) );
		if(expr()){
			if(lit(xle)){
				if(expr()){
					if(topdiff()<=0)pushone();
					else pushzero();
				}
			}
			else if(lit(xge)){
				if(expr()){
					if(topdiff()>=0)pushone();
					else pushzero();
				}
			}
			else if(lit(xeqeq)){
				if(expr()){
					if(topdiff()==0)pushone();
					else pushzero();
				}
			}
			else if(lit(xnoteq)){
				if(expr()){
					if(topdiff()!=0)pushone();
					else pushzero();
				}
			}
			else if(lit(xgt)){
				if(expr()){
					if(topdiff()>0)pushone();
					else pushzero();
				}
			}
			else if(lit(xlt)){
				if(expr()){
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
	boolean expr(){
	if(trace)System.err.println("expr~72: " + pr.charAt(cursor) );
		if(lit(xminus)){    /* unary minus */
			term();
			stk.pushk(-stk.toptoi());
		}
		else if(lit(xplus)){
			term();
			stk.pushk(stk.toptoi());
		}
		else term();
		while(error==0){    /* rest of the terms */
			boolean leftIsArray = (stk.peekTop()).isArray;
			boolean rightIsArray;
			if(lit(xminus)){
				term();
				rightIsArray = stk.peekTop().isArray;
				int b=stk.toptoi();
				int a=stk.toptoi();
				if( rightIsArray || leftIsArray ) stk.pushPtr(a-b);
				else stk.pushk(a-b);
			}
			else if(lit(xplus)){  // ISSUE: merge these 2 cases ???
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
	if(trace)System.err.println("term~109: " + pr.charAt(cursor) );
		factor();
		while(error==0) {
			if(lit(xstar)){
				factor();
				if(error==0)stk.pushk( stk.toptoi() * stk.toptoi() );
			}
			else if(lit(xslash)){
				if(pr.charAt(cursor)=='*' || pr.charAt(cursor)=='/') {
					--cursor;    /* opps, its a comment */
					return true;
				}
				factor();
				int denom = stk.toptoi();
				int numer = stk.toptoi();
				if(denom != 0){
					int div = numer/denom;
					if(error==0)stk.pushk(div);
				}
				else eset(DIVERR);
			}
			else if(lit(xpcnt)){
				factor();
				int b=stk.toptoi();
				int a=stk.toptoi();
				if(b>0){
					int pct = a%b;
					if(error==0)stk.pushk(pct);
				}
				else eset(DIVERR);
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
	if(trace)System.err.println("factor~151: " + pr.charAt(cursor) );
		int cur;
		if(lit(xlpar)) {
			asgn();
			cur=mustFind( cursor, cursor+5, ')' , RPARERR );
			if(cur>0) cursor = cur+1; /*after the paren */
			return;
		} 
	
		Stuff kon=konst();
		if( kon!=null ) stk.pushst(kon);
	
		else if( symName() ) {
			int where, len, obsize, stuff;
System.err.println("Expr~219,factor: cursor->" + pr.charAt(cursor) );
			cursor = lname+1;
			if( symNameIs("MC") ) { 
				enter(0); return;
			} else {
				Vartab vt = Vartab.getInstance();
				Var v = vt.addrval();  /* looks up symbol */
System.err.println("Expr~226,factor: v " + v );
				if( v==null ){ eset(SYMERR); return; } /* not declared */
/*
				int integer =  v.value.ui; 
				int character = v.value.uc;
				int class=v.dtod; 
				int type=v.type; 
				int obsize = typeToSize(class,type);
				int len=v.len;
*/
				if( v.value.isFcn() ){
					where = v.value.getInt();
System.err.println("Expr~238,isFcn, where:  " + where );
					enter(where);
				}
				else {   /* is var name */
					if( v.isArray ) {
System.err.println("Expr~184: isArray do later");
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
System.err.println("Expr~200: is simple");
	/*
					// is simple. Must push as 'L', &storagePlace. 
						if(class==1){
							foo.up = &((*v).value.up);
						}
						else{
							foo.up = where;
						}
						pushst( class, 'L', type, &foo);
	*/
					}
				}
			}
		}
		else {
			eset(SYNXERR);
		}
	}

//  good java below
	public Stuff konst() {
//		int lname,fname;
		int x;  //index into pr
		rem();
		char c = pr.charAt(cursor);
		if( c=='+' || c=='-' || (c>='0'&&c<='9') ) {
			fname = cursor;
			do{
				++cursor; c=pr.charAt(cursor);
			} while(c>='0'&&c<='9' && cursor<endapp);
			lname=cursor;

			
			
			String s = pr.substring(fname,lname);
			int i = Integer.parseInt(s);
			return new Ival(i);


		} else if(lit("\"")) {
			fname=cursor;
			x = findEOS(fname);
			if( x>0 ) {
			/* set lname = last char, cursor = lname+2 (past the quote) */
				lname = x; /*at the quote */
				cursor = x+1; /*after the quote */
			}
			else { eset(CURSERR); return null; }
			return new Sval(pr.substring(fname,lname));
	
		} else if(lit("\'")) {
			fname=cursor;
			/* lname = last char, cursor = lname+2 (past the quote) */
			x=mustFind(fname+1,fname+2,'\'',CURSERR);
			if( x>0 ) {
				lname = x-1; 
				cursor = x+1;
			}
			else { eset(CURSERR); return null; }
			return new Cval(pr.charAt(fname));
		
		} else return null;  /* no match */
	}
	
// Unit tests...
	public static void main(String args[]){
		Expr ex = Expr.getInstance();
		Stack stk = Stack.getInstance();
String pr0 ="  77  \"foo\"    7<9  7>9  7<=9  7>=9  7==9  7!=9    88  "; //77foo 101001 88
String pr1 ="  77  \"foo\"    7<7  7>7  7<=7  7>=7  7==7  7!=7    88  "; //77foo 001110 88
String pr2 ="         (1+2)*3  3*(1+2)  (1+2*3)  1+(2*3) 88 ";       // null null 9 9 7 7 88
//           012345678901234567890123456789012345678901234567890123456789
//                     1         2         3         4         5
pr = pr2;
// tests...
		endapp = pr.length();
		cursor = 0;
		System.out.println("running Expr.main");
		System.out.println("pr: -->>"+pr+"<<--");
		Stuff con;
		con = ex.konst();
		System.out.println("constant is "+con);
		con = ex.konst();
		System.out.println("constant is "+con);
		while(cursor<endapp-2){
			boolean b = ex.asgn();
			int x = stk.toptoi();
			System.out.println("error: "+b + ", expression is " + x);
		}
	}
}