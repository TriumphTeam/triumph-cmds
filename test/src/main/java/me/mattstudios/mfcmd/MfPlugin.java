package me.mattstudios.mfcmd;

import me.mattstudios.mfcmd.bukkit.BukkitCommandManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;

/**
 * @author Matt
 */
public final class MfPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        final BukkitCommandManager commandManager = new BukkitCommandManager(this);

        commandManager.registerCompletion("#test", input -> Collections.singletonList("hey"));

        commandManager.register(new Cmd());
    }

}