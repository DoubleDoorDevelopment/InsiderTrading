package net.doubledoordev.insidertrading.asm;

import net.doubledoordev.insidertrading.InsiderTrading;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.village.MerchantRecipeList;

import java.util.Random;

/**
 * Called by ASM
 *
 * @author Dries007
 */
@SuppressWarnings("unused")
public class Hooks
{
    private Hooks()
    {
    }

    public static void manipulate(MerchantRecipeList merchantrecipelist, EntityVillager entityVillager, int profession, Random rand)
    {
        InsiderTrading.instance.handle(merchantrecipelist, profession, rand);
    }
}
