package io.github.divios.core_lib.misc;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class Msg {

    public static String PREFIX = "";
    public static String TELEPORT_CANCELLED = "&7teleport_cancelled";

    public static synchronized void setPREFIX(String prefix) {
        PREFIX = prefix;
    }

    public static synchronized void setTeleportCancelled(String text) { TELEPORT_CANCELLED = text; }

    public static singletonMsg singletonMsg(String s) {
        return new singletonMsg(s);
    }

    public static MsgList msgList(List<String> s) { return new MsgList(s); }

    public static class singletonMsg {

        private String rawStr;
        private final Map<String, String> placeholders = new HashMap<>();

        private singletonMsg(String rawStr) {
            this.rawStr = rawStr == null ? "":rawStr;
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
            this.rawStr = rawStr == null? Collections.emptyList():rawStr;
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

    public static void sendMsg(Player p, String s) {
        if (p == null) return;
        p.sendMessage(PREFIX + FormatUtils.color(s));
    }

    public static void sendMsg(UUID uuid, String s) {
        sendMsg(Bukkit.getPlayer(uuid), s);
    }

}
