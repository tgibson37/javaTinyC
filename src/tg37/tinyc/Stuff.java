/**	Stuff is a type free datum with description and value.
	It is used on the Stack, and the Vartab. Derivatives 
	hold values of type: int, char, string.
NEED: support for vars, and lvalue. Below constructors do just constants.
	Basically Stuff defines what goes on the Stack.
*/
package tg37.tinyc;

class Sval extends Stuff {
	String val;
	Sval(String v){ super(Type.CHAR,v.length(),'A'); val=v; }
	public String getStr(){ return val; }
	public String toString(){ return val; }
	public int getInt(){ eset(TYPEERR); return 999999; }
}
class Cval extends Stuff {
	char val;
	Cval(char v){ super(Type.CHAR,1,'A'); val=v; }
	public String toString(){ return String.valueOf(val); }
	int getInt(){return val;}
	String getStr(){ eset(TYPEERR); return null; }
}
class Ival extends Stuff {
	int val;
	Ival(int v){ super(Type.INT,1,'A'); val=v; }
	public String toString(){ return String.valueOf(val); }
	int getInt(){return val;}
	String getStr(){ eset(TYPEERR); return null; }
}
// used only by Expr.expr() for ptr +/- int
class Pval extends Stuff {
	int val;
	Pval(int v){ super(Type.INT,1,'A'); val=v; dtod = 1; }
	public String toString(){ return String.valueOf(val); }
	int getInt(){return val;}
	String getStr(){ eset(TYPEERR); return null; }
}

abstract public class Stuff extends TJ {
	enum Type {CHAR, INT, FCN }

	int dtod;        // 0 for datum, 1 for array 
	char lvalue;      // 'L' for lvalue, 'A' for actual
	Type type;       // CHAR, INT, FUNCTION
	int len;         // 1 for datum, else length of array
	Stuff(Type t, int l, char lv ){
		type = t; len = l; lvalue = lv;
	}
	abstract int getInt();
	abstract String getStr();

	char getType(){    // 'I' ,'C', or 'S'
		String t = this.getClass().toString();
		return t.charAt( t.length()-4 );
	}
	boolean isNum(){
		char t = getType();
		return t=='I' || t=='C' ;
	}
	boolean isInt(){
		char t = getType();
		return t=='I';
	}
	boolean isChar(){
		char t = getType();
		return t=='C';
	}
	boolean isStr(){
		char t = getType();
		return t=='S';
	}
// tests...
	public static void main(String[] args){
		Ival vi = new Ival(7);
		Cval vc = new Cval('Q');
		Sval vs = new Sval("foo-bar");
		
		System.out.println("  types");
		System.out.println(vi.getType());
		System.out.println(vc.getType());
		System.out.println(vs.getType());
		
		System.out.println("  using getters, (81 is decimal Q)");
		System.out.println(String.valueOf(vi.getInt()));
		System.out.println(String.valueOf(vc.getInt()));
		System.out.println(vs.getStr());
		
		System.out.println("  using toString");
		System.out.println(vi);
		System.out.println(vc);
		System.out.println(vs);

		System.out.println("  via Stuff");
		Stuff svi = vi;
		System.out.print(svi.getType()+": ");
		System.out.println(svi.getInt());
		Stuff svc = vc;
		System.out.print(svc.getType()+": ");
		System.out.println(svc.getInt());
		Stuff svs = vs;
		System.out.print(svc.getType()+": ");
		System.out.println(svc.getStr());
	}
}

