package xp9nda.enderpouch.handlers;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xp9nda.enderpouch.EnderPouch;

public class OpenCommand {

    // Variables
    private final Plugin plugin;
    private final EnderPouch pluginClass;
    private final ConfigHandler configHandler;
    private final MiniMessage miniMsg = MiniMessage.miniMessage();

    // constructor
    public OpenCommand(Plugin loader) {
        plugin = loader;
        pluginClass = (EnderPouch) plugin;
        configHandler = pluginClass.getConfigHandler();
    }

    // open enderchest
    private void openEnderChest(Player playerChestOwner, Player playerShowChestTo) {
        // check that both parameters are valid players
        if (playerShowChestTo == null) {
            return;
        }

        if (playerChestOwner == null) {
            if (!configHandler.getPlayerNotFoundErrorMessage().isEmpty()) {
                playerShowChestTo.sendMessage(miniMsg.deserialize(configHandler.getPlayerNotFoundErrorMessage()));
            }
            return;
        }

        playerShowChestTo.openInventory(playerChestOwner.getEnderChest());
    }

    // open command
    @CommandMethod("ender|enderpouch open [player]")
    @CommandPermission("enderpouch.open")
    public void openPouchCommand(
            Player commandSender,
            @Argument("player") Player playerToOpen
    ) {
        // check if the player specified a user's enderchest to open
        if (playerToOpen == null) {
            // if not, open the command sender's enderchest
            if (!configHandler.getOpenEnderPouchMessage().isEmpty()) {
                commandSender.sendMessage(miniMsg.deserialize(configHandler.getOpenEnderPouchMessage()));
            }
            openEnderChest(commandSender, commandSender);
        } else {
            // if so, check if the player has permission to open other users enderchests
            if (commandSender.hasPermission("enderpouch.open.others")) {
                // if so, open the specified player's enderchest
                openEnderChest(playerToOpen, commandSender);

                // send a message to the command sender
                if (!configHandler.getOpenEnderPouchOtherMessage().isEmpty()) {
                    commandSender.sendMessage(miniMsg.deserialize(
                            configHandler.getOpenEnderPouchOtherMessage(),
                            Placeholder.unparsed("player", playerToOpen.getName())
                    ));
                }
            } else {
                // if not, send an error message
                if (!configHandler.getOpenEnderChestOtherFailMessage().isEmpty()) {
                    commandSender.sendMessage(miniMsg.deserialize(configHandler.getOpenEnderChestOtherFailMessage()));
                }
            }
        }
    }

    // enderchest command
    @CommandMethod("enderchest|ec")
    @CommandPermission("enderpouch.open")
    public void openEnderChestCommand(
            Player commandSender
    ) {
        if (!configHandler.getOpenEnderPouchMessage().isEmpty()) {
            commandSender.sendMessage(miniMsg.deserialize(configHandler.getOpenEnderPouchMessage()));
        }
        openEnderChest(commandSender, commandSender);
    }
}
