package net.doubledoordev.insidertrading;

import com.google.common.reflect.TypeToken;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import net.doubledoordev.d3core.util.ID3Mod;
import net.doubledoordev.insidertrading.gui.ITGuiHandler;
import net.doubledoordev.insidertrading.util.Constants;
import net.doubledoordev.insidertrading.util.ITCommand;
import net.doubledoordev.insidertrading.util.TradeManipulation;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static net.doubledoordev.insidertrading.util.Constants.MODID;

/**
 * @author Dries007
 */
@Mod(modid = MODID)
public class InsiderTrading implements ID3Mod
{
    private Configuration configuration;
    private File jsonFile;
    public final Map<String, TradeManipulation> tradeManipulationMap = new HashMap<>();

    @Mod.Instance(MODID)
    public static InsiderTrading instance;

    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event) throws IOException
    {
        configuration = new Configuration(event.getSuggestedConfigurationFile());
        jsonFile = new File(event.getModConfigurationDirectory(), MODID.concat(".json"));
        if (jsonFile.exists())
        {
            Type type = new TypeToken<Map<String, TradeManipulation>>()
            {
            }.getType();
            tradeManipulationMap.putAll(Constants.GSON.<Map<String, TradeManipulation>>fromJson(FileUtils.readFileToString(jsonFile, "utf-8"), type));
        }

        NetworkRegistry.INSTANCE.registerGuiHandler(this, new ITGuiHandler());

        syncConfig();
    }

    @Mod.EventHandler
    public void init(FMLPostInitializationEvent event)
    {
        for (Integer i : VillagerRegistry.getRegisteredVillagers())
        {
            if (i < 0)
            {
                RuntimeException e = new RuntimeException("Some mod decided to register a villager with a negative ID. InsiderTrading won't be able to work in this environment.");
                e.setStackTrace(new StackTraceElement[0]);
                throw e;
            }
        }
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new ITCommand());
    }

    @Override
    public void syncConfig()
    {
        if (configuration.hasChanged()) configuration.save();
    }

    @Override
    public void addConfigElements(List<IConfigElement> list)
    {
        list.add(new ConfigElement(configuration.getCategory(MODID.toLowerCase())));
    }

    public void handle(MerchantRecipeList list, int profession, Random rand)
    {
        TradeManipulation manipulation = tradeManipulationMap.get("-1");
        if (manipulation != null) manipulation.apply(list, rand);
        manipulation = tradeManipulationMap.get(String.valueOf(profession));
        if (manipulation != null) manipulation.apply(list, rand);
    }

    public void saveDB()
    {
        try
        {
            FileUtils.write(jsonFile, Constants.GSON.toJson(tradeManipulationMap), "utf-8");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
