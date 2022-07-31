/* TC main loads and runs an app.
 */
package tg37.tinyc;
import java.io.IOException;
import java.nio.file.*;

public class TC {
    String startSeed = "[main();]";
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
            stmt.st();
        } catch(Exception e) {
e.printStackTrace();
System.err.println("TC~60, Exception: "+e);
        }
    }
    public static void main(String[] args) {
    	TC tc;
        System.out.println("running TC.main");
        tc = new TC();
       	tc.initAndGo(args);
System.out.println("\nTC~69, natural exit");
    }
}