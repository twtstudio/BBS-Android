package com.twtstudio.bbs.bdpqchen.bbs.commons.utils;

/**
 * Created by bdpqchen on 17-6-7.
 */

public final class CastUtil {

    public static boolean cast2boolean(Object newValue) {
        return newValue instanceof Boolean && (boolean) newValue;
    }

    public static int cast2int(Object obj) {
        if (obj instanceof Integer) {
            return (int) obj;
        } else {
            return 0;
        }
    }

    public static int parse2int(String s) {
        return parse2int(s, 0);
    }

    public static int parse2int(String s, int def) {
        int casted = def;
        try {
            casted = Integer.parseInt(s);
        } catch (NumberFormatException ignored) {
        }
        return casted;
    }

    public static int parse2intWithMin(String s) {
        return parse2int(s, 0);
    }

    public static int parse2intWithMax(String s) {
        return parse2int(s, Integer.MAX_VALUE);
    }


    public static boolean isNumeric(String str) {
        final int length = str.length();
        if (length == 0) return false;
        for (int i = length; --i >= 0; ) {
            int chr = str.charAt(i);
            if (chr < 48 || chr > 57)
                return false;
        }
        return true;
    }


}
