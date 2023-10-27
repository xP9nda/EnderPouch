package xp9nda.enderpouch;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.SimpleCommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import xp9nda.enderpouch.handlers.ConfigHandler;
import xp9nda.enderpouch.handlers.EnderPouchItem;
import xp9nda.enderpouch.handlers.InfoCommand;
import xp9nda.enderpouch.handlers.OpenCommand;
import xp9nda.enderpouch.utils.InventoryUtils;

public final class EnderPouch extends JavaPlugin {

    // variables
    private ConfigHandler configHandler;
    private InventoryUtils inventoryUtils;
    private EnderPouchItem enderPouch;
    private InfoCommand infoCommand;

    @Override
    public void onEnable() {
        // Plugin startup logic

        // config
        saveDefaultConfig();

        // Handlers and events
        var pluginManager = getServer().getPluginManager();

        configHandler = new ConfigHandler(this);
        pluginManager.registerEvents(configHandler, this);

        inventoryUtils = new InventoryUtils(this);

        enderPouch = new EnderPouchItem(this);
        pluginManager.registerEvents(enderPouch, this);

        infoCommand = new InfoCommand(this);

        OpenCommand openEnderCommand = new OpenCommand(this);

        // Commands
        try {
            PaperCommandManager<CommandSender> commandManager = PaperCommandManager.createNative(
                    this, CommandExecutionCoordinator.simpleCoordinator()
            );

            AnnotationParser<CommandSender> annotationParser = new AnnotationParser<>(
                    commandManager, CommandSender.class, params -> SimpleCommandMeta.empty()
            );

            annotationParser.parse(configHandler);
            annotationParser.parse(enderPouch);
            annotationParser.parse(infoCommand);
            annotationParser.parse(openEnderCommand);


        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public InventoryUtils getInventoryUtils() {
        return inventoryUtils;
    }
}
