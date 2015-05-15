package net.doubledoordev.insidertrading.asm;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.ModMetadata;
import net.doubledoordev.insidertrading.util.Constants;

/**
 * @author Dries007
 */
public class FakeModContainer extends DummyModContainer
{
    public FakeModContainer()
    {
        super(new ModMetadata());

        ModMetadata modMetadata = getMetadata();
        modMetadata.parent = Constants.MODID;
        modMetadata.modId = Constants.MODID_ASM;
    }
}
