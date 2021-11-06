package io.github.divios.core_lib.utils;

public class Primitives {

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
