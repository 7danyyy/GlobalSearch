package it.s7danyy.globalsearch.commands;

import dev.lone.itemsadder.api.CustomStack;
import it.s7danyy.globalsearch.Globalsearch;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GlobalSearchCommand implements CommandExecutor {

    private final Globalsearch plugin;
    private final FileConfiguration cfg;
    private final String usageLang;

    public GlobalSearchCommand(Globalsearch plugin) {
        this.plugin = plugin;
        this.cfg    = plugin.getConfig();
        this.usageLang = plugin.getConfig().getString("usage-lang").toUpperCase();

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        if (args.length < 1) {
            sendUsage(player);
            return true;
        }

        String sub = args[0].toLowerCase();
        if (!sub.equals("hand") && !sub.equals("item")) {
            sendUsage(player);
            return true;
        }

        boolean offline   = false;
        int     quantity  = 0;
        boolean tagSearch = false;
        String  typeFilter = null, nameFilter = null, loreFilter = null, iaFilter = null;

        for (int i = 1; i < args.length; i++) {
            String arg = args[i];
            if (arg.equalsIgnoreCase("+offline")) {
                offline = true;
            } else if (arg.startsWith("q:")) {
                try { quantity = Integer.parseInt(arg.substring(2)); }
                catch (NumberFormatException ignored) {}
            } else if (sub.equals("item")) {
                if      (arg.startsWith("t:"))  typeFilter = arg.substring(2).toUpperCase();
                else if (arg.startsWith("n:"))  nameFilter = arg.substring(2).replace("_", " ");
                else if (arg.startsWith("l:"))  loreFilter = arg.substring(2).replace("_", " ");
                else if (arg.startsWith("ia:")) iaFilter   = arg.substring(3);
                else if (arg.equalsIgnoreCase("+tag")) tagSearch = true;
            }
        }

        if (sub.equals("item") && (typeFilter == null || typeFilter.isEmpty())) {
            player.sendMessage(colorize(cfg.getString("messages.missing-t-param")));
            return true;
        }

        ItemStack reference = null;
        if (sub.equals("hand")) {
            reference = player.getInventory().getItemInMainHand();
            if (reference == null || reference.getType().isAir()) {
                player.sendMessage(colorize(cfg.getString("messages.no-hand-item")));
                return true;
            }
        }

        String worldName = cfg.getString("search.world", "world");
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            player.sendMessage(
                    colorize(
                            cfg.getString("messages.world-not-found")
                                    .replace("{world}", worldName)
                    )
            );
            return true;
        }

        for (Chunk chunk : world.getLoadedChunks()) {
            for (BlockState bs : chunk.getTileEntities()) {
                if (bs instanceof Container) {
                    Container container = (Container) bs;
                    ItemStack[] contents = container.getInventory().getContents();
                    for (int slot = 0; slot < contents.length; slot++) {
                        ItemStack item = contents[slot];
                        if (item != null && matches(sub, item, reference,
                                typeFilter, nameFilter, loreFilter, iaFilter, tagSearch, quantity)) {
                            sendContainerMessage(player, container, bs.getLocation(), slot);
                        }
                    }
                }
            }
        }

        for (Player online : Bukkit.getOnlinePlayers()) {
            ItemStack[] contents = online.getInventory().getContents();
            for (int slot = 0; slot < contents.length; slot++) {
                ItemStack item = contents[slot];
                if (item != null && matches(sub, item, reference,
                        typeFilter, nameFilter, loreFilter, iaFilter, tagSearch, quantity)) {
                    sendOnlinePlayerMessage(player, online);
                }
            }
        }

        return true;
    }

    private boolean matches(String sub, ItemStack item, ItemStack reference,
                            String typeFilter, String nameFilter, String loreFilter,
                            String iaFilter, boolean tagSearch, int quantity) {

        if (sub.equals("hand")) {
            if (!item.isSimilar(reference)) return false;
            return quantity <= 0 || item.getAmount() >= quantity;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        if (iaFilter != null) {
            CustomStack cs = CustomStack.byItemStack(item);
            if (cs == null || !cs.getId().equalsIgnoreCase(iaFilter)) return false;
        }
        else if (typeFilter != null) {
            try {
                if (item.getType() != Material.valueOf(typeFilter)) return false;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }

        if (quantity > 0 && item.getAmount() < quantity) return false;

        if (nameFilter != null && !nameFilter.isEmpty()) {
            if (!meta.hasDisplayName() ||
                    !meta.getDisplayName().toLowerCase().contains(nameFilter.toLowerCase()))
                return false;
        }

        if (loreFilter != null && !loreFilter.isEmpty()) {
            if (!meta.hasLore()) return false;
            boolean found = false;
            for (String line : meta.getLore()) {
                if (line.toLowerCase().contains(loreFilter.toLowerCase())) {
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }

        return true;
    }

    private void sendUsage(Player p) {
        if ("EN".equals(usageLang)) {
            p.sendMessage("  §6§lGlobalSearch §r§8| §6Usage Guide");
            p.sendMessage("");
            p.sendMessage("  §2» §a/globalsearch hand [params]");
            p.sendMessage("  §7> §6Param q: §e(Filter by quantity)");
            p.sendMessage("  §7> §6Param +offline §e(Search offline inventories)");
            p.sendMessage("");
            p.sendMessage("  §2» §a/globalsearch item [params]");
            p.sendMessage("  §7> §6Param t: §e(Filter by item type) §c* Required");
            p.sendMessage("  §7> §6Param n: §e(Filter by name)");
            p.sendMessage("  §7> §6Param l: §e(Filter by lore)");
            p.sendMessage("  §7> §6Param q: §e(Filter by quantity)");
            p.sendMessage("  §7> §6Param +tag §e(Filter by unique tag)");
            p.sendMessage("  §7> §6Param ia: §e(Filter by ItemsAdder ID)");
            p.sendMessage("  §7> §6Param +offline §e(Search offline inventories)");
            p.sendMessage("");
            p.sendMessage("§f» Examples");
            p.sendMessage("§8♦ §7/gsearch hand +offline");
            p.sendMessage("§8♦ §7/gsearch item t:STICK n:Guitar l:Legendary");
        } else {
            p.sendMessage("  §6§lGlobalSearch §r§8| §6Guida all'utilizzo");
            p.sendMessage("");
            p.sendMessage("  §2» §a/globalsearch hand [parametri]");
            p.sendMessage("  §7> §6Parametro q: §e(Filtra per quantità)");
            p.sendMessage("  §7> §6Parametro +offline §e(Cerca anche tra gli inventari offline)");
            p.sendMessage("");
            p.sendMessage("  §2» §a/globalsearch item [parametri]");
            p.sendMessage("  §7> §6Parametro t: §e(Filtra per tipo di oggetto) §c* Obbligatorio");
            p.sendMessage("  §7> §6Parametro n: §e(Filtra per nome)");
            p.sendMessage("  §7> §6Parametro l: §e(Filtra per lore)");
            p.sendMessage("  §7> §6Parametro q: §e(Filtra per quantità)");
            p.sendMessage("  §7> §6Parametro +tag §e(Filtra per l'ID univoco dell'item)");
            p.sendMessage("  §7> §6Parametro ia: §e(Filtra per ID univoco ItemsAdder)");
            p.sendMessage("  §7> §6Parametro +offline §e(Cerca anche tra gli inventari offline)");
            p.sendMessage("");
            p.sendMessage("§f» Comandi di esempio");
            p.sendMessage("§8♦ §7/gsearch hand +offline");
            p.sendMessage("§8♦ §7/gsearch item t:STICK n:Chitarra l:Item appartenente a 7danyy_ ");
        }
    }

    private void sendContainerMessage(Player p, Container container, Location loc, int slot) {
        String template = cfg.getString("messages.container-found-message");
        String text = template
                .replace("{x}", String.valueOf(loc.getBlockX()))
                .replace("{y}", String.valueOf(loc.getBlockY()))
                .replace("{z}", String.valueOf(loc.getBlockZ()));

        TextComponent tp = new TextComponent(colorize(cfg.getString("messages.teleport-button")));
        tp.setClickEvent(new ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                "/tp " + p.getName() + " " +
                        loc.getBlockX() + " " +
                        loc.getBlockY() + " " +
                        loc.getBlockZ()
        ));
        tp.setHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(colorize(
                        cfg.getString("messages.teleport-hover")
                                .replace("{target}", loc.getBlockX()+","+loc.getBlockY()+","+loc.getBlockZ())
                )).create()
        ));

        TextComponent msg = new TextComponent(colorize(text));
        msg.setColor(net.md_5.bungee.api.ChatColor.GRAY);

        TextComponent out = new TextComponent();
        out.addExtra(tp);
        out.addExtra(msg);
        p.spigot().sendMessage(out);
    }

    private void sendOnlinePlayerMessage(Player p, Player found) {
        TextComponent tp = new TextComponent(colorize(cfg.getString("messages.teleport-button")));
        tp.setClickEvent(new ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                "/tp " + p.getName() + " " + found.getName()
        ));
        tp.setHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(colorize(
                        cfg.getString("messages.teleport-hover")
                                .replace("{target}", found.getName())
                )).create()
        ));

        String template = cfg.getString("messages.online-found-message");
        String text = template.replace("{player}", found.getName());

        TextComponent msg = new TextComponent(colorize(text));
        msg.setColor(net.md_5.bungee.api.ChatColor.GRAY);

        TextComponent out = new TextComponent();
        out.addExtra(tp);
        out.addExtra(msg);
        p.spigot().sendMessage(out);
    }

    private String colorize(String s) {
        return ChatColor.translateAlternateColorCodes('&', s == null ? "" : s);
    }
}
