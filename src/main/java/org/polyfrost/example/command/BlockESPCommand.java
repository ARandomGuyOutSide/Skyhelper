package org.polyfrost.example.command;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import org.polyfrost.example.mining.BlockESP;

import java.util.ArrayList;
import java.util.List;

public class BlockESPCommand extends CommandBase {

    private List<BlockPos> blockPosList = new ArrayList<>();

    @Override
    public String getCommandName() {
        return "blockesp";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if(args.length > 2)
        {
            int x, y, z;
            try
            {
                x = Integer.parseInt(args[0]);
                y = Integer.parseInt(args[1]);
                z = Integer.parseInt(args[2]);

                BlockPos blockPos = new BlockPos(x, y, z);

                blockPosList.add(blockPos);

                BlockESP.setBlockESPOfBlockWithList(blockPosList);

                Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("Set Block Esp of Block " + blockPos));
            } catch (NumberFormatException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
