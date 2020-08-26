package me.mattstudios.mfcmd;

import me.mattstudios.mf.base.CommandManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Matt
 */
public final class MfPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        final CommandManager commandManager = new CommandManager(this);

    }
}