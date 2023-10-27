package xp9nda.enderpouch.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import xp9nda.enderpouch.EnderPouch;

public class InventoryUtils {

    // variables
    private final Plugin plugin;
    private EnderPouch pluginClass;

    // constructor
    public InventoryUtils(Plugin loader) {
        plugin = loader;
        pluginClass = (EnderPouch) plugin;
    }

    // check if the player has an available inventory slot in the main inventory
    public boolean hasAvailableSlot(Player player) {
        // Check if the player is valid
        if (player == null) {
            return false;
        }

        Inventory inv = player.getInventory();

        // Iterate through the main inventory slots (0-35)
        for (int i = 0; i < 36; i++) {
            ItemStack item = inv.getItem(i);
            if (item == null) {
                return true;
            }
        }

        return false;
    }
}
