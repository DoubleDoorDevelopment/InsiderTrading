package net.doubledoordev.insidertrading.gui;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiCheckBox;
import cpw.mods.fml.client.config.GuiSlider;
import cpw.mods.fml.client.config.HoverChecker;
import cpw.mods.fml.common.registry.GameRegistry;
import net.doubledoordev.insidertrading.InsiderTrading;
import net.doubledoordev.insidertrading.util.ItemStackWrapper;
import net.doubledoordev.insidertrading.util.TradeManipulation;
import net.doubledoordev.insidertrading.util.TradeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import org.lwjgl.opengl.GL11;

/**
 * @author Dries007
 */
public class ITGui extends GuiContainer
{
    private static final ResourceLocation VILLAGER_RESOURCE_LOCATION = new ResourceLocation("insidertrading:gui/trade.png");
    private static final String NAME = "InsiderTrading";
    private static final String MAX = "Max:";
    private static final String MIN = "Min:";

    private final boolean add;
    private final int profession;
    private final String helpText;
    private final GuiTextField[] fields = new GuiTextField[6];
    private final HoverChecker[] fieldsHoverChecker = new HoverChecker[6];
    private final GuiCheckBox[] checkBoxes = new GuiCheckBox[9];
    private final HoverChecker[] checkBoxHoverChecker = new HoverChecker[9];
    private final GuiButton[] buttons = new GuiButton[1];
    private final GuiSlider[] sliders = new GuiSlider[1];

    public ITContainer container;

    public ITGui(ITContainer container)
    {
        super(container);
        this.container = container;
        this.ySize = 198;

        if (container.profession == Integer.MIN_VALUE)
        {
            add = false;
            profession = -1;
            this.helpText = "Remove from all professions";
        }
        else if (container.profession == Integer.MAX_VALUE)
        {
            add = true;
            profession = -1;
            this.helpText = "Add to all professions";
        }
        else if (container.profession >= 0)
        {
            add = true;
            profession = container.profession;
            this.helpText = "Adding to " + container.profession;
        }
        else
        {
            add = false;
            profession = container.profession - Integer.MIN_VALUE;
            this.helpText = "Removing from " + profession;
        }
    }

