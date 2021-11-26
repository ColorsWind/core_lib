package io.github.divios.core_lib.utils;

public class Primitives {

    public static boolean isInteger(String s) {
        return testCast(() -> Integer.parseInt(s));
    }

    public static int getAsInteger(String s) {
        if (!isInteger(s)) throw new RuntimeException("String passed is not an integer");
        return Integer.parseInt(s);
    }

    public static boolean isBoolean(String s) {
        return testCast(() -> Boolean.getBoolean(s));
    }

    public static boolean getAsBoolean(String s) {
        if (isBoolean(s)) throw new RuntimeException("String passed is not a boolean");
        return Boolean.getBoolean(s);
    }

    private static boolean testCast(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
