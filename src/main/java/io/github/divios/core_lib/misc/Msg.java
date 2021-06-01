package io.github.divios.core_lib.misc;

import java.util.HashMap;
import java.util.Map;

public class Msg {

    private String rawStr;
    private final Map<String, String> placeholders = new HashMap<>();

    private Msg(String rawStr) {
        this.rawStr = rawStr;
    }

    public static Msg create(String s) {
        return new Msg(s);
    }

    public Msg add(String placeholder, String toReplace) {
        placeholders.put(placeholder, FormatUtils.color(toReplace));
        return this;
    }

    public String build() {
        placeholders.forEach((s, s2) ->
                rawStr = rawStr.replaceAll("%" + s + "%", s2));
        return rawStr;
    }

}
