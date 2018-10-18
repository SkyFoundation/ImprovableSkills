package com.zeitheron.improvableskills.client.gui;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import com.zeitheron.hammercore.client.utils.GLImageManager;
import com.zeitheron.hammercore.client.utils.RenderUtil;
import com.zeitheron.hammercore.client.utils.UV;
import com.zeitheron.hammercore.client.utils.texture.gui.theme.GuiTheme;
import com.zeitheron.hammercore.lib.zlib.utils.Threading;
import com.zeitheron.hammercore.lib.zlib.web.HttpRequest;
import com.zeitheron.hammercore.utils.FinalFieldHelper;
import com.zeitheron.hammercore.utils.color.ColorHelper;
import com.zeitheron.improvableskills.ImprovableSkillsMod;
import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.client.gui.base.GuiTabbable;
import com.zeitheron.improvableskills.custom.pagelets.PageletUpdate;
import com.zeitheron.improvableskills.init.PageletsIS;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;

public class GuiDiscord extends GuiTabbable
{
	public static final Integer DISCORD_SERVER_ID_TEXTURE = null;
	private static long lastReload;
	private static long lastRequest;
	
	public final UV gui1;
	
	public static int getDiscordServerIdTexture()
	{
		lastRequest = System.currentTimeMillis();
		
		if(DISCORD_SERVER_ID_TEXTURE == null)
		{
			FinalFieldHelper.setStaticFinalField(GuiDiscord.class, "DISCORD_SERVER_ID_TEXTURE", (Integer) GlStateManager.generateTexture());
			
			if(DISCORD_SERVER_ID_TEXTURE != null)
				Threading.createAndStart("IS3ZD", () ->
				{
					while(true)
					{
						try
						{
							Thread.sleep(1000L);
						} catch(InterruptedException e)
						{
							e.printStackTrace();
						}
						
						long now = System.currentTimeMillis();
						
						if(now - lastRequest < 5000L)
						{
							lastRequest = 0;
							lastReload -= 15000L;
						}
						
						if(now - lastReload >= 15000L)
						{
							try(InputStream in = HttpRequest.get("https://drive.google.com/uc?export=download&id=1DX-Npc2mu6tz6gBXoMsRtypfYTjbRt4q").userAgent("ImprovableSkills3").connectTimeout(10000).stream())
							{
								BufferedImage img = ImageIO.read(in);
								if(img != null)
									Minecraft.getMinecraft().addScheduledTask(() -> GLImageManager.loadTexture(img, DISCORD_SERVER_ID_TEXTURE, false));
							} catch(Throwable err)
							{
								err.printStackTrace();
							}
							
							lastReload = now;
						}
					}
				});
		}
		return DISCORD_SERVER_ID_TEXTURE != null ? DISCORD_SERVER_ID_TEXTURE.intValue() : 0;
	}
	
	public GuiDiscord()
	{
		super(PageletsIS.DISCORD);
		
		xSize = 195;
		ySize = 168;
		
		gui1 = new UV(new ResourceLocation(InfoIS.MOD_ID, "textures/gui/skills_gui_paper.png"), 0, 0, xSize, ySize);
		
		getDiscordServerIdTexture();
	}
	
	@Override
	protected void drawBack(float partialTicks, int mouseX, int mouseY)
	{
		GL11.glColor4f(1, 1, 1, 1);
		gui1.render(guiLeft, guiTop);
		
		boolean mouse = mouseX >= guiLeft + (xSize - 3 * xSize / 3.5) / 2 && mouseY >= guiTop + (ySize - xSize / 3.5) / 2 && mouseX < guiLeft + (xSize - 3 * xSize / 3.5) / 2 + 3 * xSize / 3.5 && mouseY < guiTop + (ySize - xSize / 3.5) / 2 + xSize / 3.5;
		
		GlStateManager.bindTexture(DISCORD_SERVER_ID_TEXTURE != null ? DISCORD_SERVER_ID_TEXTURE.intValue() : 0);
		GlStateManager.color(mouse ? .9F : 1, mouse ? .9F : 1, 1, 1);
		RenderUtil.drawFullTexturedModalRect(guiLeft + (xSize - 3 * xSize / 3.5) / 2, guiTop + (ySize - xSize / 3.5) / 2, 3 * xSize / 3.5, xSize / 3.5);
		
		int rgb = GuiTheme.CURRENT_THEME.name.equalsIgnoreCase("Vanilla") ? 0x0000FF : GuiTheme.CURRENT_THEME.bodyColor;
		ColorHelper.gl(255 << 24 | rgb);
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, 5);
		gui2.render(guiLeft, guiTop);
		GlStateManager.popMatrix();
		
		if(System.currentTimeMillis() - lastRequest > 120_000L)
			lastRequest = System.currentTimeMillis();
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		boolean mouse = mouseX >= guiLeft + (xSize - 3 * xSize / 3.5) / 2 && mouseY >= guiTop + (ySize - xSize / 3.5) / 2 && mouseX < guiLeft + (xSize - 3 * xSize / 3.5) / 2 + 3 * xSize / 3.5 && mouseY < guiTop + (ySize - xSize / 3.5) / 2 + xSize / 3.5;
		
		if(mouse)
		{
			openInviteLink(PageletUpdate.discord);
			mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
		}
		
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	public static void openInviteLink(String inviteCode)
	{
		GuiScreen parent = Minecraft.getMinecraft().currentScreen;
		
		String url = "https://discord.gg/" + inviteCode;
		
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
}