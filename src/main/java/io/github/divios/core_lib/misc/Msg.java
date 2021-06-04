package io.github.divios.core_lib.misc;

import java.util.*;
import java.util.stream.Collectors;

public class Msg {


    public static singletonMsg singletonMsg(String s) {
        return new singletonMsg(s);
    }

    public static MsgList msgList(List<String> s) { return new MsgList(s); }

    public static class singletonMsg {

        private String rawStr;
        private final Map<String, String> placeholders = new HashMap<>();

        private singletonMsg(String rawStr) {
            this.rawStr = rawStr;
        }

        public singletonMsg add(String placeholder, String toReplace) {
            placeholders.put(placeholder, toReplace);
            return this;
        }

        public String build() {
            placeholders.forEach((s, s2) ->
                    rawStr = rawStr.replaceAll(s, s2));
            return rawStr;
        }

    }

    public static class MsgList {

        private final List<String> rawStr;
        private final Map<String, String> placeholders = new HashMap<>();

        private MsgList(List<String> rawStr) {
            this.rawStr = rawStr;
        }

        public MsgList add(String placeholder, String toReplace) {
            placeholders.put(placeholder, toReplace);
            return this;
        }

        public List<String> build() {

            return rawStr.stream().map(s -> {
                String aux = s;
                for (Map.Entry<String, String> entry : placeholders.entrySet())
                    aux = aux.replaceAll(entry.getKey(), entry.getValue());
                return aux;
            }).collect(Collectors.toList());
        }

    }

}