    @SuppressWarnings({"unchecked", "UnusedAssignment"})
    @Override
    public void initGui()
    {
        super.initGui();

        int oX = (this.width - this.xSize) / 2;
        int oY = (this.height - this.ySize) / 2;

        fields[0] = new GuiTextField(this.mc.fontRenderer, oX + 34, oY + 24, 20, 12);
        fields[1] = new GuiTextField(this.mc.fontRenderer, oX + 34, oY + 38, 20, 12);

        fields[2] = new GuiTextField(this.mc.fontRenderer, oX + 60, oY + 24, 20, 12);
        fields[3] = new GuiTextField(this.mc.fontRenderer, oX + 60, oY + 38, 20, 12);

        fields[4] = new GuiTextField(this.mc.fontRenderer, oX + 118, oY + 24, 20, 12);
        fields[5] = new GuiTextField(this.mc.fontRenderer, oX + 118, oY + 38, 20, 12);

        int id = 0;
        this.buttonList.add(buttons[0] = new GuiButtonExt(id++, oX + 150, oY + 90, 18, 18, "Ok"));
        //public GuiSlider(int id, int xPos, int yPos, int width, int height, String prefix, String suf, double minVal, double maxVal, double currentVal, boolean showDec, boolean drawStr, ISlider par)
        this.buttonList.add(sliders[0] = new GuiSlider(id++, oX, oY - 20, this.xSize, 20, "Chance ", "", 0.0d, 1.0d, 1.0d, true, true, null));
        sliders[0].precision = 2;
        sliders[0].visible = add;

        this.buttonList.add(checkBoxes[0] = new GuiCheckBox(id++, oX + 38, oY + 75, "", false));
        this.buttonList.add(checkBoxes[1] = new GuiCheckBox(id++, oX + 38, oY + 88, "", false));

        this.buttonList.add(checkBoxes[2] = new GuiCheckBox(id++, oX + 64, oY + 75, "", false));
        this.buttonList.add(checkBoxes[3] = new GuiCheckBox(id++, oX + 64, oY + 88, "", false));

        this.buttonList.add(checkBoxes[4] = new GuiCheckBox(id++, oX + 122, oY + 75, "", false));
        this.buttonList.add(checkBoxes[5] = new GuiCheckBox(id++, oX + 122, oY + 88, "", false));

        this.buttonList.add(checkBoxes[6] = new GuiCheckBox(id++, oX + 46, oY + 48, "", false));
        this.buttonList.add(checkBoxes[7] = new GuiCheckBox(id++, oX + 72, oY + 48, "", false));
        this.buttonList.add(checkBoxes[8] = new GuiCheckBox(id++, oX + 132, oY + 48, "", false));

        for (int i = 0; i < checkBoxes.length; i++)
        {
            checkBoxHoverChecker[i] = new HoverChecker(checkBoxes[i], 800);
        }

        for (int i = 0; i < fields.length; i++)
        {
            GuiTextField f = fields[i];
            f.setText("1");
            f.setMaxStringLength(2);
            f.setVisible(checkBoxes[i].visible = container.slots[i / 2].getHasStack());
            fieldsHoverChecker[i] = new HoverChecker(f.yPosition, f.yPosition + f.height, f.xPosition, f.xPosition + f.width, 800);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.id == buttons[0].id)
        {
            try
            {
                TradeWrapper trade = new TradeWrapper();

                trade.chance = (float) sliders[0].getValue();
                trade.buying1 = getItemStackWrapperFromSlot(0);
                trade.buying2 = getItemStackWrapperFromSlot(1);
                trade.selling = getItemStackWrapperFromSlot(2);

                if (trade.isValid())
                {
                    TradeManipulation manipulation = InsiderTrading.instance.tradeManipulationMap.get(Integer.toString(profession));
                    if (manipulation == null) InsiderTrading.instance.tradeManipulationMap.put(Integer.toString(profession), manipulation = new TradeManipulation());

                    (add ? manipulation.add : manipulation.remove).add(trade);

                    InsiderTrading.instance.saveDB();
                }
                else
                {
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Invalid recipe."));
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            this.mc.thePlayer.closeScreen();
        }
        super.actionPerformed(button);
    }

    private ItemStackWrapper getItemStackWrapperFromSlot(int i)
    {
        if (container.slots[i].getHasStack())
        {
            ItemStackWrapper wrapper = new ItemStackWrapper();

            wrapper.maxStacksize = Integer.parseInt(fields[i * 2].getText());
            wrapper.minStacksize = Integer.parseInt(fields[1 + i * 2].getText());

            ItemStack stack = container.slots[i].getStack();
            wrapper.item = GameRegistry.findUniqueIdentifierFor(stack.getItem()).toString();
            wrapper.meta = checkBoxes[i * 2].isChecked() ? OreDictionary.WILDCARD_VALUE : stack.getItemDamage();
            wrapper.anyTag = checkBoxes[1 + i * 2].isChecked();
            if (!wrapper.anyTag)
            {
                wrapper.actualTag = stack.stackTagCompound;
                if (wrapper.actualTag != null) wrapper.tag = wrapper.actualTag.toString();
            }
            return wrapper;
        }
        else if (checkBoxes[i + 6].isChecked())
        {
            ItemStackWrapper wrapper = new ItemStackWrapper();
            wrapper.item = "*";
            return wrapper;
        }
        return null;
    }

    @Override
    protected void keyTyped(char p_73869_1_, int p_73869_2_)
    {
        //  Numbers or special chars only
        if ((p_73869_1_ >= '0' && p_73869_1_ <= '9') || p_73869_1_ == '\b' || p_73869_1_ == 0)
        {
            for (GuiTextField field : fields)
            {
                if (field.textboxKeyTyped(p_73869_1_, p_73869_2_)) return;
            }
        }
        super.keyTyped(p_73869_1_, p_73869_2_);
    }

    protected void mouseClicked(int p_73864_1_, int p_73864_2_, int p_73864_3_)
    {
        super.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);

        for (GuiTextField field : fields)
        {
            field.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
        }
    }

    @Override
    protected void handleMouseClick(Slot p_146984_1_, int p_146984_2_, int p_146984_3_, int p_146984_4_)
    {
        super.handleMouseClick(p_146984_1_, p_146984_2_, p_146984_3_, p_146984_4_);
        for (int i = 0; i < fields.length; i++)
        {
            fields[i].setVisible(checkBoxes[i].visible = container.slots[i / 2].getHasStack());
        }
        for (int i = 0; i < 3; i++)
        {
            checkBoxes[6 + i].visible = !container.slots[i].getHasStack();
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_)
    {
        this.fontRendererObj.drawString(NAME, this.xSize / 2 - this.fontRendererObj.getStringWidth(NAME) / 2, 6, 4210752);

        this.fontRendererObj.drawString(helpText, this.xSize / 2 - this.fontRendererObj.getStringWidth(helpText) / 2, 14, 4210752);
        this.fontRendererObj.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);

        this.fontRendererObj.drawString("Meta", 6, 78, 4210752);
        this.fontRendererObj.drawString("NBT", 6, 90, 4210752);

        this.fontRendererObj.drawString(MAX, 30 - this.fontRendererObj.getStringWidth(MAX), 25, 4210752);
        this.fontRendererObj.drawString(MIN, 30 - this.fontRendererObj.getStringWidth(MIN), 40, 4210752);
    }

    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(VILLAGER_RESOURCE_LOCATION);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

        this.drawTexturedModalRect(k + 35, l + 52, 176, 0, 18, 18); // Slot 0
        this.drawTexturedModalRect(k + 61, l + 52, 176, 0, 18, 18); // Slot 1
        this.drawTexturedModalRect(k + 85, l + 52, 176, 44, 22, 15); // Arrow
        this.drawTexturedModalRect(k + 115, l + 48, 176, 18, 26, 26); // Slot 2

        for (GuiTextField guiTextField : fields) guiTextField.drawTextBox();
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);

        for (HoverChecker hoverChecker : checkBoxHoverChecker)
        {
            if (hoverChecker.checkHover(mouseX, mouseY))
            {
                this.func_146283_a(this.mc.fontRenderer.listFormattedStringToWidth("Match any value", 300), mouseX, mouseY);
            }
        }

        for (HoverChecker hoverChecker : fieldsHoverChecker)
        {
            if (hoverChecker.checkHover(mouseX, mouseY))
            {
                this.func_146283_a(this.mc.fontRenderer.listFormattedStringToWidth("Stacksize", 300), mouseX, mouseY);
            }
        }
    }
}
