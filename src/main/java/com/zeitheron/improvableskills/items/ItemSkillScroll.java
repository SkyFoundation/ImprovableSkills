package com.zeitheron.improvableskills.items;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.zeitheron.hammercore.net.HCNet;
import com.zeitheron.hammercore.utils.Chars;
import com.zeitheron.hammercore.utils.SoundUtil;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.api.registry.PlayerSkillBase;
import com.zeitheron.improvableskills.data.PlayerDataManager;
import com.zeitheron.improvableskills.init.ItemsIS;
import com.zeitheron.improvableskills.net.PacketScrollUnlockedSkill;
import com.zeitheron.improvableskills.proxy.SyncSkills;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
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
		setTranslationKey("scroll_normal");
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
			ItemStack stack = new ItemStack(ItemsIS.SKILL_SCROLL);
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
		        .getValuesCollection() //
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
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		PlayerSkillBase base = getSkillFromScroll(stack);
		if(base == null)
			return;
		tooltip.add(TextFormatting.GRAY + base.getLocalizedName(SyncSkills.getData()));
		if(flagIn.isAdvanced())
			tooltip.add(TextFormatting.DARK_GRAY + " - " + base.getRegistryName());
		if(GuiScreen.isShiftKeyDown())
			tooltip.add(I18n.format("recipe." + base.getRegistryName().getNamespace() + ":skill." + base.getRegistryName().getPath()).replace('&', Chars.SECTION_SIGN));
		else
			tooltip.add(I18n.format("text.improvableskills:shiftfrecipe").replace('&', Chars.SECTION_SIGN));
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		if(!worldIn.isRemote)
			return PlayerDataManager.handleDataSafely(playerIn, data ->
			{
				PlayerSkillBase base = getSkillFromScroll(playerIn.getHeldItem(handIn));
				
				if(!data.stat_scrolls.contains(base.getRegistryName().toString()))
				{
					data.stat_scrolls.add(base.getRegistryName().toString());
					ItemStack used = playerIn.getHeldItem(handIn).copy();
					playerIn.getHeldItem(handIn).shrink(1);
					HCNet.swingArm(playerIn, handIn);
					SoundUtil.playSoundEffect(worldIn, "block.enchantment_table.use", playerIn.getPosition(), .5F, 1F, SoundCategory.PLAYERS);
					
					int slot = handIn == EnumHand.OFF_HAND ? -2 : playerIn.inventory.currentItem;
					
					if(playerIn instanceof EntityPlayerMP)
						HCNet.INSTANCE.sendTo(new PacketScrollUnlockedSkill(slot, used, base.getRegistryName()), (EntityPlayerMP) playerIn);
					data.sync();
					return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
				} else if(data.getSkillLevel(base) < base.maxLvl)
				{
					data.setSkillLevel(base, data.getSkillLevel(base) + 1);
					ItemStack used = playerIn.getHeldItem(handIn).copy();
					playerIn.getHeldItem(handIn).shrink(1);
					HCNet.swingArm(playerIn, handIn);
					SoundUtil.playSoundEffect(worldIn, "block.enchantment_table.use", playerIn.getPosition(), .5F, 1F, SoundCategory.PLAYERS);
					int slot = handIn == EnumHand.OFF_HAND ? -2 : playerIn.inventory.currentItem;
					if(playerIn instanceof EntityPlayerMP)
						HCNet.INSTANCE.sendTo(new PacketScrollUnlockedSkill(slot, used, base.getRegistryName()), (EntityPlayerMP) playerIn);
					return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
				}
				
				return new ActionResult<ItemStack>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
			}, new ActionResult<ItemStack>(EnumActionResult.PASS, playerIn.getHeldItem(handIn)));
		
		return new ActionResult<ItemStack>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
	}
}