package com.zeitheron.improvableskills.custom.pagelets;

import com.zeitheron.hammercore.utils.VersionCompareTool.EnumVersionLevel;
import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.api.registry.PageletBase;
import com.zeitheron.improvableskills.client.gui.GuiNewsBook;
import com.zeitheron.improvableskills.client.gui.base.GuiTabbable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PageletNews extends PageletBase
{
	public final ResourceLocation texture = new ResourceLocation(InfoIS.MOD_ID, "textures/gui/news.png");
	
	public static EnumVersionLevel level;
	public static String changes, latest;
	
	{
		setRegistryName(InfoIS.MOD_ID, "news");
		setTitle(new TextComponentTranslation("pagelet." + InfoIS.MOD_ID + ":news"));
	}
	
	@Override
	public boolean isRight()
	{
		return false;
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
	public GuiTabbable createTab(PlayerSkillData data)
	{
		return new GuiNewsBook(this);
	}
}