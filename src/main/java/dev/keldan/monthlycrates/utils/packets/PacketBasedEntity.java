package dev.keldan.monthlycrates.utils.packets;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.*;

public class PacketBasedEntity {

    private final int entityId;
    private Location location;
    private ItemStack itemStack;

    private Object entityItem;
    private Object craftWorld;

    private static Class<?> craftPlayerClass;
    private static Method getHandleMethod;
    private static Field playerConnectionField;
    private static Method sendPacketMethod;

    private static Class<?> nmsEntityItemClass;
    private static Class<?> nmsWorldClass;
    private static Class<?> nmsItemStackClass;
    private static Class<?> nmsPacketPlayOutSpawnEntityClass;
    private static Class<?> nmsPacketPlayOutEntityMetadataClass;
    private static Class<?> nmsDataWatcherClass;
    private static Class<?> nmsPacketPlayOutEntityDestroyClass;
    private static Class<?> nmsPacketPlayOutEntityTeleportClass;

    private static Constructor<?> entityItemConstructor;
    private static Constructor<?> packetPlayOutSpawnEntityConstructor;
    private static Constructor<?> packetPlayOutEntityMetadataConstructor;
    private static Constructor<?> packetPlayOutEntityDestroyConstructor;
    private static Constructor<?> packetPlayOutEntityTeleportConstructor;

    private static Method getDataWatcherMethod;

    static {
        try {
            String version = getVersion();

            craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
            getHandleMethod = craftPlayerClass.getMethod("getHandle");
            Class<?> entityPlayerClass = Class.forName("net.minecraft.server." + version + ".EntityPlayer");
            playerConnectionField = entityPlayerClass.getField("playerConnection");
            Class<?> playerConnectionClass = Class.forName("net.minecraft.server." + version + ".PlayerConnection");
            Class<?> packetClass = Class.forName("net.minecraft.server." + version + ".Packet");
            sendPacketMethod = playerConnectionClass.getMethod("sendPacket", packetClass);

            nmsWorldClass = Class.forName("net.minecraft.server." + version + ".World");
            nmsItemStackClass = Class.forName("net.minecraft.server." + version + ".ItemStack");
            nmsEntityItemClass = Class.forName("net.minecraft.server." + version + ".EntityItem");
            nmsPacketPlayOutSpawnEntityClass = Class.forName("net.minecraft.server." + version + ".PacketPlayOutSpawnEntity");
            nmsPacketPlayOutEntityMetadataClass = Class.forName("net.minecraft.server." + version + ".PacketPlayOutEntityMetadata");
            nmsDataWatcherClass = Class.forName("net.minecraft.server." + version + ".DataWatcher");
            nmsPacketPlayOutEntityDestroyClass = Class.forName("net.minecraft.server." + version + ".PacketPlayOutEntityDestroy");
            nmsPacketPlayOutEntityTeleportClass = Class.forName("net.minecraft.server." + version + ".PacketPlayOutEntityTeleport");

            entityItemConstructor = nmsEntityItemClass.getConstructor(nmsWorldClass, double.class, double.class, double.class, nmsItemStackClass);
            packetPlayOutSpawnEntityConstructor = nmsPacketPlayOutSpawnEntityClass.getConstructor(nmsEntityItemClass);
            packetPlayOutEntityMetadataConstructor = nmsPacketPlayOutEntityMetadataClass.getConstructor(int.class, nmsDataWatcherClass, boolean.class);
            packetPlayOutEntityDestroyConstructor = nmsPacketPlayOutEntityDestroyClass.getConstructor(int[].class);
            packetPlayOutEntityTeleportConstructor = nmsPacketPlayOutEntityTeleportClass.getConstructor(nmsEntityItemClass);

            getDataWatcherMethod = nmsEntityItemClass.getMethod("getDataWatcher");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize NMS reflection for PacketBasedItem.");
        }
    }

    public PacketBasedEntity(Location location, ItemStack itemStack) throws Exception {
        this.location = location;
        this.itemStack = itemStack;
        this.entityId = EntityIdGenerator.getNextEntityId();

        this.craftWorld = getHandle(location.getWorld());

        Object nmsItemStack = asNMSCopy(itemStack);

        this.entityItem = entityItemConstructor.newInstance(craftWorld, location.getX(), location.getY(), location.getZ(), nmsItemStack);

        setNoPickup();
        setNoGravity();
        setInvisible();
    }

    private static String getVersion() {
        String packageName = PacketBasedEntity.class.getPackage().getName();
        return packageName.substring(packageName.lastIndexOf('.') - 4).replace(".", "");
    }

    private Object getHandle(Object bukkitWorld) throws Exception {
        Class<?> craftWorldClass = Class.forName("org.bukkit.craftbukkit." + getVersion() + ".CraftWorld");
        Method getHandle = craftWorldClass.getMethod("getHandle");
        return getHandle.invoke(craftWorldClass.cast(bukkitWorld));
    }

    private Object asNMSCopy(ItemStack itemStack) throws Exception {
        Class<?> craftItemStackClass = Class.forName("org.bukkit.craftbukkit." + getVersion() + ".inventory.CraftItemStack");
        Method asNMSCopy = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class);
        return asNMSCopy.invoke(null, itemStack);
    }

    private void setNoPickup() {
        try {
            Field pickupDelay = nmsEntityItemClass.getDeclaredField("pickupDelay");
            pickupDelay.setAccessible(true);
            pickupDelay.setInt(entityItem, Integer.MAX_VALUE);
        } catch (Exception ignored) {
        }
    }

    private void setNoGravity() {
        try {
            Method setNoGravity = nmsEntityItemClass.getMethod("setNoGravity", boolean.class);
            setNoGravity.invoke(entityItem, true);
        } catch (NoSuchMethodException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setInvisible() {
        try {
            Object dataWatcher = getDataWatcherMethod.invoke(entityItem);
            Method watchMethod = nmsDataWatcherClass.getMethod("watch", int.class, Object.class);
            Byte invisibleFlag = (byte) 0x20;
            watchMethod.invoke(dataWatcher, 0, invisibleFlag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void sendPacket(Player player, Object packet) throws Exception {
        Object craftPlayer = craftPlayerClass.cast(player);
        Object entityPlayer = getHandleMethod.invoke(craftPlayer);
        Object playerConnection = playerConnectionField.get(entityPlayer);
        sendPacketMethod.invoke(playerConnection, packet);
    }

    public void spawn(Player player) {
        try {
            Object spawnPacket = packetPlayOutSpawnEntityConstructor.newInstance(entityItem);
            sendPacket(player, spawnPacket);

            Object dataWatcher = getDataWatcherMethod.invoke(entityItem);
            Object metaPacket = packetPlayOutEntityMetadataConstructor.newInstance(entityId, dataWatcher, true);
            sendPacket(player, metaPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void teleport(Player player, Location newLocation) {
        this.location = newLocation;
        try {
            Object nmsItemStack = asNMSCopy(itemStack);
            Object newEntityItem = entityItemConstructor.newInstance(craftWorld, newLocation.getX(), newLocation.getY(), newLocation.getZ(), nmsItemStack);
            Object teleportPacket = packetPlayOutEntityTeleportConstructor.newInstance(newEntityItem);
            sendPacket(player, teleportPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroy(Player player) {
        try {
            int[] entities = new int[]{entityId};
            Object destroyPacket = packetPlayOutEntityDestroyConstructor.newInstance((Object) entities);
            sendPacket(player, destroyPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
