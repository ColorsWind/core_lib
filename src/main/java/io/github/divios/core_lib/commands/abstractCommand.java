package io.github.divios.core_lib.commands;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class abstractCommand {

    private cmdTypes type;

    public abstractCommand(cmdTypes type) {
        CommandManager.addCommand(this);
        this.type = type;
    }

    public abstract String getName();

    public abstract boolean validArgs(List<String> args);

    public abstract String getHelp();

    public abstract List<String> getPerms();
    
    public List<String> getSafePerms() { return getPerms() == null ? Collections.emptyList():getPerms(); }

    public abstract List<String> getTabCompletition(List<String> args);

    public cmdTypes getType() { return type; }

    public void setType(cmdTypes type) { this.type = type; }

    public abstract void run(CommandSender sender, List<String> args);

    public static abstractCommand empty() {
        return new abstractCommand(null) {
            @Override
            public String getName() {
                return "";
            }

            @Override
            public boolean validArgs(List<String> args) {
                return false;
            }

            @Override
            public String getHelp() {
                return "";
            }

            @Override
            public List<String> getPerms() {
                return new ArrayList<>();
            }

            @Override
            public List<String> getTabCompletition(List<String> args) {
                return new ArrayList<>();
            }

            @Override
            public void run(CommandSender sender, List<String> args) {

            }
        };
    }

}
