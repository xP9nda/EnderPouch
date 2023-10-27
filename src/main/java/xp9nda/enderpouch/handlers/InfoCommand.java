package xp9nda.enderpouch.handlers;

import cloud.commandframework.annotations.CommandMethod;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xp9nda.enderpouch.EnderPouch;

public class InfoCommand {

    // variables
    private final Plugin plugin;
    private final EnderPouch pluginClass;
    private final MiniMessage miniMsg = MiniMessage.miniMessage();

    // constructor
    public InfoCommand(Plugin loader) {
        plugin = loader;
        pluginClass = (EnderPouch) plugin;
    }

    // info command
    @CommandMethod("ender|enderpouch info")
    public void infoCommand(Player commandSender) {
        commandSender.sendMessage(miniMsg.deserialize(
                "<#6e5d83><b>EnderPouch Info:</b>" +
                    "\n" +
                    "\n<#6e5d83><b>Author:</b> <#9982b6><u><hover:show_text:'<#9982b6>Open GitHub'><click:open_url:'https://github.com/xP9nda'>xP9nda</click></hover></u>" +
                    "\n<#6e5d83><b>Version:</b> <#9982b6>This server is running version " + pluginClass.getPluginMeta().getVersion() +
                    "\n<#6e5d83><b>View Source:</b> <#9982b6><click:open_url:'https://github.com/xP9nda/EnderPouch'><u><hover:show_text:'<#9982b6>Open GitHub'>click here</hover></u></click>" +
                    "\n" +
                    "\n<#9982b6>EnderPouch is an all in one ender chest management and access plugin."
        ));
    }
}
