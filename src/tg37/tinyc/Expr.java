/*	Tests for and parses constant. Cursor moved just beyond constant. 
 */
package tg37.tinyc;

public class Expr extends PT {
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