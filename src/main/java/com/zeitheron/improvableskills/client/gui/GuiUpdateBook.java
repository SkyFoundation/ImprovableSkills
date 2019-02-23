package com.zeitheron.improvableskills.client.gui;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.utils.URLEncodedUtils;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;

import com.google.common.base.Joiner;
import com.zeitheron.hammercore.client.HCClientOptions;
import com.zeitheron.hammercore.client.utils.UV;
import com.zeitheron.hammercore.client.utils.texture.gui.theme.GuiTheme;
import com.zeitheron.hammercore.lib.zlib.json.JSONObject;
import com.zeitheron.hammercore.lib.zlib.json.JSONTokener;
import com.zeitheron.hammercore.lib.zlib.utils.MD5;
import com.zeitheron.hammercore.lib.zlib.utils.Threading;
import com.zeitheron.hammercore.lib.zlib.web.HttpRequest;
import com.zeitheron.hammercore.lib.zlib.web.HttpRequest.Base64;
import com.zeitheron.hammercore.utils.Chars;
import com.zeitheron.hammercore.utils.VersionCompareTool;
import com.zeitheron.hammercore.utils.color.ColorHelper;
import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.registry.PageletBase;
import com.zeitheron.improvableskills.client.gui.base.GuiTabbable;
import com.zeitheron.improvableskills.custom.pagelets.PageletNews;
import com.zeitheron.improvableskills.custom.pagelets.PageletUpdate;
import com.zeitheron.improvableskills.init.PageletsIS;
import com.zeitheron.improvableskills.init.SoundsIS;
import com.zeitheron.improvableskills.utils.GoogleTranslate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;

public class GuiUpdateBook extends GuiTabbable
{
	public String changes, translated;
	
	public GuiUpdateBook(PageletBase pagelet)
	{
		super(pagelet);
		
		xSize = 195;
		ySize = 168;
		
		reload();
	}
	
