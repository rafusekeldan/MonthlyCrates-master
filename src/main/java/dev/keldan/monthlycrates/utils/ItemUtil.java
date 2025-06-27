package dev.keldan.monthlycrates.utils;

import de.tr7zw.nbtapi.NBT;
import dev.keldan.monthlycrates.utils.enums.CompatibleHand;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

public class ItemUtil {

    private static boolean OFF_HAND_SUPPORTED;
    private static Class<?> equipmentSlotClass;
    private static Object offHandEnum;
    private static Method getItemMethod;
    private static Method setItemMethod;

    static {
        try {
            equipmentSlotClass = Class.forName("org.bukkit.inventory.EquipmentSlot");
            offHandEnum = Enum.valueOf((Class<Enum>) equipmentSlotClass, "OFF_HAND");

            getItemMethod = Player.class.getMethod("getInventory")
                    .getClass().getMethod("getItem", equipmentSlotClass);
            setItemMethod = Player.class.getMethod("getInventory")
                    .getClass().getMethod("setItem", equipmentSlotClass, ItemStack.class);

            OFF_HAND_SUPPORTED = true;
        } catch (Exception e) {
            OFF_HAND_SUPPORTED = false;
        }
    }

    public static void takeActiveItem(Player player, CompatibleHand hand, int amount) {
        if (hand == CompatibleHand.MAIN_HAND || !OFF_HAND_SUPPORTED) {
            ItemStack item = player.getItemInHand();
            if (item == null) return;

            int result = item.getAmount() - amount;
            item.setAmount(result);

            player.setItemInHand(result > 0 ? item : null);
        } else {
            try {
                Object inventory = player.getInventory();
                ItemStack item = (ItemStack) getItemMethod.invoke(inventory, offHandEnum);
                if (item == null) return;

                int result = item.getAmount() - amount;
                item.setAmount(result);

                setItemMethod.invoke(inventory, offHandEnum, result > 0 ? item : null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isValid(ItemStack itemStack) {
        return itemStack != null && itemStack.getType() != Material.AIR;
    }

    public static boolean hasNBT(ItemStack itemStack, String tag) {
        if (!isValid(itemStack)) return false;

        return NBT.get(itemStack, nbt -> (boolean) nbt.hasTag(tag));
    }

    public static String getNBTString(ItemStack itemStack, String tag) {
        if (!isValid(itemStack)) return "";
        return NBT.get(itemStack, nbt -> (String) nbt.getString(tag));
    }
}