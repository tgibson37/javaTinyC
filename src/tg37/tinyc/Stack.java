/*	Stack: local stack, basic push/pop methods.
 *	Requires TJ.class, specifically STACKSIZE, Stuff.class which defines
 *	a Stack entry.
 */
package tg37.tinyc;

public class Stack extends TJ {
	public static int nextstack=0;
	public static Stuff stack[] = new Stuff[TJ.STACKSIZE];

	/* basic pusher */
	public static void pushst(Stuff stuff) {
		if(nextstack >= TJ.STACKSIZE) TJ.error = TJ.PUSHERR;
		else stack[nextstack++] = stuff;
	}
	/* basic popper, entry stays accessible until pushed over */
	public static Stuff popst() {
		if( --nextstack < 0 ) { TJ.error = TJ.POPERR; return null; }
		return stack[nextstack];
	}
	/* used by Expr.reln() for relational ops (<=, etc) */
	public static int topdiff() {
		int b = toptoi();
		int a = toptoi();
		return ( a-b );
	}
/* pop the stack returning its int value. dtod is distance to datum, 0 or 1.
	It used to be called class.
 */
	public static int toptoi() {
        int datum=9999999;
        Stuff stf;
//System.err.print("Stack~33 nxtstack before pop: "+nextstack);
        Stuff top = popst();
//System.err.println(" after: "+nextstack+", top: "+top);
/*        if( top.dtod==1 ) {
                if(top.lvalue == 'L') {
                        if(t.isNum()) datum=top.getInt();
                        else eset(LVALERR);
                }
                else datum=(int)(top.value.up);
        }
        else
*/
        if(top.lvalue == 'L') {
                if(top.isNum()) datum  = top.getInt();
                else eset(TYPEERR);
        }
        else if(top.lvalue == 'A') {
                if(top.isNum()) datum  = top.getInt();
                else eset(TYPEERR);
        }
        else { eset(LVALERR); }
        return datum;
    }

/* push an int */
	public static void pushk(int datum) {
			pushst( new Ival(datum) );
	}

/* push an int as a class 1 */
	public static void pushPtr(int datum) {
			pushst( new Pval(datum) );
	}

/* these two used by RELN */
	public static void pushone() {
			pushk(1);
	}
	public static void pushzero() {
			pushk(0);
	}

// tests...
	static void kase(Stuff s, String sb) {
		Stack.pushst(s);
		System.out.print("Should be:"+sb);
		int x = Stack.toptoi();
		System.out.println(": "+x);
	}
	public static void main(String[] args){
		Stuff str;
		System.out.println("running Stack.main");
		kase( new Ival(77), "77" );
		kase( new Cval('A'), "65" );
		kase( str = new Sval("A string"), "error msg" );
		System.out.println("Sval as string: "+str.getStr());
	}
}