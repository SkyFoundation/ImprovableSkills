package com.endie.is.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.endie.is.InfoIS;
import com.endie.is.api.PlayerSkillBase;
import com.endie.is.api.PlayerSkillData;
import com.endie.is.api.SkillTex;
import com.endie.is.api.iGuiSkillDataConsumer;
import com.endie.is.init.SkillsIS;
import com.endie.lib.tuple.TwoTuple;
import com.pengu.hammercore.client.UV;
import com.pengu.hammercore.client.texture.gui.theme.GuiTheme;
import com.pengu.hammercore.core.gui.GuiCentered;
import com.pengu.hammercore.utils.ColorHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class GuiSkillsBook extends GuiCentered implements iGuiSkillDataConsumer
{
	public final UV gui1, gui2, star;
	public double scrolledPixels;
	public double prevScrolledPixels;
	public int row = 6;
	
	public Map<SkillTex, TwoTuple.Atomic<Integer, Integer>> hoverAnims = new HashMap<>();
	
	public int cHover;
	
	public PlayerSkillData data;
	public List<SkillTex> texes = new ArrayList<>();
	
	public GuiScreen parent;
	
	public GuiSkillsBook(PlayerSkillData data)
	{
		this.parent = Minecraft.getMinecraft().currentScreen;
		
		while(this.parent instanceof GuiSkillsBook)
			this.parent = ((GuiSkillsBook) this.parent).parent;
		
		this.data = data;
		
		xSize = 195;
		ySize = 168;
		
		gui1 = new UV(new ResourceLocation(InfoIS.MOD_ID, "textures/gui/skills_gui_paper.png"), 0, 0, xSize, ySize);
		gui2 = new UV(new ResourceLocation(InfoIS.MOD_ID, "textures/gui/skills_gui_overlay.png"), 0, 0, xSize, ySize);
		star = new UV(new ResourceLocation(InfoIS.MOD_ID, "textures/gui/skills_gui_overlay.png"), xSize, 0, 10, 10);
		
		GameRegistry.findRegistry(PlayerSkillBase.class).forEach(skill -> texes.add(skill.tex));
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		drawDefaultBackground();
		GL11.glColor4f(1, 1, 1, 1);
		gui1.render(guiLeft, guiTop);
		
		GlStateManager.enableDepth();
		
		int co = texes.size();
		
		ScaledResolution sr = new ScaledResolution(mc);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(3089);
		GL11.glScissor((int) Math.ceil(guiLeft * sr.getScaleFactor()), (int) Math.ceil((guiTop + 5) * sr.getScaleFactor()), (int) Math.ceil(xSize * sr.getScaleFactor()), (int) Math.ceil((ySize - 10) * sr.getScaleFactor()));
		
		int cht = 0, chtni = 0;
		boolean singleHover = false;
		
		for(int i = 0; i < co; ++i)
		{
			int j = i % co;
			SkillTex tex = texes.get(j);
			
			TwoTuple.Atomic<Integer, Integer> hovt = hoverAnims.get(tex);
			if(hovt == null)
				hoverAnims.put(tex, hovt = new TwoTuple.Atomic<>(0, 0));
			
			int cHoverTime = hovt.get1();
			int cHoverTimePrev = hovt.get2();
			
			double x = (i % row) * 28 + guiLeft + 16;
			double y = (i / row) * 28 - (prevScrolledPixels + (scrolledPixels - prevScrolledPixels) * partialTicks);
			
			if(y < -24)
				continue;
			
			if(y > ySize - 14)
				break;
			
			y += guiTop + 9;
			
			boolean hover = mouseX >= x && mouseX < x + 24 && mouseY >= y && mouseY < y + 24;
			
			if(hover)
			{
				cHover = i;
				singleHover = true;
				
				chtni = cHoverTime;
			}
			
			if(cHoverTime > 0)
			{
				cht = (int) (cHoverTimePrev + (cHoverTime - cHoverTimePrev) * partialTicks);
				
				UV norm = tex.toUV(false);
				UV hov = tex.toUV(true);
				
				norm.render(x, y, 24, 24);
				
				GL11.glColor4f(1, 1, 1, cht / 255F);
				hov.render(x, y, 24, 24);
				GL11.glColor4f(1, 1, 1, 1);
			} else
				tex.toUV(false).render(x, y, 24, 24);
			
			if(tex.skill != SkillsIS.XP_STORAGE && data.getSkillLevel(tex.skill) >= tex.skill.maxLvl)
				star.render(x + 14, y + 14, 10, 10);
		}
		
		if(!singleHover)
			cHover = -1;
		
		GL11.glDisable(3089);
		
		int rgb = GuiTheme.CURRENT_THEME.bodyColor;
		if(GuiTheme.CURRENT_THEME.name.equalsIgnoreCase("Vanilla"))
			GL11.glColor4f(0, 0, 1, 1);
		else
			GL11.glColor4f(ColorHelper.getRed(rgb), ColorHelper.getGreen(rgb), ColorHelper.getBlue(rgb), 1);
		gui2.render(guiLeft, guiTop);
		GL11.glColor4f(1, 1, 1, 1);
		
		if(cHover >= 0 && chtni >= 200)
		{
			GL11.glPushMatrix();
			GL11.glTranslatef(0, 0, 500);
			drawHoveringText(texes.get(cHover % co).skill.getLocalizedName(data), mouseX, mouseY);
			GL11.glPopMatrix();
		}
		
		GL11.glDisable(GL11.GL_BLEND);
		GlStateManager.disableDepth();
	}
	
	@Override
	public void updateScreen()
	{
		super.updateScreen();
		
		prevScrolledPixels = scrolledPixels;
		
		boolean singleHover = false;
		
		int co = texes.size();
		float maxPixels = 28 * (co / row) - 28 * 7;
		
		int dw = Mouse.getDWheel();
		
		if(dw != 0)
		{
			scrolledPixels -= dw / 15F;
			scrolledPixels = Math.max(Math.min(scrolledPixels, maxPixels), 0);
		}
		
		for(int i = 0; i < co; ++i)
		{
			int j = i % co;
			SkillTex tex = texes.get(j);
			
			TwoTuple.Atomic<Integer, Integer> hovt = hoverAnims.get(tex);
			if(hovt == null)
				hoverAnims.put(tex, hovt = new TwoTuple.Atomic<>(0, 0));
			
			int cHoverTime = hovt.get1();
			int pht = cHoverTime;
			
			if(cHover == i)
				cHoverTime = Math.min(cHoverTime + 55, 255);
			else
				cHoverTime = Math.max(cHoverTime - 15, 0);
			
			hovt.set(cHoverTime, pht);
		}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		if(cHover >= 0)
		{
			PlayerSkillBase skill = texes.get(cHover % texes.size()).skill;
			if(skill == SkillsIS.XP_STORAGE)
				mc.displayGuiScreen(new GuiXPBank(this));
			else
				mc.displayGuiScreen(new GuiSkillViewer(this, skill));
			mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1F));
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
	
	@Override
	public void applySkillData(PlayerSkillData data)
	{
		this.data = data;
	}
}