package com.endie.is.items;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.endie.is.api.PlayerSkillBase;
import com.endie.is.api.PlayerSkillData;
import com.endie.is.data.PlayerDataManager;
import com.endie.is.init.ItemsIS;
import com.endie.is.net.PacketScrollUnlockedSkill;
import com.endie.is.proxy.SyncSkills;
import com.pengu.hammercore.common.utils.SoundUtil;
import com.pengu.hammercore.net.HCNetwork;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemSkillScroll extends Item
{
	private static final Map<String, PlayerSkillBase> SKILL_MAP = new HashMap<>();
	
	public ItemSkillScroll()
	{
		setUnlocalizedName("scroll_normal");
		setMaxStackSize(1);
	}
	
	@Nullable
	public static PlayerSkillBase getSkillFromScroll(ItemStack stack)
	{
		if(!stack.isEmpty() && stack.getItem() instanceof ItemSkillScroll && stack.hasTagCompound() && stack.getTagCompound().hasKey("Skill", NBT.TAG_STRING))
		{
			String skill = stack.getTagCompound().getString("Skill");
			
			if(SKILL_MAP.containsKey(skill))
				return SKILL_MAP.get(skill);
			
			PlayerSkillBase b = GameRegistry.findRegistry(PlayerSkillBase.class).getValue(new ResourceLocation(stack.getTagCompound().getString("Skill")));
			
			SKILL_MAP.put(skill, b);
			
			return b;
		}
		return null;
	}
	
	public static ItemStack of(PlayerSkillBase base)
	{
		if(base.getScrollState().hasScroll())
		{
			ItemStack stack = new ItemStack(ItemsIS.SCROLL);
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("Skill", base.getRegistryName().toString());
			stack.setTagCompound(tag);
			return stack;
		}
		return ItemStack.EMPTY;
	}
	
	public static void getItems(NonNullList<ItemStack> items)
	{
		GameRegistry.findRegistry(PlayerSkillBase.class) //
		        .getValues() //
		        .stream() //
		        .filter(skill -> skill.getScrollState().hasScroll()) //
		        .forEach(skill -> items.add(ItemSkillScroll.of(skill)));
	}
	
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
	{
		if(isInCreativeTab(tab))
			getItems(items);
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack stack)
	{
		return super.getItemStackDisplayName(stack);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		PlayerSkillBase base = getSkillFromScroll(stack);
		
		tooltip.add(TextFormatting.GRAY + base.getLocalizedName(SyncSkills.getData()));
		if(flagIn.isAdvanced())
			tooltip.add(TextFormatting.DARK_GRAY + " - " + base.getRegistryName());
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		if(!worldIn.isRemote)
		{
			PlayerSkillData data = PlayerDataManager.getDataFor(playerIn);
			PlayerSkillBase base = getSkillFromScroll(playerIn.getHeldItem(handIn));
			
			if(!data.stat_scrolls.contains(base.getRegistryName().toString()))
			{
				data.stat_scrolls.add(base.getRegistryName().toString());
				playerIn.getHeldItem(handIn).shrink(1);
				HCNetwork.swingArm(playerIn, handIn);
				SoundUtil.playSoundEffect(worldIn, "block.enchantment_table.use", playerIn.getPosition(), .5F, 1F, SoundCategory.PLAYERS);
				
				int slot = handIn == EnumHand.OFF_HAND ? -2 : playerIn.inventory.currentItem;
				
				if(playerIn instanceof EntityPlayerMP)
					HCNetwork.manager.sendTo(new PacketScrollUnlockedSkill(base.getRegistryName(), slot), (EntityPlayerMP) playerIn);
				data.sync();
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
			}
		}
		
		return new ActionResult<ItemStack>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
	}
}