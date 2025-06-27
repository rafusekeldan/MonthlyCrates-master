package dev.keldan.monthlycrates;

import de.tr7zw.nbtapi.NBT;
import dev.keldan.monthlycrates.command.CrateCommand;
import dev.keldan.monthlycrates.utils.commands.CommandFramework;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class MonthlyCrates extends JavaPlugin {

    @Getter
    private static MonthlyCrates instance;
    CommandFramework framework;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        framework = new CommandFramework(this);
        registerCommands();
        registerAPIs();
    }

    private void registerCommands() {
        CrateCommand crateCommand = new CrateCommand();
        framework.registerCommand("crate", crateCommand);
        getCommand("crate").setTabCompleter(crateCommand);
    }

    public static MonthlyCrates getInstance() {
        return instance;
    }

    private void registerAPIs() {
        if (!NBT.preloadApi()) {
            getLogger().warning("NBT-API wasn't initialized properly, disabling the plugin.");
            getPluginLoader().disablePlugin(this);
            return;
        }
    }
}
