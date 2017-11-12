/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package triggerdoc;

import static java.lang.System.out;

/**
 *
 * @author veckardt
 */
public class Copyright {

    public static final String COPYRIGHT = "(c)";
    public static String copyright = "Copyright " + COPYRIGHT + " 2017 PTC Inc.";
    public static String copyrightHtml = "Copyright &copy; 2017 PTC Inc.";
    public static String programName = "Integrity Trigger Doc";
    public static String programVersion = "0.8";
    public static String author = "Author: Volker Eckardt";
    public static String email = "email: veckardt@ptc.com";

    public static String getCopyright() {
        String copy = ("* " + programName + " - Version " + programVersion);
        copy = copy + ("\n* An utility to support Trigger Documentation by Integrity Typs");
        copy = copy + ("\n* Tested with Integrity 10.9");
        copy = copy + ("\n*");
        copy = copy + ("\n* " + copyright);
        copy = copy + ("\n* " + author + ", " + email + "\n");
        return copy;
    }

    public static void write() {
        out.println(getCopyright());
    }

    public static void usage() {
        out.println("*");
        out.println("* Usage: ");
        out.println("*   <path-to-java>\\javaw -jar <path-to-jar>\\IntegrityTriggerDoc.jar");
        out.println("* Example:");
        out.println("*   jre\\bin\\java -jar IntegrityTriggerDoc.jar --hostname=<host> --port=<7001> --username=<mrx>");
        // out.println("* Additional Notes:");
        // out.println("*   - a configuration file 'IntegrityUndo.properties' can be used to specify default values");
        // out.println("*   - a log file is created in directory '%temp%', the filename is 'IntegrityUndo_YYYY-MM-DD.log'");
        out.println("*");
    }
}
