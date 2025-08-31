package it.s7danyy.globalsearch.API;

import it.s7danyy.globalsearch.Globalsearch;

public class APIHandler_Fallback implements APIHandler {
    private final Globalsearch plugin;
    public APIHandler_Fallback(Globalsearch plugin) { this.plugin = plugin; }

    @Override
    public void onEnable() {
        plugin.getLogger().warning("Fallback APIHandler on: molte funzionalità potrebbero non funzionare.");
    }

    @Override
    public void onDisable() { }
}
