package pse_gui.Utils;

/**
 * Created by r1p on 6/19/17.
 */
public class OSValidator {

    public enum OSType{
        ERROR,
        Windows,
        OSX,
        Unix,
        Solaris
    }
    private static String OS = System.getProperty("os.name").toLowerCase();

    public static boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    public static boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }

    public static boolean isUnix() {
        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
    }

    public static boolean isSolaris() {
        return (OS.indexOf("sunos") >= 0);
    }

    public static OSType getOS(){
        if (isWindows()) return OSType.Windows;
        else if (isMac()) return OSType.OSX;
        else if (isUnix()) return OSType.Unix;
        else if (isSolaris()) return OSType.Solaris;
        else return OSType.ERROR;
    }

}