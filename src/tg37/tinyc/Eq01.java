/*	Tests for and parses constant. Cursor moved just beyond constant.
 */
package tg37.tinyc;

public class Eq01 extends PT {
	static Stack stk;
	
    private static Eq01 instance;
    private Eq01(){}
    public static synchronized Eq01 getInstance(){
        if(instance == null){
            instance = new Eq01();
			stk = Stack.getInstance();
//			stmt = ST.getInstance();
//			vt = Vartab.getInstance();
//			dl = Dialog.getInstance();
        }
        return instance;
    }

/* SITUATION: Parsed an assignment expression. Two stack entries, lvalue, datum.
 *	Effects the assignment. 
 */
	public static void eq() {
		if(TJ.traceON);
//stk.dump("\nEq01~26, stk before pops: ");
		int value = stk.toptoi();
		Stuff lval = stk.popst();
		lval.setInt(value);
		stk.pushStuff(lval);
		if(TJ.traceON)stk.dump("Eq01~32, After assignment push: \n");
//stk.dump("\nEq01~32, After assignment push: ");
//System.err.println("");
	}
}
