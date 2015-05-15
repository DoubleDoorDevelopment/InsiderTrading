package net.doubledoordev.insidertrading.util;

import com.google.common.base.Strings;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Random;

/**
 * @author Dries007
 */
public class ItemStackWrapper implements IMatcher<ItemStack>
{
    public String item;
    public int meta;
    public int maxStacksize;
    public int minStacksize;
    public boolean anyTag;
    public String tag;
    public transient NBTBase actualTag;

    {
        if (!Strings.isNullOrEmpty(tag))
        {
            try
            {
                actualTag = JsonToNBT.func_150315_a(tag);
            }
            catch (NBTException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public Item unwrapItem()
    {
        int i = item.indexOf(':');
        String modid = i == -1 ? "minecraft" : item.substring(0, i);
        String itemname = i == -1 ? item : item.substring(i + 1);
        return GameRegistry.findItem(modid, itemname);
    }

    public ItemStack unwrapStack(Random rand)
    {
        Item item = unwrapItem();
        if (item == null) return null;
        ItemStack stack = new ItemStack(unwrapItem(), MathHelper.getRandomIntegerInRange(rand, minStacksize, maxStacksize), meta);
        if (actualTag != null) stack.stackTagCompound = (NBTTagCompound) actualTag.copy();
        return stack;
    }

    @Override
    public boolean matches(ItemStack obj)
    {
        return item.equals("*") ||
                (obj != null && unwrapItem() == obj.getItem() && (meta == OreDictionary.WILDCARD_VALUE || meta == obj.getItemDamage()) && obj.stackSize >= minStacksize && obj.stackSize <= maxStacksize &&
                        (anyTag || (actualTag == null && obj.stackTagCompound == null) || (actualTag != null && obj.stackTagCompound != null && actualTag.equals(obj.stackTagCompound))));
    }
}
