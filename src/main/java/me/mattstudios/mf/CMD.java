package me.mattstudios.mf;

import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.CompletionFor;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.entity.Player;

@Command("cmd")
public final class CMD extends CommandBase {

    @SubCommand("test")
    @Permission({"test.perm.one", "test.perm.two"})
    public void command(final Player player, final Player inputPlayer, final String string) {
        System.out.println("uuhh");
    }

    @CompletionFor("test")
    public void commandCompletion(final String string) {

    }

}
