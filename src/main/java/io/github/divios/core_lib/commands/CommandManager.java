package io.github.divios.core_lib.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is responsible for managing all the commands
 * registered, both onCommand and tabComplete. It needs a
 * plugin to be registered
 */

public class CommandManager implements TabCompleter, CommandExecutor {

    private static CommandManager instance = null;
    private static final Set<abstractCommand> cmds = new HashSet<>();

    private CommandManager() {}

    public static void register(PluginCommand cmdPlugin) {
        if (instance != null) return;

        cmdPlugin.setTabCompleter((instance = new CommandManager()));
        cmdPlugin.setExecutor(instance);
    }

    public static void addCommand(abstractCommand cmd) { cmds.add(cmd); }

    public static void addCommand(abstractCommand... _cmds) { cmds.addAll(Arrays.asList(_cmds)); }

    public static void removeCommand(abstractCommand cmd) { cmds.remove(cmd); }

    public static Set<abstractCommand> getCmds() { return Collections.unmodifiableSet(cmds); }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] strings) {

        if (strings.length == 0)
            return false;

        cmds.stream()
                .filter(absC -> absC.getName().equals(strings[0]))
                .findFirst()
                .ifPresent(absC -> {

                    if (!absC.validArgs(Arrays.stream(strings).skip(1).collect(Collectors.toList()))) {     //Check valid args
                        commandSender.sendMessage(absC.getHelp());
                        return;
                    }

                    if (!absC.getType().compare(commandSender)) {       // Check if valid commandSender
                        commandSender.sendMessage(absC.getType().getErrorMsg());
                        return;
                    }

                    for (String perm : absC.getPerms())     // Check Perms
                        if (!commandSender.hasPermission(perm)) {
                            commandSender.sendMessage("Not perms");
                            return;
                        }

                    absC.run(commandSender, Arrays.stream(strings).skip(1).collect(Collectors.toList()));

                });

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender,
                                      @NotNull Command command,
                                      @NotNull String label,
                                      @NotNull String[] strings) {

        Optional<List<String>> toReturn;

        if (strings.length == 1) {      // Si son los primeros parametros
            toReturn = Optional.of(cmds.stream()
                    .map(abstractCommand::getName)
                    .collect(Collectors.toList()));
        }

        else {
            toReturn = Optional.ofNullable(
                    cmds.stream()
                        .filter(absC -> absC.getName().equalsIgnoreCase(strings[0]))
                        .findFirst()
                        .orElseGet(abstractCommand::empty)
                        .getTabCompletition(Arrays.stream(strings)
                                .skip(1).collect(Collectors.toList())));
        }

        return toReturn
                .filter(steam -> steam.stream().findAny().isPresent())
                .orElseGet(Collections::emptyList).stream()
                .filter(s -> s.toLowerCase(Locale.ROOT)
                    .startsWith(strings[strings.length - 1].toLowerCase()))
                .collect(Collectors.toList());
    }
}

