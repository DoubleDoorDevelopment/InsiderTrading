package net.doubledoordev.insidertrading.asm;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

/**
 * @author Dries007
 */
public class AsmCore extends DummyModContainer implements IFMLLoadingPlugin
{
    @Override
    public String[] getASMTransformerClass()
    {
        return new String[]{"net.doubledoordev.insidertrading.asm.ClassTransformer"};
    }

    @Override
    public String getModContainerClass()
    {
        return "net.doubledoordev.insidertrading.asm.FakeModContainer";
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
