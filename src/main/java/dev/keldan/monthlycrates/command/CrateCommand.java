package dev.keldan.monthlycrates.command;

import dev.keldan.monthlycrates.crate.CrateItemFactory;
import dev.keldan.monthlycrates.utils.commands.BaseCommand;
import dev.keldan.monthlycrates.utils.commands.CommandContext;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CrateCommand implements BaseCommand {

    private static String lastCrateId = "default";

    @Override
    public void execute(CommandContext ctx) {
        String subCommand = ctx.arg(0);
        if (subCommand == null) {
            ctx.getSender().sendMessage("§cUsage: /crate create <id> OR /crate give <player> [amount]");
            return;
        }

        if (subCommand.equalsIgnoreCase("create")) {
            String id = ctx.arg(1);
            if (id == null) {
                ctx.getSender().sendMessage("§cUsage: /crate create <id>");
                return;
            }

            lastCrateId = id;
            ctx.getSender().sendMessage("§aCrate created with ID: " + id);
        }
        else if (subCommand.equalsIgnoreCase("give")) {
            String playerName = ctx.arg(1);
            if (playerName == null) {
                ctx.getSender().sendMessage("§cUsage: /crate give <player> [amount]");
                return;
            }

            Player target = Bukkit.getPlayerExact(playerName);
            if (target == null) {
                ctx.getSender().sendMessage("§cPlayer not found: " + playerName);
                return;
            }

            int amount = 1;
            String amountStr = ctx.arg(2);
            if (amountStr != null) {
                try {
                    amount = Integer.parseInt(amountStr);
                } catch (NumberFormatException e) {
                    ctx.getSender().sendMessage("§cInvalid amount, using 1");
                }
            }

            ItemStack crateItem = CrateItemFactory.createCrate(lastCrateId);
            crateItem.setAmount(amount);

            target.getInventory().addItem(crateItem);
            ctx.getSender().sendMessage("§aGave " + amount + " crate(s) to " + target.getName());
            target.sendMessage("§aYou received " + amount + " crate(s)!");
        }
        else {
            ctx.getSender().sendMessage("§cUnknown subcommand. Use create or give.");
        }
    }

    @Override
    public boolean playerOnly() {
        return false;
    }

    @Override
    public String getPermission() {
        return "monthlycrates.admin";
    }
}