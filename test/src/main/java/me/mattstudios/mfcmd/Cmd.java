package me.mattstudios.mfcmd;

import me.mattstudios.mfcmd.base.CommandBase;
import me.mattstudios.mfcmd.base.annotations.Command;
import me.mattstudios.mfcmd.base.annotations.SubCommand;
import me.mattstudios.mfcmd.bukkit.annotations.Permission;
import org.bukkit.entity.Player;

@Command("command")
public final class Cmd extends CommandBase {

    /*@Default
    public void test2(final Player player) {

    }*/

    @SubCommand("test")
    @Permission("fuck.you")
    public void test(final Player player) {

    }

}
