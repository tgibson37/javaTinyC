/*	Tests for and parses constant. Cursor moved just beyond constant. 
 */
package tg37.tinyc;

public class Expr extends PT {
	static boolean trace=false;
/* An ASGN is a reln or an lvalue = asgn. Note that reln can match an lvalue.
 */
public static boolean asgn(){ 
if(trace)System.err.println("asgn~9: " + pr.charAt(cursor) );
	if(reln()){
		if(lit(xeq)){
			asgn();
//			if(error==0)eq();      actions covered for now
		}
	}
	return error!=0;
}

private static int topdiff() { return Stack.topdiff(); }
private static void pushone() { Stack.pushone(); }
private static void pushzero() { Stack.pushzero(); }

/* a RELN is an expr or a comparison of exprs
 */
static boolean reln(){
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
//System.err.print("Expr~54");
			if(expr()){
//System.err.println("Expr~56");
				if(topdiff()>0)pushone();
				else pushzero();
			}
		}
		else if(lit(xlt)){
//System.err.print("Expr~62");
			if(expr()){
//System.err.println("Expr~64");
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
static boolean expr(){
if(trace)System.err.println("expr~72: " + pr.charAt(cursor) );
	if(lit(xminus)){    /* unary minus */
		term();
		Stack.pushk(-Stack.toptoi());
	}
	else if(lit(xplus)){
		term();
		Stack.pushk(Stack.toptoi());
	}
	else term();
	while(error==0){    /* rest of the terms */
		int leftclass = Stack.stack[Stack.nextstack-1].dtod;
		int rightclass;
		if(lit(xminus)){
			term();
			rightclass = Stack.stack[Stack.nextstack-1].dtod;
			int b=Stack.toptoi();
			int a=Stack.toptoi();
			if( rightclass>0 || leftclass>0 ) Stack.pushPtr(a-b);
			else Stack.pushk(a-b);
		}
		else if(lit(xplus)){
			term();
			rightclass = Stack.stack[Stack.nextstack-1].dtod;
			int b=Stack.toptoi();
			int a=Stack.toptoi();
			if( rightclass>0 || leftclass>0 ) Stack.pushPtr(a+b);
			else Stack.pushk(a+b);
		}
		else return true;   /* is expression, all terms done */
	}
	return false;   /* error code, set down deep */
}

/* a TERM is a factor or a product of factors.
 */
static boolean term() {
if(trace)System.err.println("term~109: " + pr.charAt(cursor) );
	factor();
	while(error==0) {
		if(lit(xstar)){
			factor();
			if(error==0)Stack.pushk(Stack.toptoi()*Stack.toptoi());
		}
		else if(lit(xslash)){
			if(pr.charAt(cursor)=='*' || pr.charAt(cursor)=='/') {
				--cursor;    /* opps, its a comment */
				return true;
			}
			factor();
			int denom = Stack.toptoi();
			int numer = Stack.toptoi();
			if(denom != 0){
				int div = numer/denom;
				if(error==0)Stack.pushk(div);
			}
			else eset(DIVERR);
		}
		else if(lit(xpcnt)){
			factor();
			int b=Stack.toptoi();
			int a=Stack.toptoi();
			if(b>0){
				int pct = a%b;
				if(error==0)Stack.pushk(pct);
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
static void factor() {
if(trace)System.err.println("factor~151: " + pr.charAt(cursor) );
	int cur;
	if(lit(xlpar)) {
		asgn();
		cur=mustFind( cursor, cursor+5, ')' , RPARERR );
		if(cur>0) cursor = cur+1; /*after the paren */
		return;
	} 

	Stuff kon=konst();
	if( kon!=null ) Stack.pushst(kon);  // All below info already in kon
		// AND pushst 

//	{
//		Stuff.Type type;
//		switch(kon.getType()){
//		case 'I': 
//			pushk( Stuff );  /* integer, use private atoi */
//			break;
//		case 'C':
//			char ch = pr.charAt(fname);
//			Stuff s = new Cval(ch);
//			pushst( 0, 'A', type, s );
//			break;
//		case 'S':		/* special type used ONLY here, quoted string */
//			String str = new Sval(fname,lname);
//			pushst( 1, 'A', Char, str );
//			break;
//		}
//	}

//	else if( symName() ) {
//		cursor = lname+1;
//		int where, len, class, obsize, stuff;
//		if( symNameIs("MC") ) { 
//			enter(0); return;
//		} else {
//			struct var *v = addrval();  /* looks up symbol */
//			if( !v ){ eset(SYMERR); return; } /* no decl */
//		  	char* where = (*v).value.up;
//		  	int integer =  (*v).value.ui; 
//		  	int character = (*v).value.uc;
//		  	int class=(*v).dtod; 
//	  		int type=(*v).type; 
//	  		int obsize = typeToSize(class,type);
//	  		int len=(*v).len;
//		  	if( class=='E' ) enter(where);  /* fcn call */
//			else {   /* is var name */
//				if( lit(xlpar) ) {		       /* is dimensioned */
//			  		if( !class ) {   /* must be class>0 */
//						eset(CLASERR);
//			  		} else {  /* dereference the lvalue */
//			  			/* reduce the class and recompute obsize */
//	  					obsize = typeToSize(--class,type);
//			  			/* increment where by subscript*obsize */
//		        		asgn(); if( error!=0 )return;
//		        		lit(xrpar);
//			      		int subscript = toptoi();
//						if(len-1)if( subscript<0 || subscript>=len )eset(RANGERR); 
//						where += subscript * obsize;
//						foo.up = where;
//						pushst( class, 'L', type, &foo);
//						return;
//					}
//				} else {	
//				/* is simple. Must push as 'L', &storagePlace. */
//					if(class==1){
//						foo.up = &((*v).value.up);
//					}
//					else{
//						foo.up = where;
//					}
//			  		pushst( class, 'L', type, &foo);
//				}
//			}
//		}
//	}
	else {
		eset(SYNXERR);
	}
}


//  good java below
	public static Stuff konst() {
		int lname,fname;
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
	public static void main(String args[]){
/*
pr = "  77  \"foo\"  ";
//    012345 6789 01234567890
		System.out.println("Test case: "+pr);
		System.out.println("           0123456789012345678901234567890");
		System.out.println("cursor/endapp: "+ cursor + " " + endapp);
		con = konst();
		System.out.println("  Should be 77, is: "+con);
		System.out.println("cursor/endapp: "+ cursor + " " + endapp);
		con = konst();
		System.out.println("  Should be foo, is: "+con);
		System.out.println("cursor/endapp: "+ cursor + " " + endapp);
*/
//pr="  7<9  7>9  7<=9  7>=9  7==9  7!=9    11  "; //101001
//pr="  7<7  7>7  7<=7  7>=7  7==7  7!=7    11  "; //001110
pr="  77  \"foo\"  ";
//  01234567890123456789
		endapp = pr.length();
		cursor = 0;
		System.out.println("running Expr.main");
		System.out.println("pr: -->>"+pr+"<<--");
/*		Stuff con;
		con = konst();
		System.out.println("constant is "+con);
		con = konst();
		System.out.println("constant is "+con);
 */
		cursor = 0;
		while(cursor<endapp-2){
//System.err.println("Expr~307 cursor: "+cursor);
			boolean b = asgn();
//System.err.println("Expr~309 cursor: "+cursor);
			int x = Stack.toptoi();
			System.out.println("error: "+b + ", expression is " + x);
		}
	}
}
