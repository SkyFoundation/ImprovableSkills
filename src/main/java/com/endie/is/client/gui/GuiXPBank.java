package com.endie.is.client.gui;

import java.io.IOException;
import java.math.BigInteger;

import org.lwjgl.opengl.GL11;

import com.endie.is.InfoIS;
import com.endie.is.api.PlayerSkillData;
import com.endie.is.api.iGuiSkillDataConsumer;
import com.endie.is.init.SkillsIS;
import com.endie.is.net.PacketDrawXP;
import com.endie.is.net.PacketStoreXP;
import com.pengu.hammercore.client.utils.RenderUtil;
import com.pengu.hammercore.client.utils.UtilsFX;
import com.pengu.hammercore.common.utils.XPUtil;
import com.pengu.hammercore.core.gui.GuiCentered;
import com.pengu.hammercore.net.HCNetwork;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class GuiXPBank extends GuiCentered implements iGuiSkillDataConsumer
{
	public GuiSkillsBook parent;
	public PlayerSkillData data;
	public GuiButton back;
	
	public GuiXPBank(GuiSkillsBook parent)
	{
		this.parent = parent;
		this.data = parent.data;
		xSize = 195;
		ySize = 168;
	}
	
	@Override
	public void initGui()
	{
		super.initGui();
		
		int guiLeft = (int) this.guiLeft;
		int guiTop = (int) this.guiTop;
		
		addButton(back = new GuiButton(0, guiLeft + 20, guiTop + (int) ySize - 36, I18n.format("gui.back") + "    "));
		
		GuiButton btn;
		GuiButton btn1;
		
		addButton(btn1 = btn = new GuiButton(1, guiLeft + 30, guiTop + 39, I18n.format("text." + InfoIS.MOD_ID + ":storeall")));
		btn.width = (fontRenderer.getStringWidth(I18n.format("text." + InfoIS.MOD_ID + ":storeall")) + 8);
		
		GuiButton btn2;
		addButton(btn2 = new GuiButton(3, guiLeft + btn1.width + 44, guiTop + btn.height + 30, I18n.format("text." + InfoIS.MOD_ID + ":draw10lvls")));
		btn2.width = (fontRenderer.getStringWidth(I18n.format("text." + InfoIS.MOD_ID + ":draw10lvls")) + 12);
		
		addButton(btn = new GuiButton(2, guiLeft + btn1.width + 44, guiTop + 28, I18n.format("text." + InfoIS.MOD_ID + ":draw1lvl") + ' '));
		btn.width = btn2.width;
		
		back.width = btn.width;
		back.x = guiLeft + (int) (xSize - back.width) / 2;
	}
	
	@Override
	public void applySkillData(PlayerSkillData data)
	{
		this.data = parent.data = data;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		int guiLeft = (int) this.guiLeft;
		int guiTop = (int) this.guiTop;
		
		drawDefaultBackground();
		GL11.glColor4f(1, 1, 1, 1);
		
		float r = (float) (System.currentTimeMillis() % 2000L) / 2000.0F;
		r = r > 0.5F ? 1.0F - r : r;
		r += 0.45F;
		
		GL11.glEnable(3042);
		
		GlStateManager.disableRescaleNormal();
		net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		
		UtilsFX.bindTexture(InfoIS.MOD_ID, "textures/gui/skills_gui.png");
		
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, (int) xSize, (int) ySize);
		
		int sizeX = 20;
		int sizeY = 26;
		
		GL11.glPushMatrix();
		GL11.glColor3f(0.8F, 0.8F, 0.8F);
		drawTexturedModalRect(guiLeft + sizeX, guiTop + sizeY - 9, sizeX, sizeY, (int) xSize - sizeX * 2, (int) ySize - sizeY * 2 - 4);
		GL11.glPopMatrix();
		
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		UtilsFX.bindTexture(InfoIS.MOD_ID, "textures/gui/skills_gui.png");
		GL11.glPushMatrix();
		GL11.glTranslated(back.x + back.width - 18, guiTop + 133.85, 0);
		GL11.glScaled(1.45, 1.45, 1);
		RenderUtil.drawTexturedModalRect(0, 0, 195, 10, 10, 11);
		GL11.glPopMatrix();
		
		String form = TextFormatting.BLACK + I18n.format(SkillsIS.XP_STORAGE.getLocalizedName(data)) + ": " + TextFormatting.RESET.toString() + data.storageXp + " XP";
		fontRenderer.drawString(form, guiLeft + ((int) xSize - fontRenderer.getStringWidth(form)) / 2, guiTop + 8, 4161280);
		GL11.glColor3f(1.0F, 1.0F, 1.0F);
		
		mc.getTextureManager().bindTexture(ICONS);
		
		double bx = xSize / 2 - 72.8D;
		double by = ySize - 50;
		BigInteger i = data.storageXp;
		int xp = i.intValue();
		float current = XPUtil.getCurrentFromXPValue(xp);
		
		GL11.glPushMatrix();
		GL11.glTranslated(guiLeft + bx, guiTop + by, 0.0D);
		GL11.glScaled(0.8D, 0.8D, 0.8D);
		RenderUtil.drawTexturedModalRect(0.0D, 0.0D, 0.0D, 64.0D, 182.0D, 5.0D);
		RenderUtil.drawTexturedModalRect(0.0D, 0.0D, 0.0D, 69.0D, 182.0F * current, 5.0D);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glTranslated(guiLeft + bx, guiTop + by, 0.0D);
		GL11.glScaled(1.1D, 1.1D, 1.1D);
		int lvl = XPUtil.getLevelFromXPValue(xp);
		String text = (lvl < 0 ? "TOO MUCH!!!" : Integer.valueOf(lvl)) + "";
		GL11.glColor3f(0.24705882F, 0.49803922F, 0.0F);
		fontRenderer.drawString(text, (145.6F - fontRenderer.getStringWidth(text)) / 2.0F * 0.9F, -8.0F, 4161280, false);
		GL11.glColor3f(1.0F, 1.0F, 1.0F);
		GL11.glPopMatrix();
		
		drawCenteredString(fontRenderer, I18n.format("text." + InfoIS.MOD_ID + ":totalXP", XPUtil.getXPTotal(mc.player)), guiLeft + (int) xSize / 2, guiTop + (int) ySize + 4, (int) (r * 255.0F) << 16 | 0xFF00 | 0x0);
		GL11.glColor3f(1.0F, 1.0F, 1.0F);
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException
	{
		if(keyCode == 1 || mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode))
			mc.displayGuiScreen(parent);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		int id = button.id;
		
		if(id == 0)
			mc.displayGuiScreen(parent);
		
		else if(id == 1)
			HCNetwork.manager.sendToServer(new PacketStoreXP(XPUtil.getXPTotal(mc.player)));
		else if(id == 2)
			HCNetwork.manager.sendToServer(new PacketDrawXP(XPUtil.getXPValueToNextLevel(XPUtil.getLevelFromXPValue(XPUtil.getXPTotal(mc.player)))));
		else if(id == 3)
		{
			int xpLvl = XPUtil.getLevelFromXPValue(XPUtil.getXPTotal(mc.player));
			int xp = 0;
			for(int i = 0; i < 10; i++)
				xp += XPUtil.getXPValueToNextLevel(xpLvl + i);
			HCNetwork.manager.sendToServer(new PacketDrawXP(xp));
		}
	}
}