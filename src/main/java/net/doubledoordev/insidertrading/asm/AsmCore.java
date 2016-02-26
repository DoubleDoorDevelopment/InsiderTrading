package net.doubledoordev.insidertrading.asm;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

/**
 * @author Dries007
 */
public class AsmCore implements IFMLLoadingPlugin
{
    @Override
    public String[] getASMTransformerClass()
    {
        return new String[]{ClassTransformer.class.getName()};
    }

    @Override
    public String getModContainerClass()
    {
        return null;
    }

    @Override
    public String getSetupClass()
    {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data)
    {

    }

    @Override
    public String getAccessTransformerClass()
    {
        return null;
    }
}
