/* TC main loads and runs an app.
 */
package tg37.tinyc;
import java.io.IOException;
import java.nio.file.*;

public class TC {
//    String startSeed = "[main();]";
    public static TJ tj;

    void at(int line){
    	System.err.println("at TC~" + line);
//    	System.err.print(" cursor=" + cursor);
//    	System.err.println(" -->"+tj.prog.substring(tj.cursor,tj.cursor+9)+"<--");
//    	System.err.println("prog  " + prog);
    }

//	load seed, pps/library.tc, "endlibrary", args(0) app.
    public void loadCode(String[] args) throws IOException {
		byte[] b;
        String ppsPath = "./pps/library.tc";
        String startSeed = new String("[main();]\n");

        Path libpath = Paths.get(ppsPath);
        b = Files.readAllBytes(libpath);
        String libs = new String(b);

        if(args.length==0) {
            System.err.println("Path to file needed");
            System.exit(1);
        }
        Path appPath = Paths.get(args[0]);
        b = Files.readAllBytes(appPath);
        String app = new String(b);

        StringBuilder sb = new StringBuilder(startSeed).append(libs)
        	.append("endlibrary\n").append(app);
        tj.endapp = sb.length();
        sb = sb.append("\n   \n   \n");  // a bit of padding
        tj.lpr = startSeed.length();
//System.err.println("TC~41 lpr: "+tj.lpr);
        tj.apr = tj.lpr + libs.length() + 11;
        tj.prog = new String(sb);
        tj.EPR = tj.prog.length();
        tj.cursor = 0;
    }
    public void initAndGo(String[] args){
        try {
			tj = TJ.getInstance();    // defines TC.tj, must be done first
			loadCode(args);   // must be done before instantiating the modules
			Vartab vt = Vartab.getInstance();
			Dialog.getInstance();
			Expr.getInstance();
			ST stmt = ST.getInstance();
			Stack.getInstance();
			tj.cursor = 0;   // seed
			vt.tclink();
/*	TRACE USAGE: TJ.symCount bumped unconditionally at Expr~313. LEAVE IT.
 *	Turn on symOn ~65 below, SYM msgs w/ count appear.
 *	Lower TraceOnLevel to turn on tracing dynamically, 
 *	See PT89 for what else is turned on. Adjust if needed.
 *		RECOMPILE needed after changing settings.    <<======
 */
//    	TJ.traceOnLevel = 80; // UNCOVER THIS and set level to turn on.
		TJ.traceON = false;
		TJ.symON = false;
		TJ.pushpopON = false;
		TJ.dynON = true;     // check PT~90 code dynamic on details.
			if(TJ.traceON)System.err.println("traceON is true, TC~68");
            stmt.st();
        } catch(Exception e) {
        	e.printStackTrace();
        	System.err.println("TC~72, Exception: "+e);
        }
    }
    public static void main(String[] args) {
    	TC tc;
        System.out.println("running TC.main");
        tc = new TC();
       	tc.initAndGo(args);
System.out.println("\nTC~81, natural exit");
    }
}