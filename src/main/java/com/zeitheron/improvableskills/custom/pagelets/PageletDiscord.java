package com.zeitheron.improvableskills.custom.pagelets;

import java.net.URI;
import java.util.List;

import com.zeitheron.improvableskills.ImprovableSkillsMod;
import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.registry.PageletBase;

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
	
	ITextComponent discord2;
	
	Object staticIcon;
	
	{
		setRegistryName(InfoIS.MOD_ID, "discord");
		setTitle(new TextComponentTranslation("pagelet." + InfoIS.MOD_ID + ":discord1"));
		discord2 = new TextComponentTranslation("pagelet." + InfoIS.MOD_ID + ":discord2");
		discord2.getStyle().setColor(TextFormatting.GRAY);
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
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasTab()
	{
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void onClick()
	{
		GuiScreen parent = Minecraft.getMinecraft().currentScreen;
		
		String url = "https://discord.gg/" + PageletUpdate.discord;
		
		Minecraft.getMinecraft().displayGuiScreen(new GuiConfirmOpenLink((result, id) ->
		{
			if(result)
			{
				try
				{
					Class<?> oclass = Class.forName("java.awt.Desktop");
					Object object = oclass.getMethod("getDesktop").invoke(null);
					oclass.getMethod("browse", URI.class).invoke(object, new URI(url));
				} catch(Throwable throwable)
				{
					ImprovableSkillsMod.LOG.error("Couldn't open link", throwable);
				}
			}
			
			Minecraft.getMinecraft().displayGuiScreen(parent);
		}, url, 0, true));
	}
	
	@Override
	public void addTitle(List<String> text)
	{
		super.addTitle(text);
		text.add(discord2.getFormattedText());
	}
}