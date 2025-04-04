package org.polyfrost.example.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import org.polyfrost.example.MacroController;
import org.polyfrost.example.MainController;

public class MacroStatusCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "macro";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/macro - Toggles the macro";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 0)
        {
            MacroController.setState("warpToForge");
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
