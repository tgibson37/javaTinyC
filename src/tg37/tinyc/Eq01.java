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
System.err.print("\nEq01~25, eq(): ");
		int value = stk.toptoi();
		Stuff lval = stk.popst();
System.err.println("value,lval: "+value+" "+lval);  // 7, Ival:5
		lval.setInt(value);
System.err.println("After setInt, lval: "+lval);  // Ival:5
		stk.pushStuff(lval);
		stk.dump("After push: ");
System.err.println("");
	}
}
