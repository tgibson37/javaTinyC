/*	Stack: local stack, basic push/pop methods.
 *	Requires TJ.class, specifically STACKSIZE, Stuff.class which defines
 *	a Stack entry.
 */
package tg37.tinyc;
import java.util.*;

public class Stack {
	StackImpl<Stuff> stack;
	TJ tj;
    public void Stack(TJ tj) {
    	this.tj = tj;
    	stack = new StackImpl<Stuff>();
    }

    /* basic pusher */
    public void pushst(Stuff stuff) {
        stack.push(stuff);
    }
    /* basic popper, entry stays accessible until pushed over */
    public Stuff popst() {
        return stack.pop();
    }
    public Stuff peekTop() {
        return stack.peekTop();
    }
    /* used by Expr.reln() for relational ops (<=, etc) */
    public int topdiff() {
        int b = toptoi();
        int a = toptoi();
        return ( a-b );
    }
    /* pop the stack returning its int value. dtod is distance to datum, 0 or 1.
    	It used to be called class.
     */
    public int toptoi() {
        int datum=9999999;
        Stuff stf;
        Stuff top = popst();
        if( top.isArray ) {
            if(top.lvalue) {
                if(top.isNum()) datum=top.getInt();
                else tj.eset(tj.LVALERR);
            }
            else datum=(int)(top.getInt());
        }
        else

            if(top.lvalue) {
                if(top.isNum()) datum  = top.getInt();
                else tj.eset(tj.TYPEERR);
            }
            else {
                tj.eset(tj.LVALERR);
            }
        return datum;
    }

    /* push an int */
    public void pushk(int datum) {
        pushst( new Ival(datum) );
    }

    /* push an int as a class 1 */
    public void pushPtr(int datum) {
        pushst( new Pval(datum) );
    }

    /* these two used by RELN */
    public void pushone() {
        pushk(1);
    }
    public void pushzero() {
        pushk(0);
    }
    /* public access to StackImpl methods */
    public int size() {
        return stack.size();
    }

// tests...
    private static void kase(Stuff s, String sb, Stack stk) {
        stk.pushst(s);
        System.out.print("Should be:"+sb);
        int x = stk.toptoi();
        System.out.println(": "+x);
    }
    public static void main(String[] args) {
    	Stack stk = new Stack();
        Stuff str;
        System.out.println("running Stack.main");
        kase( new Ival(77), "77", stk );
        kase( new Cval('A'), "65", stk );
        str = new Sval("A string via getStr(), then via getInt()");
        System.out.println(" proper call: "+((Sval)str).getStr());
        System.out.println(" intentional getInt() call:");
        kase( str, "9s", stk );
        System.out.println("      and eset(TYPEERR)");
    }
}
/*	Ref: https://contactsunny.medium.com/stack-implementation-example-in-java-9e2923fab87e
 */
class StackImpl<Stuff> extends TJ {
    private List<Stuff> list = new ArrayList<Stuff>();
    public int size() {
        return list.size();
    }
    void push(Stuff value) {
        list.add(value);
    }
    Stuff pop() {
//System.err.println("\n\nStack~95, cursor: "+TJ.cursor);
        if (this.list.isEmpty()) {
            eset(POPERR);
        }
        Stuff value = this.list.get(this.list.size() - 1);
        this.list.remove(this.list.size() - 1);
        return value;
    }
    Stuff peek(int argp) {
    	if(this.list.size()<argp) {
    		eset(POPERR);
    	}
    	return this.list.get(argp);
    }
    Stuff peekTop() {
        if (this.list.isEmpty()) {
            eset(POPERR);
        }
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
