/* TC main loads and runs an app */

package tg37.tinyc;
import java.io.IOException;
import java.nio.file.*;

//ISSUE: OLD tc files probably have null end of strings.
public class TC extends TJ {
    String startSeed = "[main();]";

    /*	loads seed, pps/library.tc, "endlibrary", args(0) app.
     */
    private static void loadCode(String[] args) throws IOException {
        byte[] b;
        String ppsPath = "./pps/library.tc";
        String startSeed = new String("[main();]");

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
        endapp = sb.length();
        sb = sb.append("\n   \n   \n");  // a bit of padding
        lpr = startSeed.length();
        apr = lpr + libs.length() + 11;
        pr = new String(sb);
        EPR = pr.length();
        cursor = 0;
//System.err.println("TC~39 lpr,apr,endapp,EPR: "+lpr+" "+apr+" "+endapp+" "+EPR);
    }

    public static void main(String[] args) throws IOException {
        System.out.println("running TC.main");
        try {
            loadCode(args);
        } catch(Exception e) {
            System.err.println(e);
        }
        Vartab vt = Vartab.getInstance();
        ST stmt = ST.getInstance();
System.err.println("TC~45, error: "+error);
        vt.tclink();
        vt.dumpVarTab();
        cursor = 0;   // seed
System.err.println("TC~49, error: "+error+", calling ST.st...");
        stmt.st();
    }
}