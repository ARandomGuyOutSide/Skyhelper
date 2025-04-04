package org.polyfrost.example.command;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public class HomeCommand extends CommandBase {

    @Override
    public String getCommandName() {
        // Wenn mal /home eingibt wird der processCommand ausgeführt
        return "home";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        // Kann man haben muss aber nicht sein
        return "";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        // Lässt den Spieler /warp island eingeben, sodass er dann zur island gewarpt wird
        Minecraft.getMinecraft().thePlayer.sendChatMessage("/warp island");
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
