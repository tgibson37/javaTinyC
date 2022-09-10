/*	Stack: local stack, basic push/pop methods.
 *	Requires TJ.class, specifically STACKSIZE.
 */
package tg37.tinyc;
import java.util.*;

public class Stack extends PT {
	static StackImpl stack;
	static TJ tj;

    private static Stack instance;
    private Stack(){}
    public static synchronized Stack getInstance(){
        if(instance == null){
            instance = new Stack();
            stack = StackImpl.getInstance();
            tj = TJ.getInstance();
        }
        return instance;
    }
    /* used by machinecall 
    public Stuff[] argsToArray(int nargs){
System.err.println("Stack~23: CODE LATER "+nargs);
return null;
    }
*/
    /* basic pusher */
    public void pushStuff(Stuff stuff) {
//dumpSourceLine("pushing "+stuff);
        stack.push(stuff);
    }
    /* basic popper, entry stays accessible until pushed over */
    public Stuff popst() {
        return stack.pop();
    }
    Stuff peek(int argp) {
    	return stack.peek(argp);
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
        Stuff top = popst();
        return top.getInt();
    }
    /* push an int */
    public void pushk(int datum) {
        pushStuff( new Ival(datum) );
    }

    /* push an int as a class 1 */
    public void pushPtr(int datum) {
        pushStuff( new Pval(datum) );
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
    public void dump(String msg){
    	System.err.print(msg);
    	stack.dump();
    }

// tests...
	private static void kase(Stuff s, String sb, Stack stk) {
        stk.pushStuff(s);
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
        str = new Sval("This is a string: via getStr(), then via getInt()");
        System.out.println(" proper call: "+((Sval)str).getStr());
//        System.out.println(" intentional getInt() call:");
//        kase( str, "9s", stk );
//        System.out.println("      and eset(TYPEERR)");
    }
}

/*	Ref: https://contactsunny.medium.com/stack-implementation-example-in-java-9e2923fab87e
 */
class StackImpl {
    private List<Stuff> list = new ArrayList<Stuff>();

    private static StackImpl instance;
	static TJ tj;
	static Dialog dl;
    private StackImpl(){}
    public static synchronized StackImpl getInstance(){
        if(instance == null){
            instance = new StackImpl();
            tj = TJ.getInstance();
            dl = Dialog.getInstance();
        }
        return instance;
    }
    
    public int size() {
        return list.size();
    }
    void push(Stuff value) {
//Thread.dumpStack();
//System.out.println("Stk~143 push: "+value.toString());
//dl.dumpLine("Stk~144");
        list.add(value);
    }
    Stuff pop() {
        if (this.list.isEmpty()) {
            tj.eset(tj.POPERR);
        }
        Stuff value = this.list.get(this.list.size() - 1);
        this.list.remove(this.list.size() - 1);
//System.out.println("Stk~152 pop: "+value.toString());
//dl.dumpLine("Stk~152");
        return value;
    }
    Stuff peek(int argp) {
    	if(this.list.size()<argp) {
    		tj.eset(tj.POPERR);
    	}
    	return this.list.get(argp);
    }
    Stuff peekTop() {
        if (this.list.isEmpty()) {
            tj.eset(tj.POPERR);
        }
        Stuff value = this.list.get(this.list.size() - 1);
        return value;
    }
    List getStackAndEmpty() {
        List stack = new ArrayList<Stuff>(this.list);
        this.list.removeAll(stack);
        return stack;
    }
    void dump(){
    	System.out.print("Stack dump, (top is last): "+this.list);
    }
//    void flush() {
//        this.list = new ArrayList<Stuff><>();
//    }
}
