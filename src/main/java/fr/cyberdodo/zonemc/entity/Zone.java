package fr.cyberdodo.zonemc.entity;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class Zone {

    private final String name;
    private final Location pos1;
    private final Location pos2;
    private final Map<Material, Integer> requiredBlocks;

    public Zone(String name, Location pos1, Location pos2) {
        this.name = name;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.requiredBlocks = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public Location getPos1() {
        return pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public String getWorld() {
        return pos1.getWorld().getName();
    }

    public void addRequiredBlock(Material material, int amount) {
        requiredBlocks.put(material, requiredBlocks.getOrDefault(material, 0) + amount);
    }

    public void removeRequiredBlock(Material material) {
        requiredBlocks.remove(material);
    }

    public Map<Material, Integer> getRequiredBlocks() {
        return requiredBlocks;
    }

    public void setRequiredBlocks(Map<Material, Integer> requiredBlocks) {
        this.requiredBlocks.clear();
        this.requiredBlocks.putAll(requiredBlocks);
    }

    @Override
    public String toString() {
        return "Zone{" +
                "name='" + name + '\'' +
                ", pos1=" + pos1 +
                ", pos2=" + pos2 +
                ", requiredBlocks=" + requiredBlocks +
                '}';
    }
}
