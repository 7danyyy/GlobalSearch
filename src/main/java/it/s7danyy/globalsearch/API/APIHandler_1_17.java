package it.s7danyy.globalsearch.API;

import it.s7danyy.globalsearch.Globalsearch;

public class APIHandler_1_17 implements APIHandler {
    private final Globalsearch plugin;
    public APIHandler_1_17(Globalsearch plugin) { this.plugin = plugin; }

    @Override
    public void onEnable() {
        plugin.getLogger().info("Loading handler for 1.17.x");
    }

    @Override
    public void onDisable() {
    }
}