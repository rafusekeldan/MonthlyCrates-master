package dev.keldan.monthlycrates.utils.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandContext {
    private final CommandSender sender;
    private final String[] args;

    public CommandContext(CommandSender sender, String[] args) {
        this.sender = sender;
        this.args = args;
    }

    public CommandSender getSender() {
        return sender;
    }

    public String[] getArgs() {
        return args;
    }

    public boolean isPlayer() {
        return sender instanceof Player;
    }

    public Player getPlayer() {
        return (Player) sender;
    }

    public String arg(int index) {
        return index < args.length ? args[index] : null;
    }
}
