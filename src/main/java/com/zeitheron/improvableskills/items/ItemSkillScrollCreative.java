package com.zeitheron.improvableskills.items;

import java.util.ArrayList;
import java.util.List;

import com.zeitheron.hammercore.net.HCNet;
import com.zeitheron.hammercore.utils.SoundUtil;
import com.zeitheron.improvableskills.api.PlayerSkillBase;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.data.PlayerDataManager;
import com.zeitheron.improvableskills.net.PacketScrollUnlockedSkill;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemSkillScrollCreative extends Item
{
	public ItemSkillScrollCreative()
	{
		setTranslationKey("scroll_creative");
		setMaxStackSize(1);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		if(!worldIn.isRemote)
		{
			PlayerSkillData data = PlayerDataManager.getDataFor(playerIn);
			
			List<ResourceLocation> loc = new ArrayList<>();
			for(PlayerSkillBase base : GameRegistry.findRegistry(PlayerSkillBase.class).getValues())
				if(base.getScrollState().hasScroll())
				{
					if(!data.stat_scrolls.contains(base.getRegistryName().toString()))
					{
						data.stat_scrolls.add(base.getRegistryName().toString());
						loc.add(base.getRegistryName());
					}
				}
			
			if(loc.isEmpty())
				return new ActionResult<ItemStack>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
			
			ItemStack used = playerIn.getHeldItem(handIn).copy();
			playerIn.getHeldItem(handIn).shrink(1);
			
			int slot = handIn == EnumHand.OFF_HAND ? -2 : playerIn.inventory.currentItem;
			data.sync();
			HCNet.swingArm(playerIn, handIn);
			SoundUtil.playSoundEffect(worldIn, "block.enchantment_table.use", playerIn.getPosition(), .5F, 1F, SoundCategory.PLAYERS);
			if(playerIn instanceof EntityPlayerMP)
				HCNet.INSTANCE.sendTo(new PacketScrollUnlockedSkill(slot, used, loc.toArray(new ResourceLocation[0])), (EntityPlayerMP) playerIn);
			
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
		}
		
		return new ActionResult<ItemStack>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
	}
}