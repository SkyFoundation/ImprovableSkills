package com.endie.is.client.gui;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.endie.is.InfoIS;
import com.endie.is.api.PlayerSkillBase;
import com.endie.is.api.PlayerSkillData;
import com.endie.is.api.iGuiSkillDataConsumer;
import com.endie.is.net.PacketLvlDownSkill;
import com.endie.is.net.PacketLvlUpSkill;
import com.pengu.hammercore.bookAPI.fancy.HCFontRenderer;
import com.pengu.hammercore.bookAPI.fancy.HCFontRenderer.iTooltipContext;
import com.pengu.hammercore.client.texture.gui.DynGuiTex;
import com.pengu.hammercore.client.texture.gui.GuiTexBakery;
import com.pengu.hammercore.client.texture.gui.theme.GuiTheme;
import com.pengu.hammercore.client.utils.RenderUtil;
import com.pengu.hammercore.client.utils.UtilsFX;
import com.pengu.hammercore.common.utils.XPUtil;
import com.pengu.hammercore.core.gui.GuiCentered;
import com.pengu.hammercore.net.HCNetwork;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

public class GuiSkillViewer extends GuiCentered implements iGuiSkillDataConsumer, iTooltipContext
{
	final GuiSkillBook parent;
	public PlayerSkillData data;
	public DynGuiTex tex;
	final HCFontRenderer fr;
	final PlayerSkillBase skill;
	
	int mouseX, mouseY;
	
	public GuiSkillViewer(GuiSkillBook parent, PlayerSkillBase skill)
	{
		this.parent = parent;
		this.skill = skill;
		this.data = parent.data;
		this.mc = Minecraft.getMinecraft();
		this.fr = new HCFontRenderer(mc.gameSettings, HCFontRenderer.FONT_NORMAL, mc.renderEngine, true);
		
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
		
		drawDefaultBackground();
		GL11.glColor4f(1, 1, 1, 1);
		
		short nsl = (short) (data.getSkillLevel(skill) + 1);
		int xp = skill.getXPToUpgrade(data, nsl);
		int xp2 = skill.getXPToUpgrade(data, (short) (nsl - 2));
		
		buttonList.get(0).enabled = nsl <= skill.maxLvl && skill.canUpgrade(data);
		buttonList.get(1).enabled = data.getSkillLevel(skill) > 0;
		
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		UtilsFX.bindTexture(InfoIS.MOD_ID, "textures/gui/skills_gui.png");
		GL11.glPushMatrix();
		GL11.glTranslated(guiLeft + (xSize - 20) / 2 + 2, guiTop + 126, 0);
		GL11.glScaled(1.5, 1.5, 1);
		RenderUtil.drawTexturedModalRect(0, 0, 195, 10, 10, 11);
		GL11.glPopMatrix();
		
		drawCenteredString(fontRenderer, I18n.format("text.improvableskills:totalXP", XPUtil.getXPTotal(mc.player)), (int) (guiLeft + xSize / 2), (int) (guiTop + ySize + 2), 0x88FF00);
		
		if(buttonList.get(0).isMouseOver() && buttonList.get(0).enabled)
			drawHoveringText("-" + xp + " XP", mouseX, mouseY);
		
		if(buttonList.get(1).isMouseOver() && buttonList.get(1).enabled)
			drawHoveringText("+" + xp2 + " XP", mouseX, mouseY);
		
		if(buttonList.get(2).isMouseOver())
			drawHoveringText(I18n.format("gui.back"), mouseX, mouseY);
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
		if(keyCode == 1)
			mc.displayGuiScreen(parent);
	}
	
	@Override
	protected void actionPerformed(GuiButton b) throws IOException
	{
		if(b.id == 2)
			mc.displayGuiScreen(parent);
		
		if(b.id == 0)
			HCNetwork.manager.sendToServer(new PacketLvlUpSkill(skill));
		if(b.id == 1)
			HCNetwork.manager.sendToServer(new PacketLvlDownSkill(skill));
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
}