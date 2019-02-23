package com.zeitheron.improvableskills.client.gui.abil.anvil;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.BlockAnvil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerSkilledAnvil extends ContainerRepair
{
	@SideOnly(Side.CLIENT)
	public ContainerSkilledAnvil(InventoryPlayer playerInventory, World worldIn, EntityPlayer player)
	{
		super(playerInventory, worldIn, player);
	}
	
	public ContainerSkilledAnvil(InventoryPlayer playerInventory, World worldIn, BlockPos blockPosIn, EntityPlayer player)
	{
		super(playerInventory, worldIn, blockPosIn, player);
		
		IInventory inv = getSlot(2).inventory;
		
		Slot new2 = new Slot(inv, 2, 134, 47)
		{
			/**
			 * Check if the stack is allowed to be placed in this slot, used for
			 * armor slots as well as furnace fuel.
			 */
			public boolean isItemValid(ItemStack stack)
			{
				return false;
			}
			
			/**
			 * Return whether this slot's stack can be taken from this slot.
			 */
			public boolean canTakeStack(EntityPlayer playerIn)
			{
				return (playerIn.capabilities.isCreativeMode || playerIn.experienceLevel >= maximumCost) && maximumCost > 0 && this.getHasStack();
			}
			
			public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack)
			{
				if(!thePlayer.capabilities.isCreativeMode)
				{
					thePlayer.addExperienceLevel(-maximumCost);
				}
				
				float breakChance = net.minecraftforge.common.ForgeHooks.onAnvilRepair(thePlayer, stack, getSlot(0).getStack(), getSlot(1).getStack());
				
				getSlot(0).putStack(ItemStack.EMPTY);
				
				if(materialCost > 0)
				{
					ItemStack itemstack = getSlot(1).getStack();
					
					if(!itemstack.isEmpty() && itemstack.getCount() > materialCost)
					{
						itemstack.shrink(materialCost);
						getSlot(1).putStack(itemstack);
					} else
					{
						getSlot(1).putStack(ItemStack.EMPTY);
					}
				} else
				{
					getSlot(1).putStack(ItemStack.EMPTY);
				}
				
				maximumCost = 0;
				IBlockState iblockstate = worldIn.getBlockState(blockPosIn);
				
				if(!thePlayer.capabilities.isCreativeMode && !worldIn.isRemote && iblockstate.getBlock() == Blocks.ANVIL && thePlayer.getRNG().nextFloat() < breakChance)
				{
					int l = ((Integer) iblockstate.getValue(BlockAnvil.DAMAGE)).intValue();
					++l;
					
					if(l > 2)
					{
						worldIn.setBlockToAir(blockPosIn);
						worldIn.playEvent(1029, blockPosIn, 0);
					} else
					{
						worldIn.setBlockState(blockPosIn, iblockstate.withProperty(BlockAnvil.DAMAGE, Integer.valueOf(l)), 2);
						worldIn.playEvent(1030, blockPosIn, 0);
					}
				} else if(!worldIn.isRemote)
				{
					worldIn.playEvent(1030, blockPosIn, 0);
				}
				
				return stack;
			}
		};
		
		new2.slotNumber = getSlot(2).slotNumber;
		inventorySlots.set(2, new2);
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		return true;
	}
}