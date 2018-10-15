package com.zeitheron.improvableskills.custom.pagelets;

import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.api.registry.PageletBase;
import com.zeitheron.improvableskills.client.gui.GuiSkillsBook;
import com.zeitheron.improvableskills.client.gui.base.GuiTabbable;
import com.zeitheron.improvableskills.init.SkillsIS;
import com.zeitheron.improvableskills.items.ItemSkillScroll;

import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PageletSkills extends PageletBase
{
	{
		setRegistryName(InfoIS.MOD_ID, "skills");
		setIcon(ItemSkillScroll.of(SkillsIS.HEALTH));
		setTitle(new TextComponentTranslation("pagelet." + InfoIS.MOD_ID + ":skills"));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiTabbable createTab(PlayerSkillData data)
	{
		return new GuiSkillsBook(this, data);
	}
}