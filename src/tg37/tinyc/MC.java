package tg37.tinyc;
import java.lang.reflect.*;

public class MC {
	
	static Stack stk = Stack.getInstance();
	static ST stmt = ST.getInstance();
	static Vartab vt = Vartab.getInstance();
	static Dialog dl = Dialog.getInstance();
	static TJ tj = TJ.getInstance();
	static MC mc = MC.getInstance();
	
    private static MC instance;
    private MC(){}
    public static synchronized MC getInstance(){
        if(instance == null){
            instance = new MC();
        }
        return instance;
    }

    int Mpc(int args[]){     // <<<===    temp test, should be args[]
		System.out.print((char)args[0]);   //   args[0]
		return args[0];
    }
/*	code the MC above and register in Names array. Placement in the array
 *	determines the MC number starting with 1, 101, 201.
 */
    
	/* first in this list is MC 1 */
	static final String[] origNames =
		{ "Mpc", "Mgch", null, null, null
		, null, "MmvBl", "Mcountch", "Mscann", null
		, null, "Mchrdy", "Mpft", "Mpn", null
	};

/* first in this list is MC 101 */
	static final String[] newNames =
        { "MprF", "Msleep", "Mfilrd", "Mstrlen", "Mstrcat"
        , "Mstrcpy", "Mfilwt", "Mexit", "Mexitq", "Mcdate"
        , "Mfopen", "Mfputs", "Mfputc", "Mfgets", "Mfclose"
        , "Mgetprop", "Msystem", "Mfpn", "Msqrt", "Marctan"
        , "naf", "naf", "naf", "naf", "naf"
};

	void invoke(String[] nameList, int mcno, Stuff[] args) {
		Stuff result = null;
		try{
			int nargs = args.length;
			int[] mcArg = new int[nargs];   //<<== temp test
			for(int i=0;i<nargs;++i) mcArg[i]=args[i].getInt();   //<<== temp test
			Class mcClass = this.getClass();
			Method mcMethod = 
				mcClass.getDeclaredMethod(nameList[mcno-1], int[].class);   //<<== temp test
			mcMethod.setAccessible(true);
			mcMethod.invoke(this,mcArg);
		} catch(Exception e){
			tj.eset(tj.MCERR);
		}
		stk.pushStuff(result);
	}

	static void origMC(int mcno, Stuff[] args) {
		if(mcno<1 || mcno>origNames.length) {
			stk.pushk(0); tj.eset(tj.ARGSERR);
		}
		else {
			mc.invoke(origNames,mcno,args);
		}
	}
/*
	static void newMC(int mcno, Stuff[] args) {
		if(mcno<1 || mcno> newNames.length) {
			stk.pushk(0); tj.eset(tj.ARGSERR);
		}
		else {
			invoke(newNames,mcno,args);
		}
	}

	void userMC(int mcno, Stuff[] args) {
		if(mcno<1 || mcno> userNames.length) {
			stk.pushk(0); tj.eset(tj.ARGSERR);
		}
		else {
			invoke(userNames,mcno,args);
		}
	}
*/

/*	
	int plugInMC(int mcno, int nargs, int *args) {
	//fprintf(stderr,"~355mc %d\n",piMC);
		if(piMC==NULL) eset(ARGSERR);
		else return (*piMC)(mcno, nargs, args);
		return 0;   // to avoid compile warning
	}
*/	
	/*	Usage: put MC 0 in your tc code. Then either:
	 *	bkpnt Mzero for debugging, or string arg to dumpVar, or both.
	void Mzero(){}
	void _mzero(int nargs, int *args){
		if(nargs){
			char* sym = (char*)args[0];
			char *label;
			if(nargs>1)label = (char*)args[1];
			else label = sym;
			fprintf(stderr,"\n%s:  ",label);
			struct var* v = addrval_all(sym);
			if(v)dumpVar(v);
			else fprintf(stderr,"%s, no such symbol\n",sym);
		}
		Mzero();
	}
*/

	public static void machinecall( int nargs ) {
		Stuff[] args = new Stuff[nargs-1];
		int mcno = stk.toptoi();
		--nargs;
		for(int i=0; i<nargs; ++i){
			Stuff x=stk.popst();
			args[nargs-1-i]=x;
		}
		if(mcno==0){  // MC 0 for debugging, DO LATER
/*			pushk(0);
			_mzero(nargs, args);
			return;
*/
		}
		if(mcno<100)origMC(mcno, args);
/*		else if(mcno<200) newMC(mcno-100, args);
		else if(mcno<300) userMC(mcno-200, args);
		else {
			int rval;
			rval = plugInMC(mcno-1000, nargs, args);
			pushk(rval);
		}
*/
		if(tj.error==tj.KILL)return;
		if(tj.error==tj.EXIT)return;
		if(tj.error!=0)System.out.print("\nMC %d not defined"+mcno);
	}
}
//System.out.println("MC~58 nargs "+nargs);
