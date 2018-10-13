package com.zeitheron.improvableskills.items;

import com.zeitheron.hammercore.net.HCNet;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.data.PlayerDataManager;
import com.zeitheron.improvableskills.net.PacketOpenSkillsBook;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemSkillsBook extends Item
{
	public ItemSkillsBook()
	{
		setTranslationKey("skills_book");
		setMaxStackSize(1);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		if(!worldIn.isRemote && playerIn instanceof EntityPlayerMP)
			HCNet.INSTANCE.sendTo(new PacketOpenSkillsBook(PlayerDataManager.getDataFor(playerIn)), (EntityPlayerMP) playerIn);
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}
	
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	{
		if(entityIn instanceof EntityPlayerMP && !worldIn.isRemote)
		{
			PlayerSkillData d = PlayerDataManager.getDataFor((EntityPlayerMP) entityIn);
//			d.stat_scrolls.clear();
			if(d != null)
				d.hasCraftedSkillBook = true;
		}
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
	}
}