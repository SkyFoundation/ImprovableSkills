package com.zeitheron.improvableskills.client.gui.abil.ench;

import java.util.List;
import java.util.Random;

import com.zeitheron.hammercore.internal.GuiManager;
import com.zeitheron.hammercore.utils.color.ColorHelper;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.data.PlayerDataManager;
import com.zeitheron.improvableskills.init.GuiHooksIS;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ContainerPortableEnchantment extends ContainerEnchantment
{
	public World worldIn;
	public EntityPlayer player;
	public int color;
	
	public ContainerPortableEnchantment(InventoryPlayer playerInv, World worldIn)
	{
		super(playerInv, worldIn, BlockPos.ORIGIN);
		player = playerInv.player;
		this.worldIn = worldIn;
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		return true;
	}
	
	@Override
	public void onCraftMatrixChanged(IInventory inventoryIn)
	{
		if(inventoryIn == this.tableInventory)
		{
			ItemStack itemstack = inventoryIn.getStackInSlot(0);
			
			if(!itemstack.isEmpty() && itemstack.isItemEnchantable())
			{
				if(!this.worldIn.isRemote)
				{
					int l = 0;
					float power = 0;
					
					PlayerSkillData data = PlayerDataManager.getDataFor(player);
					if(data != null)
						power = data.enchantPower;
					
					this.random.setSeed((long) this.xpSeed);
					
					for(int i1 = 0; i1 < 3; ++i1)
					{
						this.enchantLevels[i1] = EnchantmentHelper.calcItemStackEnchantability(this.random, i1, (int) power, itemstack);
						this.enchantClue[i1] = -1;
						this.worldClue[i1] = -1;
						
						if(this.enchantLevels[i1] < i1 + 1)
						{
							this.enchantLevels[i1] = 0;
						}
						this.enchantLevels[i1] = net.minecraftforge.event.ForgeEventFactory.onEnchantmentLevelSet(worldIn, player.getPosition(), i1, (int) power, itemstack, enchantLevels[i1]);
					}
					
					for(int j1 = 0; j1 < 3; ++j1)
					{
						if(this.enchantLevels[j1] > 0)
						{
							List<EnchantmentData> list = this.getEnchantmentList2(itemstack, j1, this.enchantLevels[j1]);
							
							if(list != null && !list.isEmpty())
							{
								EnchantmentData enchantmentdata = list.get(this.random.nextInt(list.size()));
								this.enchantClue[j1] = Enchantment.getEnchantmentID(enchantmentdata.enchantment);
								this.worldClue[j1] = enchantmentdata.enchantmentLevel;
							}
						}
					}
					
					this.detectAndSendChanges();
				}
			} else
			{
				for(int i = 0; i < 3; ++i)
				{
					this.enchantLevels[i] = 0;
					this.enchantClue[i] = -1;
					this.worldClue[i] = -1;
				}
			}
		}
	}
	
	final Random random = new Random();
	
	private List<EnchantmentData> getEnchantmentList2(ItemStack stack, int enchantSlot, int level)
	{
		this.random.setSeed((long) (this.xpSeed + enchantSlot));
		List<EnchantmentData> list = EnchantmentHelper.buildEnchantmentList(this.random, stack, level, false);
		
		if(stack.getItem() == Items.BOOK && list.size() > 1)
		{
			list.remove(this.random.nextInt(list.size()));
		}
		
		return list;
	}
	
	int capturing;
	IntList capture = new IntArrayList();
	
	@Override
	public boolean enchantItem(EntityPlayer playerIn, int id)
	{
		if(id == 666)
		{
			capturing = 3;
			return true;
		}
		
		if(id == 667)
		{
			if(!worldIn.isRemote)
			{
				GuiManager.openGuiCallback(GuiHooksIS.ENCH_POWER_BOOK_IO, playerIn, playerIn.world, playerIn.getPosition());
			}
			
			return true;
		}
		
		if(capturing > 0)
		{
			capture.add(id);
			
			--capturing;
			
			if(capture.size() == 3 && capturing == 0)
				color = capture.getInt(0) << 16 | capture.getInt(1) << 8 | capture.getInt(2);
			
			return true;
		}
		
		ItemStack itemstack = this.tableInventory.getStackInSlot(0);
		ItemStack itemstack1 = this.tableInventory.getStackInSlot(1);
		int i = id + 1;
		
		if((itemstack1.isEmpty() || itemstack1.getCount() < i) && !playerIn.capabilities.isCreativeMode)
		{
			return false;
		} else if(this.enchantLevels[id] > 0 && !itemstack.isEmpty() && (playerIn.experienceLevel >= i && playerIn.experienceLevel >= this.enchantLevels[id] || playerIn.capabilities.isCreativeMode))
		{
			if(!this.worldIn.isRemote)
			{
				List<EnchantmentData> list = this.getEnchantmentList2(itemstack, id, this.enchantLevels[id]);
				
				if(!list.isEmpty())
				{
					playerIn.onEnchant(itemstack, i);
					boolean flag = itemstack.getItem() == Items.BOOK;
					
					int red = Math.max(102, (color >> 16) & 255);
					int green = Math.max(102, (color >> 8) & 255);
					int blue = Math.max(102, (color >> 0) & 255);
					
					NBTTagCompound tag = itemstack.getTagCompound();
					if(tag == null)
						itemstack.setTagCompound(tag = new NBTTagCompound());
					tag.setString("HCCustomEnch", Integer.toHexString(ColorHelper.packRGB(red / 255F, green / 255F, blue / 255F)));
					itemstack.setTagCompound(tag);
					
					if(flag)
					{
						itemstack = new ItemStack(Items.ENCHANTED_BOOK);
						this.tableInventory.setInventorySlotContents(0, itemstack);
					}
					
					for(int j = 0; j < list.size(); ++j)
					{
						EnchantmentData enchantmentdata = list.get(j);
						
						if(flag)
						{
							ItemEnchantedBook.addEnchantment(itemstack, enchantmentdata);
						} else
						{
							itemstack.addEnchantment(enchantmentdata.enchantment, enchantmentdata.enchantmentLevel);
						}
					}
					
					if(!playerIn.capabilities.isCreativeMode)
					{
						itemstack1.shrink(i);
						
						if(itemstack1.isEmpty())
						{
							this.tableInventory.setInventorySlotContents(1, ItemStack.EMPTY);
						}
					}
					
					playerIn.addStat(StatList.ITEM_ENCHANTED);
					
					if(playerIn instanceof EntityPlayerMP)
					{
						CriteriaTriggers.ENCHANTED_ITEM.trigger((EntityPlayerMP) playerIn, itemstack, i);
					}
					
					this.tableInventory.markDirty();
					this.xpSeed = playerIn.getXPSeed();
					this.onCraftMatrixChanged(this.tableInventory);
					this.worldIn.playSound(null, player.getPosition(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, this.worldIn.rand.nextFloat() * 0.1F + 0.9F);
				}
			}
			
			return true;
		} else
		{
			return false;
		}
	}
}