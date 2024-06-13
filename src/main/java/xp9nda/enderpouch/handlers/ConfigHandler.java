package xp9nda.enderpouch.handlers;

import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import xp9nda.enderpouch.EnderPouch;

import java.util.List;

public class ConfigHandler implements Listener {

    // variables
    private final Plugin plugin;
    private final MiniMessage miniMsg = MiniMessage.miniMessage();
    private EnderPouch pluginClass;

    // config variables
    private Material enderPouchMaterial;
    private String enderPouchDisplayName;
    private List<String> enderPouchLore;
    private boolean enderPouchStackable;
    private boolean enderPouchGlowing;

    private String openEnderPouchMessage;
    private String openEnderPouchOtherMessage;
    private String openEnderChestOtherFailMessage;
    private String playerNotFoundErrorMessage;
    private String giveEnderPouchInventoryFullMessage;
    private String giveEnderPouchSuccessMessage;
    private String giveEnderPouchReceivedMessage;
    private String placeEnderPouchMessage;

    private boolean rightClickInAirRequired;
    private boolean allowEnderChestDespawn;

    private String attemptStorageMessage;
    private boolean allowEnderChestStorage;

    private String attemptDropMessage;
    private boolean allowEnderChestDrop;
    private boolean droppedItemInvulnerable;

    // constructor
    public ConfigHandler(Plugin loader) {
        plugin = loader;
        pluginClass = (EnderPouch) plugin;

        this.reloadConfig();
    }

    // reload config command
    @CommandMethod("ender|enderpouch reload")
    @CommandPermission("enderpouch.reload")
    public void reloadConfigCommand(Player commandSender) {
        this.reloadConfig();
        commandSender.sendMessage(miniMsg.deserialize("<#6e5d83><b>!</b> <#9982b6>EnderPouch config reloaded..."));
    }

    // reload config method
    public void reloadConfig() {
        pluginClass.reloadConfig();

        // get config values
        this.setEnderPouchMaterial(Material.valueOf(pluginClass.getConfig().getString("enderPouch.item")));
        this.setEnderPouchDisplayName(pluginClass.getConfig().getString("enderPouch.displayName"));
        this.setEnderPouchLore(pluginClass.getConfig().getStringList("enderPouch.lore"));
        this.setEnderPouchStackable(pluginClass.getConfig().getBoolean("enderPouch.stackable"));
        this.setEnderPouchGlowing(pluginClass.getConfig().getBoolean("enderPouch.glowing"));

        this.setOpenEnderPouchMessage(pluginClass.getConfig().getString("messages.openPouch"));
        this.setOpenEnderPouchOtherMessage(pluginClass.getConfig().getString("messages.openPouchOther"));
        this.setOpenEnderChestOtherFailMessage(pluginClass.getConfig().getString("messages.openPouchOtherFail"));
        this.setPlayerNotFoundErrorMessage(pluginClass.getConfig().getString("messages.playerNotFound"));
        this.setGiveEnderPouchInventoryFullMessage(pluginClass.getConfig().getString("messages.givePlayerInventoryFull"));
        this.setGiveEnderPouchSuccessMessage(pluginClass.getConfig().getString("messages.givePlayerSuccess"));
        this.setGiveEnderPouchReceivedMessage(pluginClass.getConfig().getString("messages.giveReceived"));
        this.setPlaceEnderPouchMessage(pluginClass.getConfig().getString("messages.attemptPlace"));

        this.setRightClickInAirRequired(pluginClass.getConfig().getBoolean("other.rightClickInAirRequired"));
        this.setAllowEnderChestStorage(pluginClass.getConfig().getBoolean("other.allowEnderChestStorage"));
        this.setAllowEnderChestDrop(pluginClass.getConfig().getBoolean("other.allowEnderChestDrop"));
        this.setAllowEnderChestDespawn(pluginClass.getConfig().getBoolean("other.allowEnderChestDespawn"));

        this.setAttemptStorageMessage(pluginClass.getConfig().getString("messages.attemptStorage"));
        this.setAttemptDropMessage(pluginClass.getConfig().getString("messages.attemptDrop"));
        this.setDroppedItemInvulnerable(pluginClass.getConfig().getBoolean("other.dropInvulnerable"));
    }

    public Material getEnderPouchMaterial() {
        return enderPouchMaterial;
    }

    public void setEnderPouchMaterial(Material enderPouchMaterial) {
        this.enderPouchMaterial = enderPouchMaterial;
    }

    public String getEnderPouchDisplayName() {
        return enderPouchDisplayName;
    }

    public void setEnderPouchDisplayName(String enderPouchDisplayName) {
        this.enderPouchDisplayName = enderPouchDisplayName;
    }

