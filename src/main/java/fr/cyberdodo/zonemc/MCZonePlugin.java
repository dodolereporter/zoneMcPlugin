package fr.cyberdodo.zonemc;

import fr.cyberdodo.zonemc.command.ZoneCommandCompleter;
import fr.cyberdodo.zonemc.command.ZoneCommandExecutor;
import fr.cyberdodo.zonemc.service.DatabaseManager;
import fr.cyberdodo.zonemc.service.ZoneManager;
import fr.cyberdodo.zonemc.web.WebServer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class MCZonePlugin extends JavaPlugin {

    private DatabaseManager databaseManager;
    private ZoneManager zoneManager;
    private WebServer webServer;

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        databaseManager = new DatabaseManager(this);
        zoneManager = new ZoneManager(databaseManager);

        this.getCommand("mczone").setExecutor(new ZoneCommandExecutor(zoneManager));
        this.getCommand("mczone").setTabCompleter(new ZoneCommandCompleter(zoneManager));

        try {
            webServer = new WebServer(8080, zoneManager);
        } catch (IOException e) {
            e.printStackTrace();
        }

        getLogger().info("MCZonePlugin enabled!");
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.closeConnection();
        }
        getLogger().info("MCZonePlugin disabled!");
    }
}
