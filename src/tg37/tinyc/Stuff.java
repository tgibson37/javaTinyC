/**	Stuff is a type free datum with description and value.
	It is used on the Stack, and the Vartab. Derivatives 
	hold values of type: int, char, string.
*/
package tg37.tinyc;

class Sval extends Stuff {
	String val;    // index into program space. Use pr(val) instead of *val
	Sval(String v){ super(CHAR,v.length(),FALSE); val=v; }
	String getStr(){return val;}
}
class Cval extends Stuff {
	char val;
	Cval(char v){ super(CHAR,1,FALSE); val=v; }
}
class Ival extends Stuff {
	int val;
	Ival(int v){ super(INT,1,FALSE); val=v; }
}

public class Stuff {
	// type...
    public static final int CHAR = 0;
    public static final int INT = 1;
    public static final int FCN = 'E';
	// dtod (distance to datum)...  [class in tc.c]
    public static final int DATUM = 0;
    public static final int PTR = 1;
    // boolean
    public static final int TRUE = 1;
    public static final int FALSE = 0;

	int dtod;   // 0 for datum, 1 for array 
	int lvalue;     // TRUE for lvalue, else FALSE
	int type;       // CHAR, INT, FUNCTION
	int len;        // 1 for datum, else length of array
	Stuff(int t, int l, int lv ){
		type = t; len = l; lvalue = lv;
	}
	int getInt(){
		int v=99999;  
		switch(type) {
		case CHAR: v = ((Cval)this).val; break;
		case INT:  v = ((Ival)this).val; break;
		}
/*		if(dtod==0){
			if(type==0){v = ((Cval)this).val;}
			else if(type==1)v = ((Ival)this).val;
		}
*/
		return v;
	}
// tests...
	public static void main(String[] args){
		Ival vi = new Ival(7);
		Cval vc = new Cval('Q');
		Sval vs = new Sval("foo-bar");
		System.out.println(String.valueOf(vi.getInt()));
		System.out.println(String.valueOf(vc.getInt()));
		System.out.println(vs.getStr());
	}
}

