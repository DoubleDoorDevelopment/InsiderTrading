package net.doubledoordev.insidertrading.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Dries007
 */
public class Constants
{
    public static final String MODID = "InsiderTrading";
    public static final String MODID_ASM = "InsiderTrading-ASM";
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
}
