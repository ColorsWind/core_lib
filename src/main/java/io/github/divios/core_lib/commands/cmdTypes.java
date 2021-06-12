package io.github.divios.core_lib.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.function.Predicate;

public enum cmdTypes {
    PLAYERS (CommandSender -> CommandSender instanceof Player),
    CONSOLE(CommandSender -> CommandSender instanceof ConsoleCommandSender),
    BOTH(CommandSender -> true);


    private final Predicate<CommandSender> predicate;

    cmdTypes(Predicate<CommandSender> predicate) {
        this.predicate = predicate;
    }

    public boolean compare(CommandSender sender) { return predicate.test(sender); }

    public String getErrorMsg() {
        if (this.equals(cmdTypes.PLAYERS))
            return "This command can only be runned by players";
        else if (this.equals(cmdTypes.CONSOLE))
            return "This command can only be runned by console";

        return "Something very wrong happened";
    }


}
