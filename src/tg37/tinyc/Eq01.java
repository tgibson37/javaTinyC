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

/* SITUATION: Parsed an assignment expression. Two stack entries, lvalue, datam.
 *	Effects the assignment. 
 */
	public static void eq() {
		int value = stk.toptoi();
		Stuff lval = stk.popst();
		lval.setInt(value);
		stk.pushStuff(lval);
	}
}
