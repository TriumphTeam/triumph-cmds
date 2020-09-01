package me.mattstudios.mfcmd;

import me.mattstudios.mfcmd.base.CommandBase;
import me.mattstudios.mfcmd.base.annotations.Command;
import me.mattstudios.mfcmd.base.annotations.Default;
import me.mattstudios.mfcmd.base.annotations.SubCommand;

@Command("command")
public final class Cmd extends CommandBase {

    @Default
    public void test2() {

    }

    @SubCommand("test")
    public void test() {

    }

}
