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
import net.doubledoordev.insidertrading.asm.ClassTransformer;
import net.doubledoordev.insidertrading.client.DebugScreen;
import net.doubledoordev.insidertrading.gui.ITGuiHandler;
import net.doubledoordev.insidertrading.util.Constants;
import net.doubledoordev.insidertrading.util.ITCommand;
import net.doubledoordev.insidertrading.util.TradeManipulation;
import net.doubledoordev.insidertrading.util.TradeWrapper;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
    public Logger logger;

    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event) throws IOException
    {
        logger = event.getModLog();

        if (ClassTransformer.done < ClassTransformer.DONE) throw new RuntimeException("ASM handler didn't work...");

        configuration = new Configuration(event.getSuggestedConfigurationFile());
        jsonFile = new File(event.getModConfigurationDirectory(), MODID.concat(".json"));
        if (jsonFile.exists())
        {
            List<TradeWrapper> errors = new ArrayList<>();
            tradeManipulationMap.putAll(Constants.GSON.<Map<String, TradeManipulation>>fromJson(FileUtils.readFileToString(jsonFile, "utf-8"), new TypeToken<Map<String, TradeManipulation>>() {}.getType()));
            for (TradeManipulation manipulation : tradeManipulationMap.values())
            {
                for (TradeWrapper wrapper : manipulation.add)
                {
                    if (!wrapper.isValid()) errors.add(wrapper);
                }
            }

            if (!errors.isEmpty())
            {
                event.getModLog().fatal(Constants.GSON.toJson(errors));

                RuntimeException e = new RuntimeException("You have some misconfiguration in your recipes. See above in the logfile.");
                e.setStackTrace(new StackTraceElement[0]);
                throw e;
            }
        }
        else logger.info("No config file, oh well.");

        NetworkRegistry.INSTANCE.registerGuiHandler(this, new ITGuiHandler());

        syncConfig();

        if (event.getSide().isClient()) MinecraftForge.EVENT_BUS.register(new DebugScreen());
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
