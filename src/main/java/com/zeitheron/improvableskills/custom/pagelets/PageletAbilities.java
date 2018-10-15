package com.zeitheron.improvableskills.custom.pagelets;

import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.api.registry.PageletBase;
import com.zeitheron.improvableskills.client.gui.GuiAbilityBook;
import com.zeitheron.improvableskills.client.gui.base.GuiTabbable;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PageletAbilities extends PageletBase
{
	{
		setRegistryName(InfoIS.MOD_ID, "abilities");
		setIcon(new ItemStack(Blocks.ENCHANTING_TABLE));
		setTitle(new TextComponentTranslation("pagelet." + InfoIS.MOD_ID + ":abilities"));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiTabbable createTab(PlayerSkillData data)
	{
		return new GuiAbilityBook(this, data);
	}
}