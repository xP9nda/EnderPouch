package xp9nda.enderpouch.handlers;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
                commandSender.sendMessage(miniMsg.deserialize(configHandler.getPlayerNotFoundErrorMessage()));
                return;
            }

            playerToGivePouchTo = playerToGive;
        }

        // check that the player to give the pouch to has an open inventory slot
        if (!inventoryUtils.hasAvailableSlot(playerToGivePouchTo)) {
            commandSender.sendMessage(miniMsg.deserialize(configHandler.getGiveEnderPouchInventoryFullMessage()));
            return;
        }

        // give the player the ender pouch
        givePlayerEnderPouch(playerToGivePouchTo);

        // send the command sender a message
        String messageToSend = configHandler.getGiveEnderPouchSuccessMessage();

        if (silent == null) {
            // send the player who received the pouch a message if the award should not be silent
            playerToGivePouchTo.sendMessage(miniMsg.deserialize(configHandler.getGiveEnderPouchReceivedMessage(),
                    Placeholder.unparsed("giver", commandSender.getName())
            ));
        } else {
            if (silent.equalsIgnoreCase("-s")) {
                messageToSend += " <#6e5d83>(given silently)";
            } else {
                // send the player who received the pouch a message if the award should not be silent
                playerToGivePouchTo.sendMessage(miniMsg.deserialize(configHandler.getGiveEnderPouchReceivedMessage(),
                        Placeholder.unparsed("giver", commandSender.getName())
                ));
            }
        }

        commandSender.sendMessage(miniMsg.deserialize(messageToSend,
                Placeholder.unparsed("player", playerToGivePouchTo.getName())
        ));
    }

    // pouch use method
    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event) {
        // check that the interaction was a right click and was not from the offhand slot
        if (!event.getAction().name().contains("RIGHT_CLICK") || event.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }

        // check that the item exists
        if (event.getItem() == null) {
            return;
        }

        // get the item in the player's main hand
        var item = event.getItem();
        assert item != null;
        var itemMeta = item.getItemMeta().getPersistentDataContainer();

        // check that the item is an ender pouch
        if (!itemMeta.has(enderPouchKey, PersistentDataType.STRING)) {
            event.setCancelled(true);
            return;
        }

        // check that the player is not attempting to place the pouch
        if (event.getClickedBlock() != null) {
            event.getPlayer().sendMessage(miniMsg.deserialize(configHandler.getPlaceEnderPouchMessage()));
            event.setCancelled(true);
            return;
        }

        // open the player's ender chest
        event.getPlayer().openInventory(event.getPlayer().getEnderChest());

        // send the player a message
        event.getPlayer().sendMessage(miniMsg.deserialize(configHandler.getOpenEnderPouchMessage()));
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
}
