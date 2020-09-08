package me.mattstudios.mfcmd;

import me.mattstudios.mfcmd.base.CommandBase;
import me.mattstudios.mfcmd.base.annotations.Command;
import me.mattstudios.mfcmd.base.annotations.Completion;
import me.mattstudios.mfcmd.base.annotations.Default;
import me.mattstudios.mfcmd.base.annotations.SubCommand;
import me.mattstudios.mfcmd.bukkit.annotations.Permission;
import org.bukkit.entity.Player;

@Command("command")
public final class Cmd extends CommandBase {

    @Default
    @Completion({"#players", "#test"})
    public void test2(final Player player, final Player player2, final String text) {

    }

    @SubCommand("test")
    @Permission("fuck.you")
    public void test(final Player player, final Integer number) {

    }

}
