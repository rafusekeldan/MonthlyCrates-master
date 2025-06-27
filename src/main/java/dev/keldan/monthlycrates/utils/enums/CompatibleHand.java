package dev.keldan.monthlycrates.utils.enums;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public enum CompatibleHand {
    MAIN_HAND, OFF_HAND;

    private static final Map<Class<?>, Method> methodCache = new ConcurrentHashMap<>();
    private static final boolean OFF_HAND_SUPPORTED = isOffHandSupported();
    private static Class<?> equipmentSlotClass;

    private static boolean isOffHandSupported() {
        try {
            equipmentSlotClass = Class.forName("org.bukkit.inventory.EquipmentSlot");
            equipmentSlotClass.getDeclaredField("OFF_HAND");
            return true;
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            return false;
        }
    }

    public static CompatibleHand getHand(Object event) {
        if (!OFF_HAND_SUPPORTED) {
            return MAIN_HAND;
        }

        try {
            Class<?> clazz = event.getClass();
            Method method = methodCache.computeIfAbsent(clazz, key -> {
                try {
                    Method m = key.getDeclaredMethod("getHand");
                    m.setAccessible(true);
                    return m;
                } catch (NoSuchMethodException e) {
                    return null;
                }
            });

            if (method != null) {
                Object result = method.invoke(event);
                if (equipmentSlotClass.isInstance(result)) {
                    String slotName = result.toString();
                    return slotName.equals("OFF_HAND") ? OFF_HAND : MAIN_HAND;
                }
            }

        } catch (IllegalAccessException | InvocationTargetException ignored) {
        }

        return MAIN_HAND;
    }
}
