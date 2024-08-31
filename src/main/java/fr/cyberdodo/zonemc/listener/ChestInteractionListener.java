package fr.cyberdodo.zonemc.listener;

import fr.cyberdodo.zonemc.service.ZoneManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ChestInteractionListener implements Listener {

    private final ZoneManager zoneManager;

    public ChestInteractionListener(ZoneManager zoneManager) {
        this.zoneManager = zoneManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Implémenter la logique pour gérer l'ajout et le retrait d'objets dans les coffres
        // et mettre à jour les listes des objets nécessaires
    }
}
