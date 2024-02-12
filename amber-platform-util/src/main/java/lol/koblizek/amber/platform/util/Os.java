package lol.koblizek.amber.platform.util;

import java.util.Locale;

public enum Os {
    WINDOWS,
    LINUX,
    MAC,
    SOLARIS,
    UNKNOWN;

    public static Os getOS() {
        String os = System.getProperty("os.name").toLowerCase(Locale.getDefault());
        if (os.contains("win")) {
            return WINDOWS;
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            return LINUX;
        } else if (os.contains("mac")) {
            return MAC;
        } else if (os.contains("sunos")) {
            return SOLARIS;
        } else {
            return UNKNOWN;
        }
    }
}
