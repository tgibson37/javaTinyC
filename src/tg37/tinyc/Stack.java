/*	Stack: local stack, basic push/pop methods.
 *	Requires TJ.class, specifically STACKSIZE.
 */
package tg37.tinyc;

public class Stack extends TJ {
	static int nextstack=0;
	static Stuff stack[] = new Stuff[TJ.STACKSIZE];

	/* basic pusher */
	static void pushst(Stuff stuff) {
		if(nextstack >= TJ.STACKSIZE) TJ.error = TJ.PUSHERR;
		else stack[nextstack++] = stuff;
	}
	/* basic popper, entry stays accessible until pushed over */
	static Stuff popst() {
		if( --nextstack < 0 ) { TJ.error = TJ.POPERR; return null; }
		return stack[nextstack];
	}
	/* used by Expr.reln() for relational ops (<=, etc) */
	int topdiff() {
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

        Stuff top = popst();
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