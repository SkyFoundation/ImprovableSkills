package com.zeitheron.improvableskills.client.gui;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.zeitheron.hammercore.bookAPI.fancy.HCFontRenderer;
import com.zeitheron.hammercore.bookAPI.fancy.HCFontRenderer.ITooltipContext;
import com.zeitheron.hammercore.client.gui.GuiCentered;
import com.zeitheron.hammercore.client.utils.RenderUtil;
import com.zeitheron.hammercore.client.utils.TexturePixelGetter;
import com.zeitheron.hammercore.client.utils.UV;
import com.zeitheron.hammercore.client.utils.UtilsFX;
import com.zeitheron.hammercore.client.utils.texture.gui.DynGuiTex;
import com.zeitheron.hammercore.client.utils.texture.gui.GuiTexBakery;
import com.zeitheron.hammercore.client.utils.texture.gui.theme.GuiTheme;
import com.zeitheron.hammercore.net.HCNet;
import com.zeitheron.hammercore.utils.XPUtil;
import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.api.IGuiSkillDataConsumer;
import com.zeitheron.improvableskills.api.registry.PlayerSkillBase;
import com.zeitheron.improvableskills.client.rendering.OnTopEffects;
import com.zeitheron.improvableskills.client.rendering.ote.OTEFadeOutButton;
import com.zeitheron.improvableskills.client.rendering.ote.OTEFadeOutUV;
import com.zeitheron.improvableskills.client.rendering.ote.OTESparkle;
import com.zeitheron.improvableskills.client.rendering.ote.OTETooltip;
import com.zeitheron.improvableskills.net.PacketLvlDownSkill;
import com.zeitheron.improvableskills.net.PacketLvlUpSkill;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class GuiSkillViewer extends GuiCentered implements IGuiSkillDataConsumer, ITooltipContext
{
	final GuiSkillsBook parent;
	public PlayerSkillData data;
	public DynGuiTex tex;
	final HCFontRenderer fr;
	final PlayerSkillBase skill;
	
	int mouseX, mouseY;
	boolean forbidden;
	
	public GuiSkillViewer(GuiSkillsBook parent, PlayerSkillBase skill)
	{
		this.parent = parent;
		this.skill = skill;
		this.data = parent.data;
		this.mc = Minecraft.getMinecraft();
		
		this.fr = new HCFontRenderer(mc.gameSettings, skill.isVisible(parent.data) ? HCFontRenderer.FONT_NORMAL : HCFontRenderer.FONT_GALACTIC, mc.renderEngine, true);
		
		xSize = 200;
		ySize = 150;
	}
	
	@Override
	public void initGui()
	{
		super.initGui();
		parent.initGui();
		
		GuiTexBakery b = GuiTexBakery.start();
		b.body(0, 0, (int) xSize, (int) ySize);
		int ssx = 180;
		int ssy = 80;
		b.slot((int) (xSize - ssx) / 2, (int) (ySize - ssy - 28), ssx, ssy);
		tex = b.bake();
		
		int gl = (int) guiLeft, gt = (int) guiTop;
		
		buttonList.add(new GuiButton(0, gl + 10, gt + 124, 75, 20, I18n.format("button.improvableskills:upgrade")));
		buttonList.add(new GuiButton(1, gl + 116, gt + 124, 75, 20, I18n.format("button.improvableskills:degrade")));
		buttonList.add(new GuiButton(2, gl + (int) (xSize - 20) / 2, gt + 124, 20, 20, " "));
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		
		forbidden = !skill.isVisible(parent.data);
		
		drawDefaultBackground();
		GL11.glColor4f(1, 1, 1, 1);
		
		short nsl = (short) (data.getSkillLevel(skill) + 1);
		int xp = skill.getXPToUpgrade(data, nsl);
		int xp2 = skill.getXPToDowngrade(data, (short) (nsl - 2));
		
		boolean max = nsl <= skill.maxLvl && !forbidden;
		buttonList.get(0).enabled = max && skill.canUpgrade(data);
		buttonList.get(1).enabled = data.getSkillLevel(skill) > 0 && !forbidden;
		
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		UtilsFX.bindTexture(InfoIS.MOD_ID, "textures/gui/skills_gui_overlay.png");
		GL11.glPushMatrix();
		GL11.glTranslated(guiLeft + (xSize - 20) / 2 + 2, guiTop + 126, 0);
		GL11.glScaled(1.5, 1.5, 1);
		RenderUtil.drawTexturedModalRect(0, 0, 195, 10, 10, 11);
		GL11.glPopMatrix();
		
		drawCenteredString(forbidden ? mc.standardGalacticFontRenderer : fontRenderer, I18n.format("text.improvableskills:totalXP", XPUtil.getXPTotal(mc.player)), (int) (guiLeft + xSize / 2), (int) (guiTop + ySize + 2), 0x88FF00);
		
		if(buttonList.get(0).isMouseOver() && max)
			OTETooltip.showTooltip("-" + xp + " XP");
		
		if(buttonList.get(1).isMouseOver() && buttonList.get(1).enabled)
			OTETooltip.showTooltip("+" + xp2 + " XP");
		
		if(buttonList.get(2).isMouseOver())
			OTETooltip.showTooltip(I18n.format("gui.back"));
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		String name = skill.getLocalizedName(data);
		
		GL11.glPushMatrix();
		GL11.glTranslated(guiLeft, guiTop, 0);
		tex.render(0, 0);
		GL11.glColor4f(1, 1, 1, 1);
		skill.tex.toUV(false).render(10, 6, 32, 32);
		int lev = data.getSkillLevel(skill);
		if(lev > 0)
		{
			GL11.glColor4f(1, 1, 1, (float) lev / skill.maxLvl);
			skill.tex.toUV(true).render(10, 6, 32, 32);
			GL11.glColor4f(1, 1, 1, 1);
		}
		
		fontRenderer.drawString(I18n.format("text.improvableskills:level", data.getSkillLevel(skill), skill.maxLvl), 44, 30, GuiTheme.CURRENT_THEME.textShadeColor, false);
		
		double scale = Math.min((xSize - 48) / fontRenderer.getStringWidth(name), 1.5);
		double flh = fontRenderer.FONT_HEIGHT * scale;
		GL11.glTranslated(44, 6 + (24 - flh) / 2, 0);
		GL11.glScaled(scale, scale, 1);
		fontRenderer.drawString(name, 0, 0, GuiTheme.CURRENT_THEME.textShadeColor, false);
		GL11.glPopMatrix();
		
		int maxWid = 176;
		
		fr.drawSplitString(skill.getLocalizedDesc(data), (int) guiLeft + 12, (int) guiTop + 42, maxWid, GuiTheme.CURRENT_THEME.textColor, this, this);
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException
	{
		if(keyCode == 1 || mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode))
			mc.displayGuiScreen(parent);
	}
	
	@Override
	protected void actionPerformed(GuiButton b) throws IOException
	{
		new OTEFadeOutButton(b, b.id == 2 ? 2 : 20);
		
		if(b.id == 2)
		{
			mc.displayGuiScreen(parent);
			new OTEFadeOutUV(new UV(new ResourceLocation(InfoIS.MOD_ID, "textures/gui/skills_gui_overlay.png"), 195, 10, 10, 11), 10 * 1.5, 11 * 1.5, guiLeft + (xSize - 20) / 2 + 2, guiTop + 126, 2);
		}
		
		if(b.id == 0)
		{
			Random r = new Random();
			int[] rgbs = TexturePixelGetter.getAllColors(skill.tex.toUV(true).path + "");
			int col = rgbs[r.nextInt(rgbs.length)];
			double tx = guiLeft + 10 + r.nextInt(64) / 2F;
			double ty = guiTop + 6 + r.nextInt(64) / 2F;
			OnTopEffects.effects.add(new OTESparkle(mouseX, mouseY, tx, ty, 30, col));
			
			HCNet.INSTANCE.sendToServer(new PacketLvlUpSkill(skill));
		}
		
		if(b.id == 1)
		{
			Random r = new Random();
			int[] rgbs = TexturePixelGetter.getAllColors(skill.tex.toUV(true).path + "");
			int col = rgbs[r.nextInt(rgbs.length)];
			double tx = guiLeft + 10 + r.nextInt(64) / 2F;
			double ty = guiTop + 6 + r.nextInt(64) / 2F;
			OnTopEffects.effects.add(new OTESparkle(tx, ty, mouseX, mouseY, 30, col));
			
			HCNet.INSTANCE.sendToServer(new PacketLvlDownSkill(skill));
		}
	}
	
	@Override
	public void applySkillData(PlayerSkillData data)
	{
		this.data = data;
		this.parent.data = data;
	}
	
	@Override
	public int getStartY()
	{
		return 0;
	}
	
	@Override
	public int getStartX()
	{
		return 0;
	}
	
	@Override
	public int getMouseY()
	{
		return mouseY;
	}
	
	@Override
	public int getMouseX()
	{
		return mouseX;
	}
	
	@Override
	public void drawHoveringText(String text, int x, int y)
	{
		drawHoveringText(Arrays.asList(text), x, y, forbidden ? mc.standardGalacticFontRenderer : fontRenderer);
	}
}