package dev.keldan.monthlycrates.crate;

import dev.keldan.monthlycrates.MonthlyCrates;
import dev.keldan.monthlycrates.utils.ItemUtil;
import dev.keldan.monthlycrates.utils.enums.CompatibleHand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EventListener;

public class CrateListener implements EventListener {

    private final MonthlyCrates plugin;

    public CrateListener(MonthlyCrates plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlace(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (!ItemUtil.isValid(item)) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR &&
                event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        if (!ItemUtil.hasNBT(item, "MonthlyCrate")) return;

        // TODO: Check nbt string to make sure crates not invalid then return

        ItemUtil.takeActiveItem(player, CompatibleHand.getHand(event), 1);

        // TODO: Start Animation
        player.sendMessage("Opened Crate, Working good");
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (ItemUtil.isValid(item) && ItemUtil.hasNBT(item, "MonthlyCrate")) {
            event.setCancelled(true);
        }
    }

}