	public String getOrTranslate(String changes)
	{
		String md5 = MD5.encrypt(changes);
		HCClientOptions opts = HCClientOptions.getOptions();
		NBTTagCompound nbt = opts.getCustomData();
		String stored = nbt.getString("ImprovableSkillsUpdateMD5");
		String olng = nbt.getString("ImprovableSkillsUpdateLang");
		String lng = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getJavaLocale().getLanguage();
		
		try
		{
			Thread.sleep(500L);
		} catch(InterruptedException e1)
		{
		}
		
		if(!md5.equalsIgnoreCase(stored) || !lng.equalsIgnoreCase(olng))
		{
			try
			{
				Thread.sleep(2000L);
			} catch(InterruptedException e1)
			{
			}
			
			List<String> s = new ArrayList<>();
			for(String ln : changes.split("\n"))
			{
				try
				{
					if(!lng.equals("en"))
						ln = GoogleTranslate.translate(lng, ln);
				} catch(IOException ioe)
				{
				}
				s.add(ln);
			}
			
			String ts = Joiner.on("\n").join(s);
			
			nbt.setString("ImprovableSkillsUpdateMD5", md5);
			nbt.setString("ImprovableSkillsUpdateLang", lng);
			try
			{
				nbt.setString("ImprovableSkillsUpdateTranslated", URLEncoder.encode(ts, "UTF-8"));
			} catch(UnsupportedEncodingException e1)
			{
			}
			
			opts.save();
			
			try
			{
				Field f = PageletNews.class.getDeclaredField("popping");
				f.setAccessible(true);
				f.setBoolean(PageletsIS.NEWS, false);
			} catch(ReflectiveOperationException e)
			{
			}
			
			return ts;
		}
		
		String t = nbt.getString("ImprovableSkillsUpdateTranslated");
		try
		{
			t = URLDecoder.decode(t, "UTF-8").replaceAll("\r", "");
		} catch(UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return t;
	}
	
	public void reload()
	{
		changes = null;
		translated = null;
		
		Threading.createAndStart(() ->
		{
			try
			{
				JSONObject o = (JSONObject) new JSONTokener(new String(HttpRequest.get("https://pastebin.com/raw/CKrGidbG").bytes())).nextValue();
				
				PageletUpdate.changes = changes = o.getString("changelog");
				PageletUpdate.homepage = o.getString("homepage");
				PageletUpdate.latest = o.getJSONObject("promos").getString(Loader.MC_VERSION + "-latest");
				PageletUpdate.level = new VersionCompareTool(InfoIS.MOD_VERSION).compare(new VersionCompareTool(PageletUpdate.latest));
			} catch(Throwable err)
			{
				changes = "Unable to connect!";
			}
			
			String ts = changes;
			try
			{
				String c = "\u25BA ";
				ts = c + getOrTranslate(changes).replaceAll("\n", "\n" + c);
			} catch(Throwable er)
			{
				er.printStackTrace();
			}
			this.translated = ts;
		});
	}
	
	@Override
	protected void drawBack(float partialTicks, int mouseX, int mouseY)
	{
		GL11.glColor4f(1, 1, 1, 1);
		gui1.render(guiLeft, guiTop);
		
		ScaledResolution sr = new ScaledResolution(mc);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(3089);
		GL11.glScissor((int) Math.ceil(guiLeft * sr.getScaleFactor()), (int) Math.ceil((guiTop + 5) * sr.getScaleFactor()), (int) Math.ceil(xSize * sr.getScaleFactor()), (int) Math.ceil((ySize - 10) * sr.getScaleFactor()));
		
		String upd = I18n.format("gui." + InfoIS.MOD_ID + ":nver") + ": " + PageletUpdate.latest;
		boolean dwnHover = mouseX >= guiLeft + 16 && mouseY >= guiTop + 11 && mouseX < guiLeft + 16 + fontRenderer.getStringWidth(upd) && mouseY < guiTop + 11 + fontRenderer.FONT_HEIGHT;
		
		if(translated != null)
		{
			fontRenderer.drawSplitString((dwnHover ? TextFormatting.BLUE : TextFormatting.RESET) + TextFormatting.UNDERLINE.toString() + upd, (int) guiLeft + 16, (int) guiTop + 11, (int) gui1.width - 22, 0xFF_000000);
			fontRenderer.drawSplitString(I18n.format("gui." + InfoIS.MOD_ID + ":changes") + ": \n" + translated, (int) guiLeft + 12, (int) guiTop + 12 + fontRenderer.FONT_HEIGHT, (int) gui1.width - 22, 0xFF_000000);
		}
		else
			GuiNewsBook.spawnLoading(width, height);
		
		GlStateManager.enableDepth();
		
		GL11.glDisable(3089);
		
		int rgb = GuiTheme.CURRENT_THEME.name.equalsIgnoreCase("Vanilla") ? 0x0088FF : GuiTheme.CURRENT_THEME.bodyColor;
		
		ColorHelper.gl(255 << 24 | rgb);
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, 5);
		gui2.render(guiLeft, guiTop);
		GlStateManager.popMatrix();
		
		GL11.glColor4f(1, 1, 1, 1);
		
		GL11.glDisable(GL11.GL_BLEND);
		GlStateManager.disableDepth();
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		String upd = I18n.format("gui." + InfoIS.MOD_ID + ":nver") + ": " + PageletUpdate.latest;
		boolean dwnHover = mouseX >= guiLeft + 16 && mouseY >= guiTop + 11 && mouseX < guiLeft + 16 + fontRenderer.getStringWidth(upd) && mouseY < guiTop + 11 + fontRenderer.FONT_HEIGHT;
		
		if(dwnHover)
		{
			mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_SLIME_SQUISH, 1F));
			Sys.openURL(PageletUpdate.homepage + "/files");
			return;
		}
		
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException
	{
		if(keyCode == 1 || mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode))
		{
			mc.displayGuiScreen(parent);
			if(mc.currentScreen == null)
				mc.setIngameFocus();
		}
	}
}