package com.zeitheron.improvableskills.client.gui.abil.ench;

import com.zeitheron.hammercore.client.gui.impl.container.ItemTransferHelper.TransferableContainer;
import com.zeitheron.hammercore.internal.GuiManager;
import com.zeitheron.hammercore.lib.zlib.utils.Threading;
import com.zeitheron.hammercore.utils.inventory.InventoryDummy;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.data.PlayerDataManager;
import com.zeitheron.improvableskills.init.GuiHooksIS;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ContainerEnchPowBook extends TransferableContainer<World>
{
	public final InventoryDummy inventory = new InventoryDummy(1);
	
	public ContainerEnchPowBook(EntityPlayer player, World t)
	{
		super(player, t, 8, 82);
		
		addSlotToContainer(new Slot(inventory, 0, 176 / 2 - 36, 32)
		{
			@Override
			public boolean isItemValid(ItemStack stack)
			{
				return !stack.isEmpty() && stack.getItem() == Items.BOOK;
			}
		});
		
		addTransfer();
	}
	
	@Override
	public boolean enchantItem(EntityPlayer playerIn, int id)
	{
		if(id == 11)
			PlayerDataManager.handleDataSafely(playerIn, data ->
			{
				ItemStack item = inventory.getStackInSlot(0);
				if(item.isEmpty() || (item.getItem() == Items.BOOK && item.getCount() < 64) && data.enchantPower > 0F)
				{
					if(item.isEmpty())
						inventory.setInventorySlotContents(0, new ItemStack(Items.BOOK));
					else
						item.grow(1);
					data.enchantPower -= 1F;
					data.sync();
					playerIn.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 1.0F, playerIn.world.rand.nextFloat() * 0.1F + 1.5F);
				}
			});
		if(id == 0)
			PlayerDataManager.handleDataSafely(playerIn, data ->
			{
				ItemStack item = inventory.getStackInSlot(0);
				if(!item.isEmpty() && item.getItem() == Items.BOOK && data.enchantPower < 15F)
				{
					item.shrink(1);
					data.enchantPower += 1F;
					data.sync();
					playerIn.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 1.0F, playerIn.world.rand.nextFloat() * 0.1F + 1.5F);
				}
			});
		if(id == 1)
			GuiManager.openGuiCallback(GuiHooksIS.ENCHANTMENT, playerIn, playerIn.world, playerIn.getPosition());
		return false;
	}
	
	@Override
	protected void addTransfer()
	{
		int bslot = -1;
		for(int i = 0; i < inventorySlots.size(); ++i)
			if(inventorySlots.get(i).inventory == inventory)
			{
				bslot = i;
				break;
			}
		
		if(bslot != -1)
		{
			int fs = bslot;
			
			transfer.addInTransferRule(fs, stack -> stack.getItem() == Items.BOOK);
			transfer.addOutTransferRule(fs, s -> s != fs);
		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		return true;
	}
	
	@Override
	public void onContainerClosed(EntityPlayer playerIn)
	{
		clearContainer(playerIn, t, inventory);
		super.onContainerClosed(playerIn);
		
		// Magic
		if(playerIn.getServer() != null)
			Threading.createAndStart(() -> playerIn.getServer().addScheduledTask(() -> GuiManager.openGuiCallback(GuiHooksIS.ENCHANTMENT, playerIn, playerIn.world, playerIn.getPosition())));
	}
}