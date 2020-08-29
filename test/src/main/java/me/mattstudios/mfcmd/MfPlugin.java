package me.mattstudios.mfcmd;

import me.mattstudios.mfcmd.bukkit.BukkitCommandManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Matt
 */
public final class MfPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        final BukkitCommandManager commandManager = new BukkitCommandManager(this);
        commandManager.register(new Cmd());
    }

}