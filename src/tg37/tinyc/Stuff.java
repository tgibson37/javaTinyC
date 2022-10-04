/**	Stuff is a type free datum with description and value.
	It is used on the Stack, and the Vartab. Derivatives
	hold values of type: int, char, string.
NEED: support for vars, and lvalue. Below constructors do just constants.
NEED: lvalue service that returns an actual value Stuff. Expr~20.
	Basically Stuff defines what goes on the Stack.
*/
package tg37.tinyc;
import java.io.StringWriter;
import java.io.PrintWriter;

abstract public class Stuff {
    public TJ.Type type;          // CHAR, INT, FCN, STR
    public int len;            // 1 for datum, else length of array
    public boolean lvalue;
    public boolean isArray;    // used to be 'class,' 0 for datum 1 for array
	static TJ tj;
	static Dialog dl;

	Stuff(TJ.Type t, int l, boolean lv, boolean ia ) {
		tj = TJ.getInstance();
		dl = Dialog.getInstance();
		
		type = t;
        len = l;
        lvalue = lv;
        isArray = ia;
    }
/* Not all Stuff can return an int. If not overridden...  */
    public int getInt(){
System.err.println("Stuff~31: "+this);
Thread.dumpStack();
    	return -999997;
    }
    public void dumpArray(){
    	System.err.println("dumpArray works for Ival");
    }
    public void setInt(int val){}
    public int getInt(int sub){return -999998;}  // return element of array
    public void setInt(int sub, int val){}  // set element of array
    public Stuff getStuff(int sub){return null;}     // return element wrapped as Stuff
    public int[] getIntArray(){tj.eset(tj.TYPEERR);return null;}
    public char[] getCharArray(){tj.eset(tj.TYPEERR);return null;}

    public void dump(String msg){ 
    	System.out.println(msg+this.toString());
    	System.out.println("  type,len,lvalue,isArray: "+type+" "+
    		len+" "+lvalue+" "+isArray );
    }
    public void setConstant() {
    	lvalue=false;
    }
    public boolean isNum() {
        boolean t = isInt() || isChar() || isFcn();  //<<== try it 
        return t;
    }
    public boolean isInt() {
        return type==TJ.Type.INT;
    }
    public boolean isChar() {
        return type==TJ.Type.CHAR;
    }
    public boolean isStr() {
        return type==TJ.Type.STR;
    }
    public boolean isFcn() {
		boolean x = type==TJ.Type.FCN;
        return x;
    }
    
    public static Ival createIval(int i) {
        return new Ival(i);
    }
    public static Ival createIval(int i, int len) {
        return new Ival(i,len);
    }
    public static Cval createCval(char c) {
        return new Cval(c);
    }
    public static AEval createAEval(Stuff array, int sub) {
        return new AEval(array, sub);
    }
    public static Fvar createFvar(int cursor) {
        return new Fvar(cursor);
    }

    // tests...
    public static void main(String[] args) {
        System.out.println("running Stuff.main");
        Stuff vi = new Ival(7);
        Stuff vc = new Cval('Q');
        Stuff vs = new Sval("foo-bar");

//        System.out.println("  types");
//        System.out.println(vi.getType());
//        System.out.println(vc.getType());
//        System.out.println(vs.getType());

        System.out.println("  using getters, (81 is decimal Q)");
        System.out.println(String.valueOf(((Ival)vi).getInt()));
        System.out.println(String.valueOf(((Cval)vc).getInt()));
        System.out.println(vs.toString());

        System.out.println("  using toString");
        System.out.println(vi);
        System.out.println(vc);
        System.out.println(vs);

        System.out.println("  via clones");
        Stuff svi = (Stuff)vi.klone();
        System.out.println("vi clone: "+svi);
        Stuff svc = (Stuff)vc.klone();
        System.out.println("vc clone: "+svc);
        Stuff svs = (Stuff)vs.klone();
        System.out.println("vs clone: "+svs);
        
        System.out.println("   arrays, constructors w/ 2 args are arrays...");
        Stuff sa = new Ival(11,7);   // arg 1 is value set into sa's [0] cell.
        System.out.println("sa[7] is "+sa.toString());
        sa.setInt(3,5);    // subscript 3, value 5.
        int x = sa.getInt(3); System.out.println("x: "+x);
        Ival ia = (Ival)sa;
        int y = ia.getInt(3); System.out.println("y: "+y);
        for(int i=0; i<7; ++i)System.out.print(" "+ia.getInt(i));
        System.out.println("");
    }
    abstract public Stuff klone();
}

/* Originals are in the var Maps. Clones are put onto the stack.
	Type t, int len, boolean lvalue, boolean isArray   <<-- constructor parms
	   1       2           3                4
*/
class Sval extends Stuff {
    String val;
    Sval(String v) {
        super(TJ.Type.CHAR, v.length(), false, true);
        val=v;
    }
    public String toString() {
        return "Sval:"+val;
    }
    public String getStr() {
        return val;
    }
	public int getInt(int sub) {
		len = val.length();
		if(sub==len)return 0;    // simulating a C string
		else if(0<=sub && sub<len) return val.charAt(sub);
		else tj.eset(tj.RANGERR);
		return 0;
    }
    public Stuff klone() {
        return new Sval(val);
    }
}

