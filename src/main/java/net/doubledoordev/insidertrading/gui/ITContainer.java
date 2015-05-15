package net.doubledoordev.insidertrading.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * @author Dries007
 */
public class ITContainer extends Container
{
    public final InventoryBasic inventoryBasic = new InventoryBasic(null, false, 3);
    public final Slot[] slots = new Slot[3];
    public int profession;

    public ITContainer(int profession, InventoryPlayer inventory)
    {
        this.profession = profession;

        slots[0] = addSlotToContainer(new Slot(this.inventoryBasic, 0, 36, 53));
        slots[1] = addSlotToContainer(new Slot(this.inventoryBasic, 1, 62, 53));
        slots[2] = addSlotToContainer(new Slot(this.inventoryBasic, 2, 120, 53));

        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 32 + 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 32 + 142));
        }
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    public void onCraftMatrixChanged(IInventory p_75130_1_)
    {
        super.onCraftMatrixChanged(p_75130_1_);
    }

    public boolean canInteractWith(EntityPlayer p_75145_1_)
    {
        return true;
    }

    @Override
    protected void retrySlotClick(int p_75133_1_, int p_75133_2_, boolean p_75133_3_, EntityPlayer p_75133_4_)
    {

    }

    @Override
    public ItemStack slotClick(int i, int mousebtn, int modifier, EntityPlayer player)
    {
        ItemStack stack = null;
        if (i >= 0 && i <= 2)// Fake slots
        {
            if (mousebtn == 2)
            {
                getSlot(i).putStack(null);
            }
            else if (mousebtn == 0)
            {
                InventoryPlayer playerInv = player.inventory;
                getSlot(i).onSlotChanged();
                ItemStack stackSlot = getSlot(i).getStack();
                ItemStack stackHeld = playerInv.getItemStack();

                if (stackSlot != null) stack = stackSlot.copy();

                if (stackHeld != null)
                {
                    ItemStack newStack = stackHeld.copy();
                    newStack.stackSize = 1;
                    getSlot(i).putStack(newStack);
                }
                else getSlot(i).putStack(null);
            }
            else if (mousebtn == 1)
            {
                InventoryPlayer playerInv = player.inventory;
                getSlot(i).onSlotChanged();
                ItemStack stackSlot = getSlot(i).getStack();
                ItemStack stackHeld = playerInv.getItemStack();

                if (stackSlot != null) stack = stackSlot.copy();

                if (stackHeld != null)
                {
                    stackHeld = stackHeld.copy();
                    stackHeld.stackSize = 1;
                    getSlot(i).putStack(stackHeld);
                }
                else
                {
                    if (stackSlot != null)
                    {
                        stackSlot.stackSize--;
                        if (stackSlot.stackSize == 0) getSlot(i).putStack(null);
                    }
                }
            }
        }
        else
        {
            stack = super.slotClick(i, mousebtn, modifier, player);
        }
        return stack;
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
    public ItemStack transferStackInSlot(EntityPlayer player, int slots)
    {
        return null;
    }

    public boolean func_94530_a(ItemStack p_94530_1_, Slot p_94530_2_)
    {
        return super.func_94530_a(p_94530_1_, p_94530_2_);
    }
}
