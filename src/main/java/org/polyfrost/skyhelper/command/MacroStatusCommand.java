package org.polyfrost.skyhelper.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import org.polyfrost.skyhelper.MacroController;
import org.polyfrost.skyhelper.mining.BlockESP;
import org.polyfrost.skyhelper.mining.ScanBlocks;
import org.polyfrost.skyhelper.player.Player;
import org.polyfrost.skyhelper.util.Chatter;

public class MacroStatusCommand extends CommandBase {

    private enum MiningState
    {
        ENABLED,
        DISABLED
    }

    MiningState miningState = MiningState.DISABLED;

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
        if(args.length >= 1)
        {
            if(args[0].equals("com"))
                MacroController.setState(MacroController.SetupState.WARP_TO_FORGE);
            else if(args[0].equals("mine"))
            {
                if(miningState.equals(MiningState.DISABLED))
                {
                    MacroController.setState(MacroController.MiningState.SEARCH_FOR_BLOCK);
                    Chatter.sendChatMessageToUser("mining macro has been §aenabled");
                    miningState = MiningState.ENABLED;
                }
                else
                {
                    MacroController.setState(MacroController.MiningState.NONE);
                    Chatter.sendChatMessageToUser("mining macro has been §cdisabled");
                    BlockESP.setSingleBlockESP(null);
                    miningState = MiningState.DISABLED;
                }
            }
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
