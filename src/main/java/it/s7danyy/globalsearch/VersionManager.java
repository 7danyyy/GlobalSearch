package it.s7danyy.globalsearch;

import it.s7danyy.globalsearch.API.*;
import org.bukkit.Bukkit;

public class VersionManager {

    public enum ServerVersion {
        v1_17, v1_18, v1_19, v1_20, UNKNOWN
    }

    private final ServerVersion version;
    private final APIHandler handler;

    public VersionManager(Globalsearch plugin) {
        this.version = detectVersion();
        this.handler = loadHandler(plugin, version);
    }

    private ServerVersion detectVersion() {
        String pkg = Bukkit.getServer().getClass().getPackage().getName();
        String ver = pkg.substring(pkg.lastIndexOf('.') + 1);
        if (ver.startsWith("v1_17")) return ServerVersion.v1_17;
        if (ver.startsWith("v1_18")) return ServerVersion.v1_18;
        if (ver.startsWith("v1_19")) return ServerVersion.v1_19;
        if (ver.startsWith("v1_20")) return ServerVersion.v1_20;
        return ServerVersion.UNKNOWN;
    }

    private APIHandler loadHandler(Globalsearch plugin, ServerVersion v) {
        switch (v) {
            case v1_17: return new APIHandler_1_17(plugin);
            case v1_18: return new APIHandler_1_18(plugin);
            case v1_19: return new APIHandler_1_19(plugin);
            case v1_20: return new APIHandler_1_20(plugin);
            default:    return new APIHandler_Fallback(plugin);
        }
    }

    public ServerVersion getVersion() {
        return version;
    }

    public APIHandler getHandler() {
        return handler;
    }
}