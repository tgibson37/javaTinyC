/*	Tests for and parses constant. Cursor moved just beyond constant. 
 */
package tg37.tinyc;

public class Expr extends PT {
/* An ASGN is a reln or an lvalue = asgn. Note that reln can match an lvalue.
 */
public static boolean asgn(){ 
	if(reln()){
		if(lit(xeq)){
			asgn();
//			if(error==0)eq();      actions covered for now
		}
	}
	return error!=0;
}

/* a RELN is an expr or a comparison of exprs
 */
static boolean reln(){
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
static boolean expr(){
	if(lit(xminus)){    /* unary minus */
		term();
		pushk(-toptoi());
	}
	else if(lit(xplus)){
		term();
		pushk(toptoi());
	}
	else term();
	while(error==0){    /* rest of the terms */
		int leftclass = stack[nxtstack-1].dtod;
		int rightclass;
		if(lit(xminus)){
			term();
			rightclass = stack[nxtstack-1].dtod;
			int b=toptoi();
			int a=toptoi();
			if( rightclass || leftclass) pushPtr(a-b);
			else pushk(a-b);
		}
		else if(lit(xplus)){
			term();
			rightclass = stack[nxtstack-1].dtod;
			int b=toptoi();
			int a=toptoi();
			if( rightclass || leftclass) pushPtr(a+b);
			else pushk(a+b);
		}
		else return true;   /* is expression, all terms done */
	}
	return false;   /* error code, set down deep */
}

/* a TERM is a factor or a product of factors.
 */
static boolean term() {
	factor();
	while(error==0) {
		if(lit(xstar)){
			factor();
			if(error==0)pushk(toptoi()*toptoi());
		}
		else if(lit(xslash)){
			if(pr.charAt(cursor)=='*' || pr.charAt(cursor)=='/') {
				--cursor;    /* opps, its a comment */
				return 1;
			}
			factor();
			int denom = toptoi();
			int numer = toptoi();
			if(denom){
				int div = numer/denom;
				if(error==0)pushk(div);
			}
			else eset(DIVERR);
		}
		else if(lit(xpcnt)){
			factor();
			int b=toptoi();
			int a=toptoi();
			if(b){
				int pct = a%b;
				if(error==0)pushk(pct);
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
//	union stuff foo;
	Type type;
	int x;
	if(lit(xlpar)) {
		asgn();
		if( (x=mustFind( cursor, cursor+5, ')' , RPARERR )) ) {
			cursor = x+1; /*after the paren */
		}
	} 
	else if( (type=konst()) ) {
	/* Defines fname,lname. Returns Type. 
	   void pushst( int class, int lvalue, Type type, void* stuff );
	*/
		switch(type){
		case Int: 
			pushk( atoi() );  /* integer, use private atoi */
			break;
		case Char:
			char ch = pr.charAt(fname);
			Stuff s = new Cval(ch);
			pushst( 0, 'A', type, s );
			break;
		case CharStar:		/* special type used ONLY here */
			String str = new Sval(fname,lname);
			pushst( 1, 'A', Char, str );
		case Err:
			return;
		}
	}
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
		pr = "  77  \"foo\"  ";
		endapp = pr.length();
		cursor = 0;
		System.out.println("running Expr.main");
		System.out.println("Test case: "+pr);
		System.out.println("           0123456789012345678901234567890");
		System.out.println("cursor/endapp: "+ cursor + " " + endapp);
		Stuff con = konst();
		System.out.println("  Should be 77, is: "+con);
		System.out.println("cursor/endapp: "+ cursor + " " + endapp);
		con = konst();
		System.out.println("  Should be foo, is: "+con);
		System.out.println("cursor/endapp: "+ cursor + " " + endapp);
	}
}