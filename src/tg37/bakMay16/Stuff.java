/**	Stuff is a type free datum with description and value.
	It is used on the Stack, and the Vartab. Derivatives
	hold values of type: int, char, string.
NEED: support for vars, and lvalue. Below constructors do just constants.
	Basically Stuff defines what goes on the Stack.
*/
package tg37.tinyc;

abstract public class Stuff extends TJ {
    enum Type {CHAR, INT, FCN, STR }

    public Type type;          // CHAR, INT, FUNCTION
    public int len;            // 1 for datum, else length of array
    public boolean lvalue;
    public boolean isArray;    // used to be 'class,' 0 for datum 1 for array

    Stuff(Type t, int l, boolean lv, boolean ia ) {
        type = t;
        len = l;
        lvalue = lv;
        isArray = ia;
    }
    abstract public int getInt();
    public char getType() {   // 'F', 'I' ,'C', or 'S'
        String t = this.getClass().toString();
        return t.charAt( t.length()-4 );
    }
    public boolean isNum() {
        char t = getType();
        return t=='I' || t=='C' ;
    }
    public boolean isInt() {
        char t = getType();
        return t=='I';
    }
    public boolean isChar() {
        char t = getType();
        return t=='C';
    }
    public boolean isStr() {
        char t = getType();
        return t=='S';
    }
    public boolean isFcn() {
        char t = getType();
//System.err.println("Stuff~42, isFcn: " + t + (t=='P') );
        return t=='P';
    }
    static Ival createIval(int i) {
        return new Ival(i);
    }
    static Cval createCval(char c) {
        return new Cval(c);
    }
    static Fvar createFvar(int cursor) {
        return new Fvar(cursor);
    }

    // tests...
    public static void main(String[] args) {
        System.out.println("running Stuff.main");
        Stuff vi = new Ival(7);
        Stuff vc = new Cval('Q');
        Stuff vs = new Sval("foo-bar");

        System.out.println("  types");
        System.out.println(vi.getType());
        System.out.println(vc.getType());
        System.out.println(vs.getType());

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
        System.out.println(svi.getType()+": "+svi);
        //System.out.println(svi.getInt());
        Stuff svc = (Stuff)vc.klone();
        System.out.println(svc.getType()+": "+svc);
        //System.out.println(svc.getInt());
        Stuff svs = (Stuff)vs.klone();
        System.out.println(svc.getType()+": "+svs);
        //System.out.println(svc.toString());

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
        super(Type.CHAR,v.length(),false,false);
        val=v;
    }
    public String toString() {
        return val;
    }
    public String getStr() {
        return val;
    }
    public Stuff klone() {
        return new Sval(val);
    }
    public int getInt() {
        eset(TYPEERR);
        return -999999;
    }
}
class Cval extends Stuff {
    char val;
    Cval(char v, int len) {
        super(Type.CHAR,len,true,false);
        val=v;
    }
    Cval(int v) {
        super(Type.CHAR, 1, true, false);
        val=(char)v;
    }
    public String toString() {
        return String.valueOf(val);
    }
    public Stuff klone() {
        return new Cval(val);
    }
    public int getInt() {
        return (int)val;
    }
}
class Ival extends Stuff {
    int val;
    Ival(int v, int len) {
        super(Type.INT,len,true,false);
        val=v;
    }
    Ival(int v) {
        super(Type.INT, 1, true, false);
        val=v;
    }
    public String toString() {
        return String.valueOf(val);
    }
    public Stuff klone() {
        return new Ival(val);
    }
    public int getInt() {
        return val;
    }
}
/* used only by Expr.expr() for expression: ptr +/- int
 */
//	Probably same as Ival with isArray true. Keep it for now.
class Pval extends Stuff {
    int kursor;
    Pval(int v) {
        super(Type.INT,1,true,true);
        kursor=v;
        isArray=true;
    }
    public String toString() {
        return String.valueOf(kursor);
    }
    public Stuff klone() {
        return new Pval(kursor);
    }
    public int getInt() {
        return kursor;
    }
}
// used only in var table, never pushed, no klone
class Fvar extends Stuff {
    int kursor;
    Fvar(int cursor) {
        super(Type.FCN,0,false,false);
        kursor=cursor;
    }
    String getStr() {
        return String.valueOf(kursor);
    }
    public Stuff klone() {
        return null;
    }
    public int getInt() {
        return kursor;
    }
}


