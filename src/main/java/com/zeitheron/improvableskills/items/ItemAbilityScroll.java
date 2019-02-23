package com.zeitheron.improvableskills.items;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.zeitheron.hammercore.net.HCNet;
import com.zeitheron.hammercore.utils.Chars;
import com.zeitheron.hammercore.utils.SoundUtil;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.api.registry.PlayerAbilityBase;
import com.zeitheron.improvableskills.data.PlayerDataManager;
import com.zeitheron.improvableskills.init.ItemsIS;
import com.zeitheron.improvableskills.net.PacketScrollUnlockedAbility;
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
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemAbilityScroll extends Item
{
	private static final Map<String, PlayerAbilityBase> ABILITY_MAP = new HashMap<>();
	
	public ItemAbilityScroll()
	{
		setTranslationKey("scroll_ability");
		setMaxStackSize(1);
	}
	
	@Nullable
	public static PlayerAbilityBase getSkillFromScroll(ItemStack stack)
	{
		if(!stack.isEmpty() && stack.getItem() instanceof ItemAbilityScroll && stack.hasTagCompound() && stack.getTagCompound().hasKey("Ability", NBT.TAG_STRING))
		{
			String skill = stack.getTagCompound().getString("Ability");
			
			if(ABILITY_MAP.containsKey(skill))
				return ABILITY_MAP.get(skill);
			
			PlayerAbilityBase b = GameRegistry.findRegistry(PlayerAbilityBase.class).getValue(new ResourceLocation(stack.getTagCompound().getString("Ability")));
			
			ABILITY_MAP.put(skill, b);
			
			return b;
		}
		return null;
	}
	
	public static ItemStack of(PlayerAbilityBase base)
	{
		ItemStack stack = new ItemStack(ItemsIS.ABILITY_SCROLL);
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("Ability", base.getRegistryName().toString());
		stack.setTagCompound(tag);
		return stack;
	}
	
	public static void getItems(NonNullList<ItemStack> items)
	{
		GameRegistry.findRegistry(PlayerAbilityBase.class) //
		        .getValuesCollection() //
		        .stream() //
		        .forEach(skill -> items.add(ItemAbilityScroll.of(skill)));
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
		PlayerAbilityBase base = getSkillFromScroll(stack);
		if(base == null)
			return;
		tooltip.add(TextFormatting.GRAY + base.getLocalizedName(SyncSkills.getData()));
		if(flagIn.isAdvanced())
			tooltip.add(TextFormatting.DARK_GRAY + " - " + base.getRegistryName());
		if(GuiScreen.isShiftKeyDown())
		{
			String ln = I18n.format("recipe." + base.getRegistryName().getNamespace() + ":ability." + base.getRegistryName().getPath()).replace('&', Chars.SECTION_SIGN);
			int i, j;
			while((i = ln.indexOf('<')) != -1 && (j = ln.indexOf('>', i + 1)) != -1)
			{
				String to = ln.substring(i + 1, j);
				String t;
				
				Item it = ForgeRegistries.ITEMS.getValue(new ResourceLocation(to));
				if(it != null)
					t = it.getDefaultInstance().getDisplayName();
				else
					t = TextFormatting.DARK_RED + I18n.format("text.improvableskills:unresolved_item") + TextFormatting.GRAY;
				
				ln = ln.replaceAll("<" + to + ">", t);
			}
			tooltip.add(ln);
		} else
			tooltip.add(I18n.format("text.improvableskills:shiftfrecipe").replace('&', Chars.SECTION_SIGN));
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		if(!worldIn.isRemote)
		{
			PlayerSkillData data = PlayerDataManager.getDataFor(playerIn);
			PlayerAbilityBase base = getSkillFromScroll(playerIn.getHeldItem(handIn));
			
			if(!data.abilities.contains(base.getRegistryName().toString()))
			{
				data.abilities.add(base.getRegistryName().toString());
				ItemStack used = playerIn.getHeldItem(handIn).copy();
				playerIn.getHeldItem(handIn).shrink(1);
				HCNet.swingArm(playerIn, handIn);
				SoundUtil.playSoundEffect(worldIn, "block.enchantment_table.use", playerIn.getPosition(), .5F, 1F, SoundCategory.PLAYERS);
				
				int slot = handIn == EnumHand.OFF_HAND ? -2 : playerIn.inventory.currentItem;
				
				if(playerIn instanceof EntityPlayerMP)
					HCNet.INSTANCE.sendTo(new PacketScrollUnlockedAbility(slot, used, base.getRegistryName()), (EntityPlayerMP) playerIn);
				data.sync();
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
			}
		}
		
		return new ActionResult<ItemStack>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
	}
}