class Cval extends Stuff {
    char val[];
// array
    Cval(char v, int len) {
        super(TJ.Type.CHAR,len,true,true);
        val = new char[len];
        val[0]=v;
    }
    public char[] getCharArray(){ return val; }
	public int getInt(int sub) {
		if(0<=sub && sub<=len) return (int)val[sub];
		tj.eset(tj.RANGERR); return 0;
    }
    public void setInt(int sub, int val) {
		if(0<=sub && sub<=len) this.val[sub] = (char)val; 
		else tj.eset(tj.RANGERR);
    }
// datum
    Cval(char v) {
        super(TJ.Type.CHAR, 1, true, false);
        val = new char[1];
        val[0] = v;
    }
    public int getInt() {
        return (int)val[0];
    }
    public void setInt(int v) {
        val[0] = (char)v;
    }
    public String toString() {
        return "Cval:"+String.valueOf(val);
    }
    public Stuff klone() {
        return new Cval(val[0]);
    }
}

class Ival extends Stuff {
    int val[];
    // array
    Ival(int v, int len) {
        super(TJ.Type.INT,len,true,true);
        val = new int[len];
        val[0] = v;  // might as will use v for something.
    }
    public int[] getIntArray(){ return val; }
    // Don't use on huge arrays...
    public void dumpArray(){
    	System.err.println("Stuff.arrayDump: ");
    	String s = "val: " + val[0];
    	if(isArray){
    		for(int i=1; i<len; ++i)s += (", "+val[i]);
    		System.err.print(s);
    	}
    	else System.err.println("Not an array, val: "+val[0]);
    }
	public int getInt(int sub) {   // Maybe not useful, getStuff() is better.
		if(0<=sub && sub<=len) return val[sub];
		tj.eset(tj.RANGERR); return 0;
    }
	public Stuff getStuff(int sub) {
		if(0<=sub && sub<=len) return new Ival(val[sub]);
		tj.eset(tj.RANGERR); return null;
    }
    public void setInt(int sub, int val) {
		if(0<=sub && sub<=len) this.val[sub] = val; 
		else tj.eset(tj.RANGERR);
    }
// datum
    Ival(int v) {
        super(TJ.Type.INT, 1, true, false);
    	len=1;
    	val = new int[1];
        val[0]=v;
    }
    public int getInt() {
        return val[0];
    }
    public void setInt(int val) {
        this.val[0] = val;
    }

    public String toString() {
    	if(isArray)return "int array["+len+"]" ;
        return "Ival:"+String.valueOf(val[0]);
    }
    public Stuff klone() {
        return new Ival(val[0]);
    }
}

// used only in var table, never pushed, no klone
class Fvar extends Stuff {
    int kursor;
    Fvar(int cursor) {
        super(TJ.Type.FCN,0,false,false);
        kursor=cursor;
    }
    String getStr() {
        return String.valueOf(kursor);
    }
    public Stuff klone() {
System.err.println("Stuff~232, shouldnt get here, cursor= "+tj.cursor);
tj.dl.dumpLine("");
Thread.dumpStack();
System.exit(99);
        return null;
    }
    public void setInt(int x) {
    	tj.eset(tj.TYPEERR);
    }
    public int getInt(){     // Only valid use of Fvar
    	return kursor;
    }
    public void setInt(int x, int sub) {
    	tj.eset(tj.TYPEERR);
    }
    public int getInt(int sub){
    	tj.eset(tj.TYPEERR);
    	return -999998;
    }
}
/*	Typeless array element. Value is element in array. Treats Sval as an array. 
 */
class AEval extends Stuff {
    int val[];   // This array exists in the parent I/Cval
    int subscript;
// datum 
    AEval(Stuff array, int subscript) {
        super(array.type, 1, true, false);    // type,len,lvalue,isArray
    	val = array.getIntArray();
    	this.subscript = subscript;
    }
    public int getInt() {			// from parent
        return val[subscript];
    }
    public void dumpArray(){
    	int len = val.length;
    	if(len>10)len=10;
    	System.err.println("Stuff.arrayDump: ");
    	String s = "val: " + val[0];
		for(int i=1; i<len; ++i) s += (", "+val[i]);
		System.err.print(s);
    }
    public void setInt(int v) {		// to parent
    	if(TJ.traceON)dumpArray();
        val[subscript] = v;
        if(TJ.traceON)dumpArray();
    }
    public String toString() {
    	String s = "AEval: ";
    	if(isArray){
    		s += "array["+val.length+"]: " ;
    		s += "val["+subscript+"]: "+val[subscript];
    	}
        else s += "datum: "+val[0];
        return s;
    }
// klone used for function arguments, read only. Hence clone is I/Cval.
    public Stuff klone() {
    	Stuff k;
    	if(type==TJ.Type.INT)k = createIval(val[subscript]);
    	else k = createCval((char)val[subscript]);
        return k;
    }
// COPY FROM Projects/Java/TryIt/Trace ...
    static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        t.printStackTrace(pw);
        pw.flush();
        sw.flush();
        return sw.toString();
    }
    private static void process(String s){
        s = munge(s);     // uncover to simplify the output
        System.err.println(s);
    }
    private static String munge(String s){
        s = s.substring(s.indexOf("at"));
        s = s.substring(0,s.indexOf("\n"));
        return s;
    }
    private static String trace(Throwable t){
        String s = getStackTrace(t);
        process(s);
        return s;
    }
    private static String trace(Throwable t, String msg, int i, int j) {
    	dl.dumpLine("Expr~455 trace dumpLine...");
        System.err.print(msg+": "+i+" "+j+" ");
        return trace(t);
    }
/* USAGE:
        trace(new Throwable());   //<<-- this is a trace mark
        trace(new Throwable(),"message");   //<<-- mark with message
//trace(new Throwable(),"konst",i,i);
*/
}
/*    inherited...
    public TJ.Type type;          // CHAR, INT, FCN, STR
    public int len;            // 1 for datum, else length of array
    public boolean lvalue;
    public boolean isArray;    // used to be 'class,' 0 for datum 1 for array
*/