package dev.keldan.monthlycrates.utils.commands;

import dev.keldan.monthlycrates.command.CrateCommand;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class CommandFramework implements CommandExecutor, TabCompleter {

    private final Map<String, BaseCommand> subcommands = new HashMap<>();
    private JavaPlugin plugin;

    public CommandFramework(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerCommand(String commandName, CrateCommand crateCommand) {
        PluginCommand cmd = plugin.getCommand(commandName);
        if (cmd == null) {
            plugin.getLogger().warning("Command '" + commandName + "' is not defined in plugin.yml");
            return;
        }

        cmd.setExecutor(this);
        cmd.setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /" + label + " <subcommand>");
            return true;
        }

        String subName = args[0].toLowerCase();
        BaseCommand sub = subcommands.get(subName);

        if (sub == null) {
            sender.sendMessage("§cUnknown subcommand.");
            return true;
        }

        if (sub.playerOnly() && !(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can run this.");
            return true;
        }

        if (sub.getPermission() != null && !sender.hasPermission(sub.getPermission())) {
            sender.sendMessage("§cYou don't have permission.");
            return true;
        }

        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        sub.execute(new CommandContext(sender, subArgs));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(subcommands.keySet());
        }

        return Collections.emptyList();
    }
}
