/*	Stack: local stack, basic push/pop methods.
 *	Requires TJ.class, specifically STACKSIZE, Stuff.class which defines
 *	a Stack entry.
 */
package tg37.tinyc;
import java.util.*;

public class Stack extends TJ {
//	public static StackImpl<Stuff> stack = new StackImpl<Stuff>();
	static StackImpl<Stuff> stack = new StackImpl<Stuff>();

	/* basic pusher */
	public static void pushst(Stuff stuff) {
		stack.push(stuff);
	}
	/* basic popper, entry stays accessible until pushed over */
	public static Stuff popst() {
		return stack.pop();
	}
	public static Stuff peekTop() { return stack.peekTop(); }
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
/*	Ref: https://contactsunny.medium.com/stack-implementation-example-in-java-9e2923fab87e
 */
class StackImpl<Stuff> {
    private List<Stuff> list = new ArrayList<Stuff>();
    public long size() { return this.list.size(); }
    void push(Stuff value) { list.add(value); }
    Stuff pop() {
        if (this.list.isEmpty()) { PT.eset(TJ.POPERR); }
        Stuff value = this.list.get(this.list.size() - 1);
        this.list.remove(this.list.size() - 1);
        return value;
    }
    Stuff peekTop() {
        if (this.list.isEmpty()) { PT.eset(TJ.POPERR); }
        Stuff value = this.list.get(this.list.size() - 1);
        return value;
    }
    List<Stuff> getStackAndEmpty() {
        List<Stuff> stack = new ArrayList<Stuff>(this.list);
        this.list.removeAll(stack);
        return stack;
    }
//    void flush() {
//        this.list = new ArrayList<>();
//    }
}
