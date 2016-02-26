package net.doubledoordev.insidertrading.util;

import net.minecraft.village.MerchantRecipe;

import java.util.Random;

/**
 * @author Dries007
 */
public class TradeWrapper implements IMatcher<MerchantRecipe>
{
    public ItemStackWrapper buying1;
    public ItemStackWrapper buying2;
    public ItemStackWrapper selling;
    public float chance = 1.0f;

    public MerchantRecipe unwrap(Random rand)
    {
        return new MerchantRecipe(buying1.unwrapStack(rand), buying2 == null ? null : buying2.unwrapStack(rand), selling.unwrapStack(rand));
    }

    @Override
    public boolean matches(MerchantRecipe obj)
    {
        return buying1.matches(obj.getItemToBuy()) && ((buying2 == null && !obj.hasSecondItemToBuy()) || buying2.matches(obj.getSecondItemToBuy())) && (selling.matches(obj.getItemToSell()));
    }

    public boolean isValid()
    {
        return buying1 != null && selling != null;
    }
}
