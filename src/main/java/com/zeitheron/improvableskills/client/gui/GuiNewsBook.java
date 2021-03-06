package com.zeitheron.improvableskills.client.gui;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Joiner;
import com.zeitheron.hammercore.client.HCClientOptions;
import com.zeitheron.hammercore.client.utils.UV;
import com.zeitheron.hammercore.client.utils.texture.gui.theme.GuiTheme;
import com.zeitheron.hammercore.lib.zlib.utils.MD5;
import com.zeitheron.hammercore.lib.zlib.utils.Threading;
import com.zeitheron.hammercore.lib.zlib.web.HttpRequest;
import com.zeitheron.hammercore.utils.color.ColorHelper;
import com.zeitheron.hammercore.utils.color.Rainbow;
import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.registry.PageletBase;
import com.zeitheron.improvableskills.client.gui.base.GuiTabbable;
import com.zeitheron.improvableskills.client.rendering.OnTopEffects;
import com.zeitheron.improvableskills.client.rendering.ote.OTESparkle;
import com.zeitheron.improvableskills.custom.pagelets.PageletNews;
import com.zeitheron.improvableskills.init.PageletsIS;
import com.zeitheron.improvableskills.utils.GoogleTranslate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class GuiNewsBook extends GuiTabbable
{
	public final UV gui1;
	
	public String changes, translated;
	
	public GuiNewsBook(PageletBase pagelet)
	{
		super(pagelet);
		
		xSize = 195;
		ySize = 168;
		
		gui1 = new UV(new ResourceLocation(InfoIS.MOD_ID, "textures/gui/skills_gui_paper.png"), 0, 0, xSize, ySize);
		
		reload();
	}
	
	public String getOrTranslate(String changes)
	{
		String md5 = MD5.encrypt(changes);
		HCClientOptions opts = HCClientOptions.getOptions();
		NBTTagCompound nbt = opts.getCustomData();
		String stored = nbt.getString("ImprovableSkillsNewsMD5");
		String olng = nbt.getString("ImprovableSkillsNewsLang");
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
			
			nbt.setString("ImprovableSkillsNewsMD5", md5);
			nbt.setString("ImprovableSkillsNewsLang", lng);
			try
			{
				nbt.setString("ImprovableSkillsNewsTranslated", URLEncoder.encode(ts, "UTF-8"));
			} catch(UnsupportedEncodingException e1)
			{
				e1.printStackTrace();
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
		
		String t = nbt.getString("ImprovableSkillsNewsTranslated");
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
			changes = new String(HttpRequest.get("https://pastebin.com/raw/DUCFiYpm").connectTimeout(5000).bytes());
			if(changes == null)
				changes = "";
			this.translated = getOrTranslate(changes);
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
		
		if(translated != null)
			fontRenderer.drawSplitString(translated, (int) guiLeft + 12, (int) guiTop + 12, (int) gui1.width - 22, 0xFF_000000);
		else
			spawnLoading(width, height);
		
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
	protected void keyTyped(char typedChar, int keyCode) throws IOException
	{
		if(keyCode == 1 || mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode))
		{
			mc.displayGuiScreen(parent);
			if(mc.currentScreen == null)
				mc.setIngameFocus();
		}
	}
	
	public static void spawnLoading(float width, float height)
	{
		Minecraft mc = Minecraft.getMinecraft();
		float partialTicks = mc.getRenderPartialTicks();
		
		int dots = 3;
		float angle = 360 / dots;
		float degree = ((mc.player.ticksExisted + partialTicks) * 3) % 360F;
		
		float x = width / 2, y = height / 2;
		float rad = 48;
		
		for(int i = 0; i < dots; ++i)
		{
			double ax = x + Math.sin(Math.toRadians(degree)) * rad, ay = y + Math.cos(Math.toRadians(degree)) * rad;
			
			double oax = x + Math.sin(Math.toRadians(degree - 30)) * rad, oay = y + Math.cos(Math.toRadians(degree - 30)) * rad;
			
			if(Math.random() < .25)
				OnTopEffects.effects.add(new OTESparkle(ax, ay, oax, oay, 20, 255 << 24 | Rainbow.doIt(i * 1000 / dots, 1000L)));
			
			degree += angle;
		}
	}
}