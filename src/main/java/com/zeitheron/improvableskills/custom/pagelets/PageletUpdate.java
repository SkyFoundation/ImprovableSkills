package com.zeitheron.improvableskills.custom.pagelets;

import com.zeitheron.hammercore.lib.zlib.json.JSONObject;
import com.zeitheron.hammercore.lib.zlib.json.JSONTokener;
import com.zeitheron.hammercore.lib.zlib.utils.Threading;
import com.zeitheron.hammercore.lib.zlib.web.HttpRequest;
import com.zeitheron.hammercore.utils.VersionCompareTool;
import com.zeitheron.hammercore.utils.VersionCompareTool.EnumVersionLevel;
import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.api.registry.PageletBase;
import com.zeitheron.improvableskills.client.gui.GuiUpdateBook;
import com.zeitheron.improvableskills.client.gui.base.GuiTabbable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PageletUpdate extends PageletBase
{
	public final ResourceLocation texture = new ResourceLocation(InfoIS.MOD_ID, "textures/gui/update.png");
	
	public static EnumVersionLevel level;
	public static String changes, latest;
	
	{
		setRegistryName(InfoIS.MOD_ID, "update");
		setTitle(new TextComponentTranslation("pagelet." + InfoIS.MOD_ID + ":update"));
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
		return new GuiUpdateBook(this);
	}
	
	@Override
	public boolean isVisible()
	{
		if(level == null)
			Threading.createAndStart(() ->
			{
				try
				{
					JSONObject o = (JSONObject) new JSONTokener(new String(HttpRequest.get("https://pastebin.com/raw/CKrGidbG").bytes())).nextValue();
					
					changes = o.getString("changelog");
					latest = o.getJSONObject("promos").getString(Loader.MC_VERSION + "-latest");
					level = new VersionCompareTool(InfoIS.MOD_VERSION).compare(new VersionCompareTool(latest));
				} catch(Throwable err)
				{
					err.printStackTrace();
				}
			});
		return level == EnumVersionLevel.OLDER;
	}
}