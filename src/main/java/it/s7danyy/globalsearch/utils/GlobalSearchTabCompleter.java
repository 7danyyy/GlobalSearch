package it.s7danyy.globalsearch.utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GlobalSearchTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("globalsearch")) {
            return null;
        }

        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            suggestions.add("hand");
            suggestions.add("item");
            return filterByArg(suggestions, args[0]);
        }

        if (args.length >= 2) {
            String firstArg = args[0].toLowerCase();

            if (firstArg.equals("hand")) {
                if (args.length == 2) {
                    suggestions.add("q:");
                    suggestions.add("+offline");
                    return filterByArg(suggestions, args[1]);
                }
                return new ArrayList<>();
            }

            if (firstArg.equals("item")) {
                if (args.length == 2) {
                    suggestions.add("t:");
                    suggestions.add("n:");
                    suggestions.add("l:");
                    suggestions.add("q:");
                    suggestions.add("ia:");
                    suggestions.add("+tag");
                    suggestions.add("+offline");
                    return filterByArg(suggestions, args[1]);
                }
                suggestions.add("t:");
                suggestions.add("n:");
                suggestions.add("l:");
                suggestions.add("q:");
                suggestions.add("ia:");
                suggestions.add("+tag");
                suggestions.add("+offline");
                return filterByArg(suggestions, args[args.length - 1]);
            }
        }

        return new ArrayList<>();
    }

    private List<String> filterByArg(List<String> suggestions, String arg) {
        return suggestions.stream()
                .filter(s -> s.toLowerCase().startsWith(arg.toLowerCase()))
                .collect(Collectors.toList());
    }
}
