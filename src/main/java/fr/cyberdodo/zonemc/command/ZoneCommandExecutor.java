package fr.cyberdodo.zonemc.command;

import fr.cyberdodo.zonemc.entity.Zone;
import fr.cyberdodo.zonemc.service.ZoneManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ZoneCommandExecutor implements CommandExecutor {

    private final ZoneManager zoneManager;

    public ZoneCommandExecutor(ZoneManager zoneManager) {
        this.zoneManager = zoneManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage("Usage: /mczone <setzonepos1|setzonepos2|create|remove|addblock>");
            return true;
        }

        String subcommand = args[0].toLowerCase();
        switch (subcommand) {
            case "setzonepos1":
                zoneManager.setPosition1(player, player.getLocation());
                player.sendMessage("Position 1 set!");
                break;
            case "setzonepos2":
                zoneManager.setPosition2(player, player.getLocation());
                player.sendMessage("Position 2 set!");
                break;
            case "create":
                Location pos1 = zoneManager.getPosition1(player);
                Location pos2 = zoneManager.getPosition2(player);

                if (pos1 == null || pos2 == null) {
                    player.sendMessage("Both positions must be set before creating a zone.");
                    return true;
                }

                if (args.length > 1 && zoneManager.createZone(player, args[1])) {
                    player.sendMessage("Zone " + args[1] + " created!");
                } else {
                    player.sendMessage("Failed to create zone. Make sure both positions are set and valid.");
                }
                break;
            case "remove":
                if (args.length > 1 && zoneManager.removeZone(args[1])) {
                    player.sendMessage("Zone " + args[1] + " removed!");
                } else {
                    player.sendMessage("Failed to remove zone.");
                }
                break;
            case "addblock":
                if (args.length > 2) {
                    String zoneName = args[1];
                    Material material = Material.matchMaterial(args[2]);
                    int amount = args.length > 3 ? Integer.parseInt(args[3]) : 1;

                    Zone zone = zoneManager.getZone(zoneName);
                    if (zone != null && material != null) {
                        zone.addRequiredBlock(material, amount);
                        player.sendMessage(amount + " " + material.name() + " added to zone " + zoneName);
                    } else {
                        player.sendMessage("Zone or material not found.");
                    }
                } else {
                    player.sendMessage("Usage: /mczone addblock <zone> <material> [amount]");
                }
                break;
            default:
                player.sendMessage("Unknown command. Usage: /mczone <setzonepos1|setzonepos2|create|remove|addblock>");
                break;
        }

        return true;
    }
}
