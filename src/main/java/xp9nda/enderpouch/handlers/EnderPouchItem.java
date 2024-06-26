package xp9nda.enderpouch.handlers;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.destroystokyo.paper.event.inventory.PrepareResultEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import xp9nda.enderpouch.EnderPouch;
import xp9nda.enderpouch.utils.InventoryUtils;

import java.util.UUID;

public class EnderPouchItem implements Listener {

    // variables
    private final Plugin plugin;
    private final EnderPouch pluginClass;
    private final ConfigHandler configHandler;
    private final InventoryUtils inventoryUtils;
    private final MiniMessage miniMsg = MiniMessage.miniMessage();

    private final NamespacedKey enderPouchKey;

    // constructor
    public EnderPouchItem(Plugin loader) {
        plugin = loader;
        pluginClass = (EnderPouch) plugin;
        configHandler = pluginClass.getConfigHandler();
        inventoryUtils = pluginClass.getInventoryUtils();

        enderPouchKey = new NamespacedKey(plugin, "ender_pouch");
    }

    // pouch give command
    @CommandMethod("ender|enderpouch give [player] [silent]")
    @CommandPermission("enderpouch.give")
    public void givePouchCommand(
            Player commandSender,
            @Argument("player") Player playerToGive,
            @Argument("silent") String silent
    ) {
        Player playerToGivePouchTo;

        if (playerToGive == null) {
            playerToGivePouchTo = commandSender;
        } else {
            // Check that the player to give the pouch to is valid and the user is not offline
            if (pluginClass.getServer().getPlayer(playerToGive.getUniqueId()) == null || !playerToGive.isOnline()) {
                if (!configHandler.getPlayerNotFoundErrorMessage().isEmpty()) {
                    commandSender.sendMessage(miniMsg.deserialize(configHandler.getPlayerNotFoundErrorMessage()));
                }
                return;
            }

            playerToGivePouchTo = playerToGive;
        }

        // check that the player to give the pouch to has an open inventory slot
        if (!inventoryUtils.hasAvailableSlot(playerToGivePouchTo)) {
            if (!configHandler.getGiveEnderPouchInventoryFullMessage().isEmpty()) {
                commandSender.sendMessage(miniMsg.deserialize(configHandler.getGiveEnderPouchInventoryFullMessage()));
            }
            return;
        }

        // give the player the ender pouch
        givePlayerEnderPouch(playerToGivePouchTo);

        // send the command sender a message
        String messageToSend = configHandler.getGiveEnderPouchSuccessMessage();

        if (silent == null) {
            // send the player who received the pouch a message if the award should not be silent
            if (!configHandler.getGiveEnderPouchReceivedMessage().isEmpty()) {
                playerToGivePouchTo.sendMessage(miniMsg.deserialize(configHandler.getGiveEnderPouchReceivedMessage(),
                        Placeholder.unparsed("giver", commandSender.getName())
                ));
            }
        } else {
            if (silent.equalsIgnoreCase("-s")) {
                messageToSend += " <#6e5d83>(given silently)";
            } else {
                // send the player who received the pouch a message if the award should not be silent
                if (!configHandler.getGiveEnderPouchReceivedMessage().isEmpty()) {
                    playerToGivePouchTo.sendMessage(miniMsg.deserialize(configHandler.getGiveEnderPouchReceivedMessage(),
                            Placeholder.unparsed("giver", commandSender.getName())
                    ));
                }
            }
        }

        if (!messageToSend.isEmpty()) {
            commandSender.sendMessage(miniMsg.deserialize(messageToSend,
                    Placeholder.unparsed("player", playerToGivePouchTo.getName())
            ));
        }
    }

