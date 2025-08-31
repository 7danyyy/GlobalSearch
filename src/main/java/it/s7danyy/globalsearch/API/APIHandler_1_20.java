package it.s7danyy.globalsearch.API;

import it.s7danyy.globalsearch.Globalsearch;

public class APIHandler_1_20 implements APIHandler {
    private final Globalsearch plugin;
    public APIHandler_1_20(Globalsearch plugin) { this.plugin = plugin; }

    @Override
    public void onEnable() {
        plugin.getLogger().info("Loading handler for 1.20.x");
    }

    @Override
    public void onDisable() { }
}