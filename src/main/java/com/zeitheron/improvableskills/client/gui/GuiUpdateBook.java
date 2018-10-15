package com.zeitheron.improvableskills.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Joiner;
import com.zeitheron.hammercore.client.utils.UV;
import com.zeitheron.hammercore.client.utils.texture.gui.theme.GuiTheme;
import com.zeitheron.hammercore.lib.zlib.json.JSONObject;
import com.zeitheron.hammercore.lib.zlib.json.JSONTokener;
import com.zeitheron.hammercore.lib.zlib.utils.Threading;
import com.zeitheron.hammercore.lib.zlib.web.HttpRequest;
import com.zeitheron.hammercore.utils.Chars;
import com.zeitheron.hammercore.utils.VersionCompareTool;
import com.zeitheron.hammercore.utils.color.ColorHelper;
import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.registry.PageletBase;
import com.zeitheron.improvableskills.client.gui.base.GuiTabbable;
import com.zeitheron.improvableskills.custom.pagelets.PageletUpdate;
import com.zeitheron.improvableskills.utils.GoogleTranslate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;

public class GuiUpdateBook extends GuiTabbable
{
	public final UV gui1;
	
	public String changes, translated;
	
	public GuiUpdateBook(PageletBase pagelet)
	{
		super(pagelet);
		
		xSize = 195;
		ySize = 168;
		
		gui1 = new UV(new ResourceLocation(InfoIS.MOD_ID, "textures/gui/skills_gui_paper.png"), 0, 0, xSize, ySize);
		
		reload();
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
				PageletUpdate.latest = o.getJSONObject("promos").getString(Loader.MC_VERSION + "-latest");
				PageletUpdate.level = new VersionCompareTool(InfoIS.MOD_VERSION).compare(new VersionCompareTool(PageletUpdate.latest));
			} catch(Throwable err)
			{
				changes = "Unable to connect!";
			}
			
			String ts = changes;
			try
			{
				List<String> s = new ArrayList<>();
				for(String ln : changes.split("\n"))
				{
					try
					{
						ln = GoogleTranslate.translate(Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getJavaLocale().getLanguage(), ln);
					} catch(IOException ioe)
					{
					}
					s.add(ln);
				}
				String c = "\u25BA ";
				ts = c + Joiner.on("\n" + c).join(s);
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
		
		if(translated != null)
			fontRenderer.drawSplitString(I18n.format("gui." + InfoIS.MOD_ID + ":nver") + ": " + PageletUpdate.latest + "\n" + I18n.format("gui." + InfoIS.MOD_ID + ":changes") + ": \n" + translated, (int) guiLeft + 12, (int) guiTop + 12, (int) gui1.width - 22, 0xFF_000000);
		else
			GuiNewsBook.spawnLoading(width, height);
		
		GlStateManager.enableDepth();
		
		GL11.glDisable(3089);
		
		int rgb = GuiTheme.CURRENT_THEME.name.equalsIgnoreCase("Vanilla") ? 0x0000FF : GuiTheme.CURRENT_THEME.bodyColor;
		
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
}