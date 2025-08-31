package it.s7danyy.globalsearch;

import it.s7danyy.globalsearch.commands.GlobalSearchCommand;
import it.s7danyy.globalsearch.utils.GlobalSearchTabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

public final class Globalsearch extends JavaPlugin {

    private VersionManager versionManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        versionManager = new VersionManager(this);
        versionManager.getHandler().onEnable();

        getCommand("globalsearch").setExecutor(new GlobalSearchCommand(this));
        getCommand("globalsearch").setTabCompleter(new GlobalSearchTabCompleter());

        getLogger().info("Globalsearch v" + getDescription().getVersion() + " enabled.");
    }

    @Override
    public void onDisable() {
        if (versionManager != null) {
            versionManager.getHandler().onDisable();
        }
        getLogger().info("Globalsearch disabled.");
    }
}
