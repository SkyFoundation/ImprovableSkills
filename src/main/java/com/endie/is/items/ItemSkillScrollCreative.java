package com.endie.is.items;

import java.util.ArrayList;
import java.util.List;

import com.endie.is.api.PlayerSkillBase;
import com.endie.is.api.PlayerSkillData;
import com.endie.is.data.PlayerDataManager;
import com.endie.is.net.PacketScrollUnlockedSkill;
import com.pengu.hammercore.common.utils.SoundUtil;
import com.pengu.hammercore.net.HCNetwork;

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
		setUnlocalizedName("scroll_creative");
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
			HCNetwork.swingArm(playerIn, handIn);
			SoundUtil.playSoundEffect(worldIn, "block.enchantment_table.use", playerIn.getPosition(), .5F, 1F, SoundCategory.PLAYERS);
			if(playerIn instanceof EntityPlayerMP)
				HCNetwork.manager.sendTo(new PacketScrollUnlockedSkill(slot, used, loc.toArray(new ResourceLocation[0])), (EntityPlayerMP) playerIn);
			
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
		}
		
		return new ActionResult<ItemStack>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
	}
}