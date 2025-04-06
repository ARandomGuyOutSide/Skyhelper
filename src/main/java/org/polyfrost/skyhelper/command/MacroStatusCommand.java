package org.polyfrost.skyhelper.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import org.polyfrost.skyhelper.MacroController;

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
        if(args.length == 1)
        {
            if(args[0].equals("com"))
                MacroController.setState("warpToForge");
            else if(args[0].equals("mine"))
                MacroController.setState("scanBlocks");
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
