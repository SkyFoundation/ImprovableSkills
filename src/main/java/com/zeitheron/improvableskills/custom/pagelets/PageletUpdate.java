package com.zeitheron.improvableskills.custom.pagelets;

import java.io.FileOutputStream;
import java.util.Base64;

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
import scala.sys.process.ProcessBuilderImpl.FileOutput;

public class PageletUpdate extends PageletBase
{
	public final ResourceLocation texture = new ResourceLocation(InfoIS.MOD_ID, "textures/gui/update.png");
	
	public static EnumVersionLevel level;
	public static String changes, latest, discord, homepage;
	public static String liveURL, liveTitle;
	
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
	public boolean doesPop()
	{
		return true;
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
		return new GuiUpdateBook(this);
	}
	
	@Override
	public void reload()
	{
		Threading.createAndStart(() ->
		{
			try
			{
				JSONObject o = (JSONObject) new JSONTokener(HttpRequest.get("https://dccg.herokuapp.com/api/fmluc/252902?changelog=true&zdev=true").body()).nextValue();
				
				changes = new String(Base64.getDecoder().decode(o.getJSONObject("changelogs64").getString(Loader.MC_VERSION + "-latest")));
				discord = "https://dccg.herokuapp.com/invite/zeithdev";
				homepage = o.getString("homepage");
				latest = o.getJSONObject("promos").getString(Loader.MC_VERSION + "-latest");
				level = new VersionCompareTool(InfoIS.MOD_VERSION).compare(new VersionCompareTool(latest));
				
				liveURL = null;
				liveTitle = null;
				
				JSONObject dev = o.optJSONObject("dev");
				if(dev != null && dev.getBoolean("live"))
				{
					liveURL = dev.getString("url");
					
					// Get the livestream title
					String txt = HttpRequest.get(liveURL).body();
					txt = txt.substring(txt.indexOf("<title>") + 7);
					txt = txt.substring(0, txt.indexOf("</title>"));
					if(txt.toLowerCase().endsWith(" - youtube"))
						txt = txt.substring(0, txt.length() - 10);
					liveTitle = txt;
				}
			} catch(Throwable err)
			{
				err.printStackTrace();
			}
		});
	}
	
	@Override
	public boolean isVisible()
	{
		return level == EnumVersionLevel.OLDER;
	}
}