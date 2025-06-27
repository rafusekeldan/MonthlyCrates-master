package dev.keldan.monthlycrates.utils.commands;

public interface BaseCommand {
    void execute(CommandContext ctx);
    default String getPermission() {
        return null;
    }
    default boolean playerOnly() {
        return false;
    }
}
