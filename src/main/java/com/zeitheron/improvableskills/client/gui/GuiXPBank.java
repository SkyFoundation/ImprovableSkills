package com.zeitheron.improvableskills.client.gui;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.zeitheron.hammercore.client.gui.GuiCentered;
import com.zeitheron.hammercore.client.utils.RenderUtil;
import com.zeitheron.hammercore.client.utils.UV;
import com.zeitheron.hammercore.client.utils.UtilsFX;
import com.zeitheron.hammercore.client.utils.texture.gui.theme.GuiTheme;
import com.zeitheron.hammercore.net.HCNet;
import com.zeitheron.hammercore.utils.XPUtil;
import com.zeitheron.hammercore.utils.color.ColorHelper;
import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.IGuiSkillDataConsumer;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.api.registry.PageletBase;
import com.zeitheron.improvableskills.client.gui.base.GuiCustomButton;
import com.zeitheron.improvableskills.client.gui.base.GuiTabbable;
import com.zeitheron.improvableskills.client.rendering.OnTopEffects;
import com.zeitheron.improvableskills.client.rendering.ote.OTEFadeOutButton;
import com.zeitheron.improvableskills.client.rendering.ote.OTEFadeOutUV;
import com.zeitheron.improvableskills.client.rendering.ote.OTEXpOrb;
import com.zeitheron.improvableskills.data.PlayerDataManager;
import com.zeitheron.improvableskills.init.PageletsIS;
import com.zeitheron.improvableskills.init.SkillsIS;
import com.zeitheron.improvableskills.init.SoundsIS;
import com.zeitheron.improvableskills.net.PacketDrawXP;
import com.zeitheron.improvableskills.net.PacketStoreXP;
import com.zeitheron.improvableskills.proxy.SyncSkills;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class GuiXPBank extends GuiTabbable implements IGuiSkillDataConsumer
{
	public PlayerSkillData data;
	public double targetXP_X, targetXP_Y;
	public float currentXP, prevXP;
	
	public GuiXPBank(PageletBase pagelet)
	{
		super(pagelet);
		this.data = SyncSkills.getData();
		xSize = 195;
		ySize = 168;
	}
	
	@Override
	public void initGui()
	{
		super.initGui();
		
		int guiLeft = (int) this.guiLeft;
		int guiTop = (int) this.guiTop;
		
		GuiButton btn;
		GuiButton btn1;
		
		addButton(btn1 = btn = new GuiCustomButton(1, guiLeft + 30, guiTop + 39, 100, 20, I18n.format("text." + InfoIS.MOD_ID + ":storeall")).setCustomClickSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP));
		btn.width = (fontRenderer.getStringWidth(I18n.format("text." + InfoIS.MOD_ID + ":storeall")) + 8);
		
		GuiButton btn2;
		addButton(btn2 = new GuiCustomButton(3, guiLeft + btn1.width + 44, guiTop + btn.height + 30, 100, 20, I18n.format("text." + InfoIS.MOD_ID + ":draw10lvls")).setCustomClickSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP));
		btn2.width = (fontRenderer.getStringWidth(I18n.format("text." + InfoIS.MOD_ID + ":draw10lvls")) + 12);
		
		addButton(btn = new GuiCustomButton(2, guiLeft + btn1.width + 44, guiTop + 28, 100, 20, I18n.format("text." + InfoIS.MOD_ID + ":draw1lvl") + ' ').setCustomClickSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP));
		btn.width = btn2.width;
	}
	
	@Override
	public void applySkillData(PlayerSkillData data)
	{
		this.data = data;
	}
	
	@Override
	public void updateScreen()
	{
		super.updateScreen();
		
		BigInteger i = data.storageXp;
		int xp = i.intValue();
		float current = XPUtil.getCurrentFromXPValue(xp);
		
		if(this.currentXP == 0F && this.prevXP == 0F)
			this.currentXP = this.prevXP = current;
		else
		{
			this.prevXP = this.currentXP;
			this.currentXP = current;
		}
	}
	
	@Override
	protected void drawBack(float partialTicks, int mouseX, int mouseY)
	{
		GL11.glColor4f(1, 1, 1, 1);
		gui1.render(guiLeft, guiTop);
		
		int guiLeft = (int) this.guiLeft;
		int guiTop = (int) this.guiTop;
		
		int sizeX = 20;
		int sizeY = 26;
		
		GL11.glPushMatrix();
		GL11.glColor3f(0.8F, 0.8F, 0.8F);
		drawTexturedModalRect(guiLeft + sizeX, guiTop + sizeY - 9, sizeX, sizeY, (int) xSize - sizeX * 2, (int) ySize - sizeY * 2 - 4 + 14);
		GL11.glPopMatrix();
		
		String form = TextFormatting.BLACK + PageletsIS.XP_STORAGE.getTitle().getUnformattedComponentText() + ": " + TextFormatting.RESET.toString() + data.storageXp + " XP";
		fontRenderer.drawString(form, guiLeft + ((int) xSize - fontRenderer.getStringWidth(form)) / 2, guiTop + 8, 4161280);
		GL11.glColor3f(1.0F, 1.0F, 1.0F);
		
		mc.getTextureManager().bindTexture(ICONS);
		
		double bx = xSize / 2 - 72.8D;
		double by = ySize - 50;
		BigInteger i = data.storageXp;
		int xp = i.intValue();
		
		GL11.glPushMatrix();
		GL11.glTranslated(targetXP_X = guiLeft + bx, targetXP_Y = guiTop + by + 18, 0.0D);
		GL11.glScaled(0.8D, 0.8D, 0.8D);
		RenderUtil.drawTexturedModalRect(0.0D, 0.0D, 0.0D, 64.0D, 182.0D, 5.0D);
		RenderUtil.drawTexturedModalRect(0.0D, 0.0D, 0.0D, 69.0D, 182.0F * (prevXP + (currentXP - prevXP) * partialTicks), 5.0D);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glTranslated(guiLeft + bx, guiTop + by + 18, 0.0D);
		GL11.glScaled(1.1D, 1.1D, 1.1D);
		int lvl = XPUtil.getLevelFromXPValue(xp);
		String text = (lvl < 0 ? "TOO MUCH!!!" : Integer.valueOf(lvl)) + "";
		GL11.glColor3f(0.24705882F, 0.49803922F, 0.0F);
		fontRenderer.drawString(text, (145.6F - fontRenderer.getStringWidth(text)) / 2.0F * 0.9F, -8.0F, 4161280, false);
		GL11.glColor3f(1.0F, 1.0F, 1.0F);
		GL11.glPopMatrix();
		
		float r = (float) (System.currentTimeMillis() % 2000L) / 2000.0F;
		r = r > 0.5F ? 1.0F - r : r;
		r += 0.45F;
		
		drawCenteredString(fontRenderer, I18n.format("text." + InfoIS.MOD_ID + ":totalXP", XPUtil.getXPTotal(mc.player)), guiLeft + (int) xSize / 2, guiTop + (int) ySize + 4, (int) (r * 255.0F) << 16 | 0xFF00 | 0x0);
		GL11.glColor3f(1.0F, 1.0F, 1.0F);
		
		int rgb = GuiTheme.CURRENT_THEME.name.equalsIgnoreCase("Vanilla") ? 0x0088FF : GuiTheme.CURRENT_THEME.bodyColor;
		
		ColorHelper.gl(255 << 24 | rgb);
		gui2.render(this.guiLeft, this.guiTop);
		GL11.glColor4f(1, 1, 1, 1);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		int guiLeft = (int) this.guiLeft;
		int guiTop = (int) this.guiTop;
		
		GL11.glColor4f(1, 1, 1, 1);
		
		float r = (float) (System.currentTimeMillis() % 2000L) / 2000.0F;
		r = r > 0.5F ? 1.0F - r : r;
		r += 0.45F;
		
		GL11.glEnable(3042);
		
		GlStateManager.disableRescaleNormal();
		net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException
	{
		if(keyCode == 1 || mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode))
			mc.displayGuiScreen(parent);
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		int id = button.id;
		
		new OTEFadeOutButton(button, id == 0 ? 2 : 20);
		
		if(id == 0)
			mc.displayGuiScreen(parent);
		
		else if(id == 1)
		{
			Random rand = new Random();
			
			int lvls = mc.player.experienceLevel;
			
			for(int i = 0; i < Math.min(100, lvls); ++i)
			{
				double rtx = button.x + button.width / 2F + (rand.nextFloat() - rand.nextFloat()) * (button.width / 2F);
				double rty = button.y + button.height / 2F + (rand.nextFloat() - rand.nextFloat()) * (button.height / 2F);
				
				rtx = targetXP_X + rand.nextFloat() * 182F * .8F;
				rty = targetXP_Y + rand.nextFloat() * 5F;
				
				double rx = guiLeft + xSize / 2 + (rand.nextFloat() - rand.nextFloat()) * 30;
				double ry = guiTop + ySize + 4 + rand.nextFloat() * fontRenderer.FONT_HEIGHT;
				
				OnTopEffects.effects.add(new OTEXpOrb(rx, ry, rtx, rty, 40));
			}
			
			HCNet.INSTANCE.sendToServer(new PacketStoreXP(XPUtil.getXPTotal(mc.player)));
		} else if(id == 2)
		{
			HCNet.INSTANCE.sendToServer(new PacketDrawXP(XPUtil.getXPValueToNextLevel(XPUtil.getLevelFromXPValue(XPUtil.getXPTotal(mc.player)))));
			
			if(data.storageXp.longValue() > 0L)
			{
				Random rand = new Random();
				
				double rx = button.x + button.width / 2F + (rand.nextFloat() - rand.nextFloat()) * (button.width / 2F);
				double ry = button.y + button.height / 2F + (rand.nextFloat() - rand.nextFloat()) * (button.height / 2F);
				
				BigInteger i = data.storageXp;
				int xp = i.intValue();
				float current = XPUtil.getCurrentFromXPValue(xp);
				
				rx = targetXP_X + rand.nextFloat() * 182F * current * .8F;
				ry = targetXP_Y + rand.nextFloat() * 5F;
				
				double rtx = guiLeft + xSize / 2 + (rand.nextFloat() - rand.nextFloat()) * 30;
				double rty = guiTop + ySize + 4 + rand.nextFloat() * fontRenderer.FONT_HEIGHT;
				
				OnTopEffects.effects.add(new OTEXpOrb(rx, ry, rtx, rty, 40));
			}
		} else if(id == 3)
		{
			Random rand = new Random();
			
			int xpLvl = XPUtil.getLevelFromXPValue(XPUtil.getXPTotal(mc.player));
			int xp = 0;
			for(int i = 0; i < 10; i++)
			{
				BigInteger i0 = data.storageXp;
				int xp0 = i0.intValue();
				float current = XPUtil.getCurrentFromXPValue(xp0);
				
				if(data.storageXp.longValue() > 0L)
				{
					double rx = button.x + button.width / 2F + (rand.nextFloat() - rand.nextFloat()) * (button.width / 2F);
					double ry = button.y + button.height / 2F + (rand.nextFloat() - rand.nextFloat()) * (button.height / 2F);
					
					rx = targetXP_X + rand.nextFloat() * 182F * current * .8F;
					ry = targetXP_Y + rand.nextFloat() * 5F;
					
					double rtx = guiLeft + xSize / 2 + (rand.nextFloat() - rand.nextFloat()) * 30;
					double rty = guiTop + ySize + 4 + rand.nextFloat() * fontRenderer.FONT_HEIGHT * .8;
					
					OnTopEffects.effects.add(new OTEXpOrb(rx, ry, rtx, rty, 40));
				}
				
				xp += XPUtil.getXPValueToNextLevel(xpLvl + i);
			}
			
			HCNet.INSTANCE.sendToServer(new PacketDrawXP(xp));
		}
	}
}