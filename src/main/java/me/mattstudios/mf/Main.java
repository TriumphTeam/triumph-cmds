package me.mattstudios.mf;

import me.mattstudios.mf.base.CommandManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        CommandManager commandManager = new CommandManager(this);
        commandManager.register(new CMD());
        commandManager.hideTabComplete(true);
    }
}
