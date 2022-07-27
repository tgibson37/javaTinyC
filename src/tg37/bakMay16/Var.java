/*	Variables and var tables: libs, globals, list of locals.
 *	Search order is active local, globals, libs.
 */
package tg37.tinyc;
import java.util.*;

public class Var extends PT {
//a variable
    ST stmt = ST.getInstance();
    String name;
    boolean isArray;
    Stuff.Type type;
    int len;
    Stuff value;
    Vartab vt = Vartab.getInstance();

    public String toString() {
        return name+": "+value;
    }

    /*********** var tools ****************/
    /* copy the argument value into the new local place */
    /*	int copyArgValue(struct var *v, int class, Type type, union stuff *passed ) {
    			if(passed && class){                                    /* passed pointer
    					(*v).value.up = (*passed).up;
    			} else if( passed && !class ) {                 /* passed datum
    					switch(type){
    					case Int:
    							put_int( (*v).value.up, (*passed).ui );
    							break;
    					case Char:
    							put_char( (*v).value.up, (*passed).uc );
    							break;
    					default:
    							eset(TYPEERR);
    							return TYPEERR;
    					}
    			}
    			return 0;
    	}
    */

    /* SITUATION: Declaration is parsed, and its descriptive data known. Create
    	a Stuff and add it to curfun. Formerly newvar. */
    public Var( boolean isArray, Stuff.Type type, int len, Stuff passed ) {
        this.isArray = isArray;
        this.type = type;
        this.len  = len;
        this.name = pr.substring(fname,lname);
        if(passed!=null)this.value = passed.klone();   // function arg
        else {    // declaration incl function parameter
            if(type==Stuff.Type.INT) {
                this.value = Stuff.createIval(0);
            }
            else if(type==Stuff.Type.CHAR) {
                this.value = Stuff.createCval((char)0);
            }
            else if(type==Stuff.Type.FCN) {
                this.value = Stuff.createFvar(cursor);
            }
        }
        vt.curfun.put(name, this);
    }


// tests...
    public static void main(String[] args) {
        System.out.println("running Var.main");
        Vartab vt = Vartab.getInstance();
        Stack stk = Stack.getInstance();
        pr="  L1   L2   G3   G4   a1   a2   b1   b2   ..   ..   ..   ..   ..   ..   ..   ";
//  01234567890123456789012345678901234567890123456789012345678901234567890123456789
//            1         2         3         4         5         6         7
        vt.curfun = vt.libs;    // should get L1,L2
        fname=2;
        lname=4;                        // L1
        new Var(true, Stuff.Type.INT, 1, null);
        for(int i=1; i<8; ++i ) {                // L2...b2
            if(i==2) vt.curfun = vt.globals;			//  G3...G2
            else if(i%2==0) {          // a1,a2 Then b1,b2 in second local frame
                vt.newfun();
//				vt.curfun=stk.peekTop();      // peekTop retrieves empty 2nd level
            }
            fname += 5;
            lname += 5;    // a1...b2
            new Var(true, Stuff.Type.INT, 1, null);
        }
        vt.dumpVarTab();
        Var v;
        Stuff s;
        fname=2;
        lname=4;
        v=vt.addrval();
        s= v.value;    // L1
        System.out.println(s+" should be 0");

        fname=17;
        lname=19;
        v=vt.addrval();
        s= v.value;   // G4
        System.out.println(s+" should be 0");

        fname=32;
        lname=34;
        v=vt.addrval();
        s= v.value;   // b1
        System.out.println(s+" should be 0");
    }
}
