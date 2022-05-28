/* TC main loads and runs an app.
 */
package tg37.tinyc;
import java.io.IOException;
import java.nio.file.*;

public class TC {
    String startSeed = "[main();]";
    TJ tj;

    void at(int line){
    	System.err.println("at loadCode: TC~" + line);
//    	System.err.print(" cursor=" + cursor);
    	System.err.println(" -->"+tj.prog.substring(tj.cursor,tj.cursor+9)+"<--");
//    	System.err.println("prog  " + prog);
    }
//	load seed, pps/library.tc, "endlibrary", args(0) app.
    public void loadCode(String[] args) throws IOException {
//at(18);
		byte[] b;
        String ppsPath = "./pps/library.tc";
        String startSeed = new String("[main();]");

        Path libpath = Paths.get(ppsPath);
        b = Files.readAllBytes(libpath);
        String libs = new String(b);

//at(27);
        if(args.length==0) {
            System.err.println("Path to file needed");
            System.exit(1);
        }
//at(32);
        Path appPath = Paths.get(args[0]);
        b = Files.readAllBytes(appPath);
        String app = new String(b);

//at(37);
        StringBuilder sb = new StringBuilder(startSeed).append(libs)
        .append("endlibrary\n").append(app);
        tj.endapp = sb.length();
        sb = sb.append("\n   \n   \n");  // a bit of padding
        tj.lpr = startSeed.length();
        tj.apr = tj.lpr + libs.length() + 11;
        tj.prog = new String(sb);
//System.err.println("TC~45 prog: "+prog);
        tj.EPR = tj.prog.length();
        tj.cursor = 0;
//System.err.println("  EPR: "+EPR);
//System.err.println(prog);
//at(47);
//System.err.println("TC~39 lpr,apr,endapp,EPR: "+lpr+" "+apr+" "+endapp+" "+EPR);
    }
    public void initAndGo(String[] args){
    	tj = new TJ();
        tj.pt = new PT(tj);
        tj.dl = new Dialog();
        tj.exp = new Expr();
        tj.stmt = new ST();
        tj.stk = new Stack();
        tj.vt = new Vartab();

        tj.vt.tclink();
        tj.vt.dumpVarTab();
        tj.cursor = 0;   // seed
        try {
			loadCode(args);
            tj.stmt.st();   // <<<===  NEEDS prog
at(66);                  // <<<===  prog is loaded
        } catch(Exception e) {
            System.err.println(e);
        }
    }
    public static void main(String[] args) throws IOException {
    	TC tc;
        System.out.println("running TC.main");
// components, (declared in TJ)
        tc = new TC();
        tc.initAndGo(args);
    }
}