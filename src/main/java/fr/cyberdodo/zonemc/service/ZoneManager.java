package fr.cyberdodo.zonemc.service;

import fr.cyberdodo.zonemc.entity.Zone;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ZoneManager {
    private final DatabaseManager databaseManager;
    private final Map<String, Zone> zones = new HashMap<>();
    private final Map<Player, Location> position1Map = new HashMap<>();
    private final Map<Player, Location> position2Map = new HashMap<>();

    public ZoneManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        initializeDatabase();
        loadZonesFromDatabase();
    }

    private void initializeDatabase() {
        try (Connection connection = databaseManager.getConnection()) {
            String createZonesTable = "CREATE TABLE IF NOT EXISTS zones (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "world TEXT NOT NULL," +
                    "pos1X DOUBLE," +
                    "pos1Y DOUBLE," +
                    "pos1Z DOUBLE," +
                    "pos2X DOUBLE," +
                    "pos2Y DOUBLE," +
                    "pos2Z DOUBLE" +
                    ");";
            try (PreparedStatement statement = connection.prepareStatement(createZonesTable)) {
                statement.execute();
            }

            String createBlocksTable = "CREATE TABLE IF NOT EXISTS required_blocks (" +
                    "zone_id INTEGER," +
                    "material TEXT NOT NULL," +
                    "amount INTEGER NOT NULL," +
                    "FOREIGN KEY(zone_id) REFERENCES zones(id) ON DELETE CASCADE" +
                    ");";
            try (PreparedStatement statement = connection.prepareStatement(createBlocksTable)) {
                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadZonesFromDatabase() {
        try (Connection connection = databaseManager.getConnection()) {
            String query = "SELECT * FROM zones;";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    String name = resultSet.getString("name");
                    String world = resultSet.getString("world");
                    Location pos1 = new Location(
                            org.bukkit.Bukkit.getWorld(world),
                            resultSet.getDouble("pos1X"),
                            resultSet.getDouble("pos1Y"),
                            resultSet.getDouble("pos1Z")
                    );
                    Location pos2 = new Location(
                            org.bukkit.Bukkit.getWorld(world),
                            resultSet.getDouble("pos2X"),
                            resultSet.getDouble("pos2Y"),
                            resultSet.getDouble("pos2Z")
                    );
                    Zone zone = new Zone(name, pos1, pos2);
                    loadRequiredBlocks(zone, resultSet.getInt("id"));
                    zones.put(name, zone);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadRequiredBlocks(Zone zone, int zoneId) {
        try (Connection connection = databaseManager.getConnection()) {
            String query = "SELECT * FROM required_blocks WHERE zone_id = ?;";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, zoneId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        Material material = Material.valueOf(resultSet.getString("material"));
                        int amount = resultSet.getInt("amount");
                        zone.addRequiredBlock(material, amount);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<Material, Integer> getRequiredBlocks(String zoneName) {
        Zone zone = getZone(zoneName);
        return zone != null ? zone.getRequiredBlocks() : null;
    }

    public boolean createZone(Player player, String zoneName) {
        Location pos1 = getPosition1(player);
        Location pos2 = getPosition2(player);

        if (pos1 == null || pos2 == null) {
            player.sendMessage("Both positions must be set before creating a zone.");
            return false;
        }

        try (Connection connection = databaseManager.getConnection()) {
            String insertZone = "INSERT INTO zones (name, world, pos1X, pos1Y, pos1Z, pos2X, pos2Y, pos2Z) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
            try (PreparedStatement statement = connection.prepareStatement(insertZone)) {
                statement.setString(1, zoneName);
                statement.setString(2, pos1.getWorld().getName());
                statement.setDouble(3, pos1.getX());
                statement.setDouble(4, pos1.getY());
                statement.setDouble(5, pos1.getZ());
                statement.setDouble(6, pos2.getX());
                statement.setDouble(7, pos2.getY());
                statement.setDouble(8, pos2.getZ());
                statement.executeUpdate();
            }

            zones.put(zoneName, new Zone(zoneName, pos1, pos2));
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean removeZone(String zoneName) {
        try (Connection connection = databaseManager.getConnection()) {
            String deleteZone = "DELETE FROM zones WHERE name = ?;";
            try (PreparedStatement statement = connection.prepareStatement(deleteZone)) {
                statement.setString(1, zoneName);
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    zones.remove(zoneName);
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Zone getZone(String zoneName) {
        return zones.get(zoneName);
    }

    public Map<String, Zone> getZones() {
        return zones;
    }

    public void setPosition1(Player player, Location location) {
        position1Map.put(player, location);
    }

    public void setPosition2(Player player, Location location) {
        position2Map.put(player, location);
    }

    public Location getPosition1(Player player) {
        return position1Map.get(player);
    }

    public Location getPosition2(Player player) {
        return position2Map.get(player);
    }
}
