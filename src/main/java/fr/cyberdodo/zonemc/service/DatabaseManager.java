package fr.cyberdodo.zonemc.service;

import fr.cyberdodo.zonemc.MCZonePlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private Connection connection;
    private final MCZonePlugin plugin;

    public DatabaseManager(MCZonePlugin plugin) {
        this.plugin = plugin;
        try {
            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdirs(); // Crée le répertoire du plugin s'il n'existe pas
            }
            File databaseFile = new File(dataFolder, "zones.db");
            String url = "jdbc:sqlite:" + databaseFile.getAbsolutePath();
            this.connection = DriverManager.getConnection(url);

            if (!databaseFile.exists()) {
                // Crée les tables seulement si le fichier de la base de données n'existe pas
                createTableIfNotExists();
                System.out.println("Database initialized and tables created.");
            } else {
                System.out.println("Database connected at " + databaseFile.getAbsolutePath());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                File dataFolder = plugin.getDataFolder();
                File databaseFile = new File(dataFolder, "zones.db");
                String url = "jdbc:sqlite:" + databaseFile.getAbsolutePath();
                this.connection = DriverManager.getConnection(url);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    private void createTableIfNotExists() {
        String createZonesTableSQL = "CREATE TABLE IF NOT EXISTS zones (" +
                "name TEXT PRIMARY KEY," +
                "pos1_x REAL," +
                "pos1_y REAL," +
                "pos1_z REAL," +
                "pos2_x REAL," +
                "pos2_y REAL," +
                "pos2_z REAL," +
                "world TEXT" +
                ");";

        String createRequiredBlocksTableSQL = "CREATE TABLE IF NOT EXISTS required_blocks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "zone_name TEXT," +
                "material TEXT," +
                "amount INTEGER," +
                "FOREIGN KEY(zone_name) REFERENCES zones(name)" +
                ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createZonesTableSQL);
            stmt.execute(createRequiredBlocksTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
