// Messages.java
package checkers.classes;

import java.util.*;

public class Messages {
    private static ResourceBundle bundle;

    static {
        setLocale(Locale.getDefault());
    }

    public static void setLocale(Locale locale) {
        try {
            bundle = ResourceBundle.getBundle("checkers.messages", locale);
        } catch (MissingResourceException e) {
            bundle = ResourceBundle.getBundle("checkers.messages", Locale.ENGLISH);
        }
    }

    public static String get(String key) {
        return bundle.getString(key);
    }
}