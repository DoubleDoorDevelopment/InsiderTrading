package net.doubledoordev.insidertrading.util;

import cpw.mods.fml.common.registry.VillagerRegistry;
import net.doubledoordev.insidertrading.InsiderTrading;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Dries007
 */
public class ITCommand extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "insidertrading";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return "/insidertrading <add|remove> [professionID, none for all types]";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender p_71519_1_)
    {
        return true;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length < 1) throw new WrongUsageException(getCommandUsage(sender));
        if (!(sender instanceof EntityPlayer)) throw new WrongUsageException("You can't use this command as console.");

        boolean add;
        if (args[0].equalsIgnoreCase("add")) add = true;
        else if (args[0].equalsIgnoreCase("remove")) add = false;
        else throw new WrongUsageException(getCommandUsage(sender));

        int profession = add ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        if (args.length > 1)
        {
            profession = parseIntWithMin(sender, args[1], 0);
            if (!add) profession = profession + Integer.MIN_VALUE;
        }
        ((EntityPlayer) sender).openGui(InsiderTrading.instance, profession, sender.getEntityWorld(), 0, 0, 0);
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, "Add", "Remove");
        }
        else if (args.length == 2)
        {
            Collection<Integer> forgeIds = VillagerRegistry.getRegisteredVillagers();
            ArrayList<String> ids = new ArrayList<>(forgeIds.size() + 6);
            ids.add("0");
            ids.add("1");
            ids.add("2");
            ids.add("3");
            ids.add("4");
            for (Integer id : forgeIds)
            {
                ids.add(id.toString());
            }
            return getListOfStringsMatchingLastWord(args, ids.toArray(new String[ids.size()]));
        }
        return null;
    }
}
