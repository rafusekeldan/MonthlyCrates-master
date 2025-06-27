package dev.keldan.monthlycrates.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CrateTabComplete implements TabCompleter {

    private static final List<String> SUBCOMMANDS = Collections.unmodifiableList(Arrays.asList("create", "give"));

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            // Suggest subcommands that start with what is typed
            List<String> completions = new ArrayList<>();
            String partial = args[0].toLowerCase();
            for (String sub : SUBCOMMANDS) {
                if (sub.startsWith(partial)) {
                    completions.add(sub);
                }
            }
            return completions;
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("give")) {
                // Suggest online player names for the player argument
                List<String> players = new ArrayList<>();
                String partialPlayer = args[1].toLowerCase();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().toLowerCase().startsWith(partialPlayer)) {
                        players.add(player.getName());
                    }
                }
                return players;
            }
            // Add tab completion for create <id> if you want later here
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("give")) {
                // Future: suggest crate types here
                return Collections.emptyList();
            }
        }
        return Collections.emptyList();
    }
}
