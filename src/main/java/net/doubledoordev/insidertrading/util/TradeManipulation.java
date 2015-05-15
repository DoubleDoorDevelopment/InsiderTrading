package net.doubledoordev.insidertrading.util;

import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * @author Dries007
 */
public class TradeManipulation
{
    public boolean clearFirst = false;
    public List<TradeWrapper> remove = new ArrayList<>();
    public List<TradeWrapper> add = new ArrayList<>();

    public void apply(MerchantRecipeList list, Random rand)
    {
        // remove
        if (clearFirst) list.clear();
        else
        {
            //noinspection unchecked
            Iterator<MerchantRecipe> i = list.iterator();
            while (i.hasNext())
            {
                MerchantRecipe recipe = i.next();
                for (TradeWrapper filter : remove)
                {
                    if (filter.matches(recipe))
                    {
                        i.remove();
                        break;
                    }
                }
            }
        }
        // add
        for (TradeWrapper wrapper : add)
        {
            if (rand.nextFloat() < wrapper.chance) list.addToListWithCheck(wrapper.unwrap(rand));
        }
    }
}