    // pouch use method
    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event) {
        // check that the event is a right click event
        Action action = event.getAction();
        if (!(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        // check that the item exists
        var item = event.getItem();
        if (item == null) {
            return;
        }

        // get the item in the player's main hand
        var itemMeta = item.getItemMeta().getPersistentDataContainer();

        // check that the item is an ender pouch
        if (!itemMeta.has(enderPouchKey, PersistentDataType.STRING)) {
            return;
        }

        // If the ender pouch is in the offhand, cancel the event continue code execution
        if (event.getHand() == EquipmentSlot.OFF_HAND) {
            event.setCancelled(true);
        }

        // check that the player is not attempting to place the pouch
        if (event.getClickedBlock() != null) {
            if (!configHandler.getPlaceEnderPouchMessage().isEmpty()) {
                event.getPlayer().sendMessage(miniMsg.deserialize(configHandler.getPlaceEnderPouchMessage()));
            }
            event.setCancelled(true);

            // cancel the event if the config dictates that the player should not be able to use the item unless right-clicking in the air
            if (configHandler.isRightClickInAirRequired()) {
                return;
            }
        }

        // check that the player is not attempting to use the item (right-click air)
        if (event.getClickedBlock() == null && event.getItem() != null) {
            event.setCancelled(true);
        }

        // open the player's ender chest
        event.getPlayer().openInventory(event.getPlayer().getEnderChest());

        // send the player a message
        if (!configHandler.getOpenEnderPouchMessage().isEmpty()) {
            event.getPlayer().sendMessage(miniMsg.deserialize(configHandler.getOpenEnderPouchMessage()));
        }
    }

    // give player ender pouch
    public void givePlayerEnderPouch(Player player) {
        // get the ender pouch material
        Material enderPouchMaterial = configHandler.getEnderPouchMaterial();
        if (enderPouchMaterial == null) {
            pluginClass.getSLF4JLogger().error("Ender pouch material is incorrect, defaulting to ENDER_CHEST.");
            enderPouchMaterial = Material.ENDER_CHEST;
        }

        // create the new itemstack
        ItemStack itemStack = new ItemStack(enderPouchMaterial);

        // edit the item meta
        itemStack.editMeta(meta -> {
            // set display name
            meta.displayName(miniMsg.deserialize(
                    configHandler.getEnderPouchDisplayName()
            ).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));

            // set item lore
            meta.lore(
                    configHandler.getEnderPouchLore().stream().map(
                            loreMsg -> miniMsg.deserialize(loreMsg).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                    ).toList()
            );

            // set up nbt data
            PersistentDataContainer dataContainer = meta.getPersistentDataContainer();

            // set the ender pouch key
            if (configHandler.isEnderPouchStackable()) {
                dataContainer.set(enderPouchKey, PersistentDataType.STRING, "0");
            } else {
                dataContainer.set(enderPouchKey, PersistentDataType.STRING, UUID.randomUUID().toString());
            }

            // set glowing by adding an enchantment and hiding enchantment flags
            if (configHandler.isEnderPouchGlowing()) {
                meta.addEnchant(Enchantment.CHANNELING, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        });

        // give the player the item
        player.getInventory().addItem(itemStack);
    }

    // When an item is put into an inventory containing a result slot, check that the item is not an ender pouch
    @EventHandler
    public void onPrepareResult(PrepareResultEvent event) {
        // check that an item is being prepared
        if (event.getResult() == null) {
            return;
        }

        // check if the result item is an ender pouch
        var itemMeta = event.getResult().getItemMeta().getPersistentDataContainer();
        if (!itemMeta.has(enderPouchKey, PersistentDataType.STRING)) {
            return;
        }

        // cancel the event
        event.setResult(null);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Check the type of inventory
        InventoryType invType = event.getInventory().getType();

        // Check that the inventory is an ender chest
        if (invType != InventoryType.ENDER_CHEST) {
            return;
        }

        ItemStack item;
        var hotbarButton = event.getHotbarButton();
        var clickedInventory = event.getClickedInventory();
        var whoClicked = event.getWhoClicked();

        // If a hotbar button is pressed, and the item in the hotbar is an ender pouch, cancel the event
        if (hotbarButton != -1) {
            item = whoClicked.getInventory().getItem(hotbarButton);
            if (item == null) {
                return;
            }
        } else {
            item = event.getCurrentItem();
            if (item == null) {
                return;
            }

            // Check if the clicked inventory is the ender chest and the cursor item is moving to the player's inventory
            if (clickedInventory != null && clickedInventory.getType() == InventoryType.ENDER_CHEST && event.getSlotType() == InventoryType.SlotType.QUICKBAR) {
                if (event.getCursor().getType() != Material.AIR) {
                    item = event.getCursor();
                }
            }
        }

        // Check that the item is not an ender pouch
        var itemMeta = item.getItemMeta().getPersistentDataContainer();
        if (!itemMeta.has(enderPouchKey, PersistentDataType.STRING)) {
            return;
        }

        // Return if the config allows ender pouch storage in ender chests
        if (configHandler.isAllowEnderChestStorage()) {
            return;
        }

        // Cancel the event
        event.setCancelled(true);

        // Send the player a message
        if (!configHandler.getAttemptStorageMessage().isEmpty()) {
            whoClicked.sendMessage(miniMsg.deserialize(configHandler.getAttemptStorageMessage()));
        }
    }


    // on item drop
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        // get the dropped item
        var item = event.getItemDrop().getItemStack();

        // check that the item is not an ender pouch
        var itemMeta = item.getItemMeta().getPersistentDataContainer();
        if (!itemMeta.has(enderPouchKey, PersistentDataType.STRING)) {
            return;
        }

        // check if item dropping is allowed
        if (configHandler.isAllowEnderChestDrop()) {
            // set the item to invulnerable if the config allows it
            event.getItemDrop().setInvulnerable(configHandler.isDroppedItemInvulnerable());

            return;
        }

        // cancel the event
        event.setCancelled(true);

        // send the player a message
        if (!configHandler.getAttemptDropMessage().isEmpty()) {
            event.getPlayer().sendMessage(miniMsg.deserialize(configHandler.getAttemptDropMessage()));
        }
    }

    // on despawn
    @EventHandler
    public void onDespawn(ItemDespawnEvent event) {
        // check that ender pouch despawning is allowed
        if (configHandler.isAllowEnderChestDespawn()) {
            return;
        }

        // if the item is not allowed to despawn, do the following
        // get the despawning item
        var item = event.getEntity().getItemStack();

        // check that the item is not an ender pouch
        var itemMeta = item.getItemMeta().getPersistentDataContainer();
        if (!itemMeta.has(enderPouchKey, PersistentDataType.STRING)) {
            return;
        }

        // cancel the event
        event.setCancelled(true);
    }

}