    public String getOpenEnderPouchMessage() {
        return openEnderPouchMessage;
    }

    public void setOpenEnderPouchMessage(String openEnderPouchMessage) {
        this.openEnderPouchMessage = openEnderPouchMessage;
    }

    public List<String> getEnderPouchLore() {
        return enderPouchLore;
    }

    public void setEnderPouchLore(List<String> enderPouchLore) {
        this.enderPouchLore = enderPouchLore;
    }

    public boolean isEnderPouchStackable() {
        return enderPouchStackable;
    }

    public void setEnderPouchStackable(boolean enderPouchStackable) {
        this.enderPouchStackable = enderPouchStackable;
    }

    public boolean isEnderPouchGlowing() {
        return enderPouchGlowing;
    }

    public void setEnderPouchGlowing(boolean enderPouchGlowing) {
        this.enderPouchGlowing = enderPouchGlowing;
    }

    public String getGiveEnderPouchReceivedMessage() {
        return giveEnderPouchReceivedMessage;
    }

    public void setGiveEnderPouchReceivedMessage(String giveEnderPouchReceivedMessage) {
        this.giveEnderPouchReceivedMessage = giveEnderPouchReceivedMessage;
    }

    public String getGiveEnderPouchSuccessMessage() {
        return giveEnderPouchSuccessMessage;
    }

    public void setGiveEnderPouchSuccessMessage(String giveEnderPouchSuccessMessage) {
        this.giveEnderPouchSuccessMessage = giveEnderPouchSuccessMessage;
    }

    public String getGiveEnderPouchInventoryFullMessage() {
        return giveEnderPouchInventoryFullMessage;
    }

    public void setGiveEnderPouchInventoryFullMessage(String giveEnderPouchInventoryFullMessage) {
        this.giveEnderPouchInventoryFullMessage = giveEnderPouchInventoryFullMessage;
    }

    public String getPlaceEnderPouchMessage() {
        return placeEnderPouchMessage;
    }

    public void setPlaceEnderPouchMessage(String placeEnderPouchMessage) {
        this.placeEnderPouchMessage = placeEnderPouchMessage;
    }

    public String getOpenEnderPouchOtherMessage() {
        return openEnderPouchOtherMessage;
    }

    public void setOpenEnderPouchOtherMessage(String openEnderPouchOtherMessage) {
        this.openEnderPouchOtherMessage = openEnderPouchOtherMessage;
    }

    public String getOpenEnderChestOtherFailMessage() {
        return openEnderChestOtherFailMessage;
    }

    public void setOpenEnderChestOtherFailMessage(String openEnderChestOtherFailMessage) {
        this.openEnderChestOtherFailMessage = openEnderChestOtherFailMessage;
    }

    public String getPlayerNotFoundErrorMessage() {
        return playerNotFoundErrorMessage;
    }

    public void setPlayerNotFoundErrorMessage(String playerNotFoundErrorMessage) {
        this.playerNotFoundErrorMessage = playerNotFoundErrorMessage;
    }


    public boolean isRightClickInAirRequired() {
        return rightClickInAirRequired;
    }

    public void setRightClickInAirRequired(boolean rightClickInAirRequired) {
        this.rightClickInAirRequired = rightClickInAirRequired;
    }

    public boolean isAllowEnderChestStorage() {
        return allowEnderChestStorage;
    }

    public void setAllowEnderChestStorage(boolean allowEnderChestStorage) {
        this.allowEnderChestStorage = allowEnderChestStorage;
    }

    public boolean isAllowEnderChestDrop() {
        return allowEnderChestDrop;
    }

    public void setAllowEnderChestDrop(boolean allowEnderChestDrop) {
        this.allowEnderChestDrop = allowEnderChestDrop;
    }

    public boolean isAllowEnderChestDespawn() {
        return allowEnderChestDespawn;
    }

    public void setAllowEnderChestDespawn(boolean allowEnderChestDespawn) {
        this.allowEnderChestDespawn = allowEnderChestDespawn;
    }

    public String getAttemptDropMessage() {
        return attemptDropMessage;
    }

    public void setAttemptDropMessage(String attemptDropMessage) {
        this.attemptDropMessage = attemptDropMessage;
    }

    public String getAttemptStorageMessage() {
        return attemptStorageMessage;
    }

    public void setAttemptStorageMessage(String attemptStorageMessage) {
        this.attemptStorageMessage = attemptStorageMessage;
    }

    public boolean isDroppedItemInvulnerable() {
        return droppedItemInvulnerable;
    }

    public void setDroppedItemInvulnerable(boolean droppedItemInvulnerable) {
        this.droppedItemInvulnerable = droppedItemInvulnerable;
    }
}
