package net.doubledoordev.insidertrading.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

/**
 * @author Dries007
 */
public class DebugScreen
{
    @SubscribeEvent
    public void event(RenderGameOverlayEvent.Text event)
    {
        if (Minecraft.getMinecraft().gameSettings.showDebugInfo)
        {
            MovingObjectPosition movingObjectPosition = Minecraft.getMinecraft().objectMouseOver;
            if (movingObjectPosition != null && movingObjectPosition.entityHit != null && movingObjectPosition.entityHit instanceof EntityVillager)
            {
                event.right.add(EnumChatFormatting.GREEN + "Villager profession: " + ((EntityVillager) movingObjectPosition.entityHit).getProfession());
            }
        }
    }
}
