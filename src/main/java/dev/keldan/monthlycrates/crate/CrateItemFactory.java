package dev.keldan.monthlycrates.crate;

import de.tr7zw.nbtapi.NBTItem;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CrateItemFactory {

    @Deprecated
    public static ItemStack createCrate(String id) {
        ItemStack item = new ItemStack(Material.CHEST);

        String text = "MONTHLY CRATE";

        StringBuilder displayNameBuilder = new StringBuilder();
        boolean useD = true;
        for (char c : text.toCharArray()) {
            if (c == ' ') {
                displayNameBuilder.append(' ');
                continue;
            }
            if (useD) {
                displayNameBuilder.append(ChatColor.DARK_PURPLE).append(ChatColor.BOLD).append(c);
            } else {
                displayNameBuilder.append(ChatColor.LIGHT_PURPLE).append(ChatColor.BOLD).append(c);
            }
            useD = !useD;
        }
        String displayName = displayNameBuilder.toString();

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);

        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setString("MonthlyCrate", id);
        return nbtItem.getItem();
    }
}