package tg37.tinyc;
import java.lang.reflect.*;
import java.util.Scanner;
import java.io.StringWriter;
import java.io.PrintWriter;

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

/*	used by printf,ps,pl. Prints one token of format string;
 *	either a %<char> or a block of chars excluding %. Recursive
 *	until whole fmt string consumed.
 */
/*
void pFmt(char *fmt, int *args) {
	char pct[9], *nxtpct;
	int datum, fmtchar;
	if(!(*fmt))return;
//printf("\n~69 %s<<--\n",fmt);
	if(*fmt=='%'){
		datum = *(args++);
		int i=0;
		while(i<5){
			pct[i++]=*(fmt++);
			pct[i]=0;
			if(charIn(*fmt,"dscx")){
				pct[i++]=*(fmt++);
				pct[i]=0;
				break;
			}
			else if(!isdigit(*fmt))break;
		}
//printf("\n  ~82 %s<<--\n",pct);
		if(i>=5)printf("\nBAD FMT, max 3 digits, then one of dscx\n");
		else printf(pct,datum);
	}
	else if( (nxtpct=find(fmt,fmt+strlen(fmt),'%')) ) {
		pft(fmt,nxtpct-1);	/* block print 
		fmt = nxtpct;
	}
	else {
		ps(fmt);
		return;      /* one item done 
	}
	pFmt(fmt, args); /* do the rest 
	return;
}


/* new MC's with this implementation. A bit of modernization. 
int MprF(int nargs, int *args)
{
}
/*
printf("\n\n63: MprF: nargs %d args[0..3] %d %d %d %d",
			nargs,args[0],args[1],args[2],args[3]);
	pFmt((char*)*args,(args+1));  /* fmt, args 
	return 0;
}
*/

//MC 1
	int Mpc(int args[]){
		System.out.print((char)args[0]);
		return args[0];
    }
//MC 2   may need esc key handling, see C code.
    	static String _buff;
    	static int buff_nxt=0, buff_len=0;
    	static Scanner _input = null;
    int Mgch(int args[]){
    	if(_input==null)_input = new Scanner(System.in);
   		_buff = _input.next();
   		buff_len = _buff.length();
   		buff_nxt = 0; 
   		int x = (buff_nxt<=buff_len-1) ? _buff.charAt(buff_nxt++) : '\n'; 
    	return x;
    }
//MC 14
	int Mpn(int args[]){
		System.out.print(" "+args[0]);
		return 0;
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

//    int Mpc(int args[]){      <<== model format

	void invoke(String[] nameList, int mcno, Stuff[] args) {
		Stuff result = null;
		try{
			int nargs = args.length;
			int[] mcArg = new int[nargs];
			for(int i=0;i<nargs;++i) mcArg[i]=args[i].getInt();
			Class mcClass = this.getClass();
			Method mcMethod = 
				mcClass.getDeclaredMethod(nameList[mcno-1], int[].class);
			mcMethod.setAccessible(true);
			Integer ret = (Integer)mcMethod.invoke(this,mcArg);
			result = new Ival(ret.intValue());
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
        System.err.print(msg+": "+i+" "+j+" ");
        return trace(t);
    }
/* USAGE:
        trace(new Throwable());   //<<-- this is a trace mark
        trace(new Throwable(),"message");   //<<-- mark with message
*/
}
//System.out.println("MC~58 nargs "+nargs);
