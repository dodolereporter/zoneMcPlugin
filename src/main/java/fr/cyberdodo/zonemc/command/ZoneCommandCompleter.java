package fr.cyberdodo.zonemc.command;

import fr.cyberdodo.zonemc.service.ZoneManager;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ZoneCommandCompleter implements TabCompleter {

    private final ZoneManager zoneManager;

    public ZoneCommandCompleter(ZoneManager zoneManager) {
        this.zoneManager = zoneManager;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) return null;

        if (args.length == 1) {
            return Arrays.asList("setzonepos1", "setzonepos2", "create", "remove", "addblock", "removeblock", "getblocks", "list");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("addblock") ||
                    args[0].equalsIgnoreCase("removeblock") || args[0].equalsIgnoreCase("getblocks")) {
                return new ArrayList<>(zoneManager.getZones().keySet());
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("addblock") || args[0].equalsIgnoreCase("removeblock")) {
                return Arrays.stream(Material.values())
                        .map(Material::name)
                        .collect(Collectors.toList());
            }
        }
        return null;
    }
}
