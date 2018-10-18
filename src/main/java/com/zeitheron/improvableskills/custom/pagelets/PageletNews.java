package com.zeitheron.improvableskills.custom.pagelets;

import com.zeitheron.hammercore.client.HCClientOptions;
import com.zeitheron.hammercore.lib.zlib.utils.MD5;
import com.zeitheron.hammercore.lib.zlib.web.HttpRequest;
import com.zeitheron.hammercore.lib.zlib.web.HttpRequest.HttpRequestException;
import com.zeitheron.hammercore.utils.VersionCompareTool.EnumVersionLevel;
import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.api.registry.PageletBase;
import com.zeitheron.improvableskills.client.gui.GuiNewsBook;
import com.zeitheron.improvableskills.client.gui.base.GuiTabbable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PageletNews extends PageletBase
{
	public final ResourceLocation texture = new ResourceLocation(InfoIS.MOD_ID, "textures/gui/news.png");
	
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
	
	boolean popping = true;
	
	@Override
	public void reload()
	{
		popping = true;
		
		try
		{
			String changes = new String(HttpRequest.get("https://pastebin.com/raw/DUCFiYpm").connectTimeout(5000).bytes());
			String rem = MD5.encrypt(changes);
			
			HCClientOptions opts = HCClientOptions.getOptions();
			NBTTagCompound nbt = opts.getCustomData();
			String stored = nbt.getString("ImprovableSkillsNewsMD5");
			
			if(stored.equalsIgnoreCase(rem))
				popping = false;
		} catch(HttpRequestException er)
		{
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean doesPop()
	{
		return popping;
	}
	
	@Override
	public GuiTabbable createTab(PlayerSkillData data)
	{
		return new GuiNewsBook(this);
	}
}