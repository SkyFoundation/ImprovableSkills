package com.zeitheron.improvableskills.client.gui.abil.crafter;

import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.data.PlayerDataManager;
import com.zeitheron.improvableskills.init.AbilitiesIS;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ContainerCrafter extends Container
{
	/** The crafting matrix inventory (3x3). */
	public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
	public InventoryCraftResult craftResult = new InventoryCraftResult();
	private final EntityPlayer player;
	
	public ContainerCrafter(InventoryPlayer playerInventory)
	{
		this.player = playerInventory.player;
		this.addSlotToContainer(new SlotCrafting(playerInventory.player, this.craftMatrix, this.craftResult, 0, 124, 35));
		
		for(int i = 0; i < 3; ++i)
			for(int j = 0; j < 3; ++j)
				this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 3, 30 + j * 18, 17 + i * 18));
		for(int k = 0; k < 3; ++k)
			for(int i1 = 0; i1 < 9; ++i1)
				this.addSlotToContainer(new Slot(playerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
		for(int l = 0; l < 9; ++l)
			this.addSlotToContainer(new Slot(playerInventory, l, 8 + l * 18, 142));
	}
	
	/**
	 * Callback for when the crafting matrix is changed.
	 */
	@Override
	public void onCraftMatrixChanged(IInventory inventoryIn)
	{
		this.slotChangedCraftingGrid(this.player.world, this.player, this.craftMatrix, this.craftResult);
	}
	
	/**
	 * Called when the container is closed.
	 */
	@Override
	public void onContainerClosed(EntityPlayer playerIn)
	{
		super.onContainerClosed(playerIn);
		
		if(!this.player.world.isRemote)
			this.clearContainer(playerIn, this.player.world, this.craftMatrix);
	}
	
	/**
	 * Determines whether supplied player can use this container
	 */
	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		PlayerSkillData data = PlayerDataManager.getDataFor(playerIn);
		if(data == null)
			return false;
		return data.abilities.contains(AbilitiesIS.CRAFTER.getRegistryName().toString());
	}
	
	/**
	 * Handle when the stack in slot {@code index} is shift-clicked. Normally
	 * this moves the stack between the player inventory and the other
	 * inventory(s).
	 */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
	{
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);
		
		if(slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			
			if(index == 0)
			{
				itemstack1.getItem().onCreated(itemstack1, this.player.world, playerIn);
				
				if(!this.mergeItemStack(itemstack1, 10, 46, true))
					return ItemStack.EMPTY;
				slot.onSlotChange(itemstack1, itemstack);
			} else if(index >= 10 && index < 37)
			{
				if(!this.mergeItemStack(itemstack1, 37, 46, false))
					return ItemStack.EMPTY;
			} else if(index >= 37 && index < 46)
			{
				if(!this.mergeItemStack(itemstack1, 10, 37, false))
					return ItemStack.EMPTY;
			} else if(!this.mergeItemStack(itemstack1, 10, 46, false))
				return ItemStack.EMPTY;
			
			if(itemstack1.isEmpty())
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();
			
			if(itemstack1.getCount() == itemstack.getCount())
				return ItemStack.EMPTY;
			
			ItemStack itemstack2 = slot.onTake(playerIn, itemstack1);
			
			if(index == 0)
				playerIn.dropItem(itemstack2, false);
		}
		
		return itemstack;
	}
	
	/**
	 * Called to determine if the current slot is valid for the stack merging
	 * (double-click) code. The stack passed in is null for the initial slot
	 * that was double-clicked.
	 */
	@Override
	public boolean canMergeSlot(ItemStack stack, Slot slotIn)
	{
		return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn);
	}
}