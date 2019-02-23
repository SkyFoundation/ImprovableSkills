package com.zeitheron.improvableskills.custom.pagelets;

import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.api.registry.PageletBase;
import com.zeitheron.improvableskills.client.gui.GuiXPBank;
import com.zeitheron.improvableskills.client.gui.base.GuiTabbable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PageletXPStorage extends PageletBase
{
	public final ResourceLocation texture = new ResourceLocation(InfoIS.MOD_ID, "textures/gui/xp_bank.png");
	
	{
		setRegistryName(InfoIS.MOD_ID, "xp_bank");
		setTitle(new TextComponentTranslation("pagelet." + InfoIS.MOD_ID + ":xp_bank"));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Object getIcon()
	{
		Object o = super.getIcon();
		
		if(o == null || !(o instanceof ITextureObject))
		{
			Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
			setIcon(o = Minecraft.getMinecraft().getTextureManager().getTexture(texture));
		}
		
		return o;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiTabbable createTab(PlayerSkillData data)
	{
		return new GuiXPBank(this);
	}
	
	@Override
	public void reload()
	{
	}
	
	@Override
	public boolean isVisible()
	{
		return true;
	}
}