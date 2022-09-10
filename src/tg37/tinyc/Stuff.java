/**	Stuff is a type free datum with description and value.
	It is used on the Stack, and the Vartab. Derivatives
	hold values of type: int, char, string.
NEED: support for vars, and lvalue. Below constructors do just constants.
NEED: lvalue service that returns an actual value Stuff. Expr~20.
	Basically Stuff defines what goes on the Stack.
*/
package tg37.tinyc;

abstract public class Stuff {
    public TJ.Type type;          // CHAR, INT, FCN, STR
    public int len;            // 1 for datum, else length of array
    public boolean lvalue;
    public boolean isArray;    // used to be 'class,' 0 for datum 1 for array
	static TJ tj;

	Stuff(TJ.Type t, int l, boolean lv, boolean ia ) {
		tj = TJ.getInstance();
		
		type = t;
        len = l;
        lvalue = lv;
        isArray = ia;
    }
    public int getInt(){return -999999;}
    public void setInt(int val){}
    public int getInt(int sub){return -999999;}  // return element of array
    public void setInt(int sub, int val){}  // set element of array
    public Stuff getStuff(int sub){return null;}     // return element wrapped as Stuff

    public void dump(String msg){ 
    	System.out.println(msg+this.toString());
    	System.out.println("  type,len,lvalue,isArray: "+type+" "+
    		len+" "+lvalue+" "+isArray );
    }
//    public char getType() {   // 'F', 'I' ,'C', or 'S'
//        String t = this.getClass().toString();
//        return t.charAt( t.length()-4 );
//    }
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
    public static Cval createCval(char c) {
        return new Cval(c);
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
        super(TJ.Type.CHAR, v.length(), false, false);
        val=v;
    }
    public String toString() {
        return "Sval:"+val;
    }
    public String getStr() {
        return val;
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
        return "Cval:"+String.valueOf(val);  //  <<===   ???
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
    	return -999999;
    }
}
