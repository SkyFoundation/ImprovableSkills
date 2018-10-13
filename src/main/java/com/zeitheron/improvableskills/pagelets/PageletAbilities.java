package com.zeitheron.improvableskills.pagelets;

import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.Pagelet;
import com.zeitheron.improvableskills.client.gui.GuiSkillsBook;
import com.zeitheron.improvableskills.client.gui.base.GuiTabbable;
import com.zeitheron.improvableskills.init.ItemsIS;
import com.zeitheron.improvableskills.init.SkillsIS;
import com.zeitheron.improvableskills.items.ItemSkillScroll;
import com.zeitheron.improvableskills.proxy.SyncSkills;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;

public class PageletAbilities extends Pagelet
{
	{
		setRegistryName(InfoIS.MOD_ID, "abilities");
		setIcon(new ItemStack(Blocks.ENCHANTING_TABLE));
		setTitle(new TextComponentTranslation("pagelet." + InfoIS.MOD_ID + ":abilities"));
	}
	
	@Override
	public GuiTabbable createTab()
	{
		return new GuiSkillsBook(SyncSkills.CLIENT_DATA);
	}
}