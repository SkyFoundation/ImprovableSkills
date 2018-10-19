package com.zeitheron.improvableskills.custom.pagelets;

import java.net.URI;
import java.util.List;

import com.zeitheron.improvableskills.ImprovableSkillsMod;
import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.api.registry.PageletBase;
import com.zeitheron.improvableskills.client.gui.GuiDiscord;
import com.zeitheron.improvableskills.client.gui.base.GuiTabbable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PageletDiscord extends PageletBase
{
	public final ResourceLocation texture = new ResourceLocation(InfoIS.MOD_ID, "textures/gui/discord.png");
	
	Object staticIcon;
	
	{
		setRegistryName(InfoIS.MOD_ID, "discord");
		setTitle(new TextComponentTranslation("pagelet." + InfoIS.MOD_ID + ":discord1"));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiTabbable createTab(PlayerSkillData data)
	{
		return new GuiDiscord();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Object getIcon()
	{
		Object o = staticIcon;
		
		{
			TextureManager mgr = Minecraft.getMinecraft().getTextureManager();
			
			ITextureObject itextureobject = mgr.getTexture(texture);
			
			if(itextureobject == null)
			{
				itextureobject = new SimpleTexture(texture);
				mgr.loadTexture(texture, itextureobject);
			}
			
			staticIcon = o = itextureobject;
		}
		
		return o;
	}
	
	@Override
	public boolean isRight()
	{
		return false;
	}
}