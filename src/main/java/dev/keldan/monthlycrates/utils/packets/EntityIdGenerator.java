package dev.keldan.monthlycrates.utils.packets;

import java.util.concurrent.atomic.AtomicInteger;

public class EntityIdGenerator {

    private static final AtomicInteger nextId = new AtomicInteger(1000);

    public static int getNextEntityId() {
        return nextId.getAndIncrement();
    }
}
