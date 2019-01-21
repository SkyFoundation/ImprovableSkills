package com.zeitheron.improvableskills.client.gui;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;

import com.zeitheron.hammercore.client.utils.GLImageManager;
import com.zeitheron.hammercore.client.utils.RenderUtil;
import com.zeitheron.hammercore.client.utils.UV;
import com.zeitheron.hammercore.client.utils.texture.gui.theme.GuiTheme;
import com.zeitheron.hammercore.lib.zlib.utils.Threading;
import com.zeitheron.hammercore.lib.zlib.web.HttpRequest;
import com.zeitheron.hammercore.utils.FinalFieldHelper;
import com.zeitheron.hammercore.utils.color.ColorHelper;
import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.client.gui.base.GuiTabbable;
import com.zeitheron.improvableskills.client.rendering.ote.OTEConfetti;
import com.zeitheron.improvableskills.custom.pagelets.PageletUpdate;
import com.zeitheron.improvableskills.init.PageletsIS;
import com.zeitheron.improvableskills.init.SoundsIS;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class GuiDiscord extends GuiTabbable
{
	public static final Integer DISCORD_SERVER_ID_TEXTURE = null;
	public static boolean texureLoaded = false;
	private static long lastReload;
	private static boolean requested;
	
	public final UV gui1;
	
	public static int getDiscordServerIdTexture()
	{
		requested = true;
		
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
						
						if(now - lastReload >= 180_000L || requested)
						{
							try(InputStream in = HttpRequest.get("http://dccg.herokuapp.com/zmc").userAgent("ImprovableSkills3").connectTimeout(10000).stream())
							{
								BufferedImage img = ImageIO.read(in);
								if(img != null)
									Minecraft.getMinecraft().addScheduledTask(() ->
									{
										GLImageManager.loadTexture(img, DISCORD_SERVER_ID_TEXTURE, false);
										if(!texureLoaded)
											texureLoaded = true;
									});
							} catch(Throwable err)
							{
								err.printStackTrace();
							}
							
							lastReload = now;
							requested = false;
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
	
	public int hoverTime;
	public boolean hovered;
	
	@Override
	public void updateScreen()
	{
		super.updateScreen();
		if(hovered && hoverTime < 10)
			++hoverTime;
		if(!hovered && hoverTime > 0)
			--hoverTime;
	}
	
	@Override
	protected void drawBack(float partialTicks, int mouseX, int mouseY)
	{
		GL11.glColor4f(1, 1, 1, 1);
		gui1.render(guiLeft, guiTop);
		
		boolean mouse = hovered = mouseX >= guiLeft + (xSize - 3 * xSize / 3.5) / 2 && mouseY >= guiTop + (ySize - xSize / 3.5) - 22 && mouseX < guiLeft + (xSize - 3 * xSize / 3.5) / 2 + 3 * xSize / 3.5 && mouseY < guiTop + (ySize - xSize / 3.5) - 22 + xSize / 3.5;
		
		GlStateManager.bindTexture(DISCORD_SERVER_ID_TEXTURE != null ? DISCORD_SERVER_ID_TEXTURE.intValue() : 0);
		
		if(texureLoaded)
		{
			float m = .67F + .33F * OTEConfetti.sineF(hoverTime / 10F);
			GlStateManager.color(m, m, m);
			RenderUtil.drawFullTexturedModalRect(guiLeft + (xSize - 3 * xSize / 3.5) / 2, guiTop + (ySize - xSize / 3.5) - 22, 3 * xSize / 3.5, xSize / 3.5);
			fontRenderer.drawSplitString(I18n.format("pagelet." + InfoIS.MOD_ID + ":discord2"), (int) guiLeft + 13, (int) guiTop + 12, (int) xSize - 21, 0);
		} else
			GuiNewsBook.spawnLoading(width, height);
		
		int rgb = GuiTheme.CURRENT_THEME.name.equalsIgnoreCase("Vanilla") ? 0x0088FF : GuiTheme.CURRENT_THEME.bodyColor;
		ColorHelper.gl(255 << 24 | rgb);
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, 5);
		gui2.render(guiLeft, guiTop);
		GlStateManager.popMatrix();
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		boolean mouse = mouseX >= guiLeft + (xSize - 3 * xSize / 3.5) / 2 && mouseY >= guiTop + (ySize - xSize / 3.5) - 22 && mouseX < guiLeft + (xSize - 3 * xSize / 3.5) / 2 + 3 * xSize / 3.5 && mouseY < guiTop + (ySize - xSize / 3.5) - 22 + xSize / 3.5;
		
		if(mouse)
		{
			openInviteLink(PageletUpdate.discord);
			mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundsIS.CONNECT, 1.0F));
		}
		
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	public static void openInviteLink(String inviteCode)
	{
		Sys.openURL("https://discord.gg/" + inviteCode);
	}
}