package com.endie.is.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.endie.is.InfoIS;
import com.endie.is.api.PlayerSkillBase;
import com.endie.is.api.PlayerSkillData;
import com.endie.is.api.SkillTex;
import com.endie.is.api.iGuiSkillDataConsumer;
import com.endie.is.init.SkillsIS;
import com.pengu.hammercore.client.UV;
import com.pengu.hammercore.core.gui.GuiCentered;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class GuiSkillBook extends GuiCentered implements iGuiSkillDataConsumer
{
	public final UV gui, star;
	public double scrolledPixels;
	public double prevScrolledPixels;
	public int row = 6;
	
	public int cHover, cHoverTime;
	
	public PlayerSkillData data;
	public List<SkillTex> texes = new ArrayList<>();
	
	public GuiSkillBook(PlayerSkillData data)
	{
		this.data = data;
		
		xSize = 195;
		ySize = 168;
		
		gui = new UV(new ResourceLocation(InfoIS.MOD_ID, "textures/gui/skills_gui.png"), 0, 0, xSize, ySize);
		star = new UV(new ResourceLocation(InfoIS.MOD_ID, "textures/gui/skills_gui.png"), xSize, 0, 10, 10);
		
		GameRegistry.findRegistry(PlayerSkillBase.class).forEach(skill -> texes.add(skill.tex));
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		drawDefaultBackground();
		GL11.glColor4f(1, 1, 1, 1);
		gui.render(guiLeft, guiTop);
		
		GlStateManager.enableDepth();
		
		int co = texes.size();
		
		ScaledResolution sr = new ScaledResolution(mc);
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(3089);
		GL11.glScissor((int) Math.ceil(guiLeft * sr.getScaleFactor()), (int) Math.ceil((guiTop + 14) * sr.getScaleFactor()), (int) Math.ceil(xSize * sr.getScaleFactor()), (int) Math.ceil((ySize - 21) * sr.getScaleFactor()));
		
		boolean singleHover = false;
		
		for(int i = 0; i < co; ++i)
		{
			int j = i % co;
			SkillTex tex = texes.get(j);
			
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
				if(cHover != i)
					cHoverTime = 0;
				cHover = i;
				
				singleHover = true;
				
				cHoverTime = Math.min(cHoverTime + 25, 255);
				
				UV norm = tex.toUV(false);
				UV hov = tex.toUV(true);
				
				norm.render(x, y, 24, 24);
				
				GL11.glColor4f(1, 1, 1, cHoverTime / 255F);
				hov.render(x, y, 24, 24);
				GL11.glColor4f(1, 1, 1, 1);
			} else if(cHoverTime > 0 && cHover == i)
			{
				cHoverTime = Math.max(cHoverTime - 25, 0);
				
				UV norm = tex.toUV(false);
				UV hov = tex.toUV(true);
				
				norm.render(x, y, 24, 24);
				
				GL11.glColor4f(1, 1, 1, cHoverTime / 255F);
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
		
		if(cHover >= 0 && cHoverTime >= 200)
		{
			GL11.glPushMatrix();
			GL11.glTranslated(0, 0, 500);
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
		
		int co = texes.size();
		float maxPixels = 28 * (co / row) - 28 * 7;
		
		int dw = Mouse.getDWheel();
		
		if(dw != 0)
		{
			scrolledPixels -= dw / 15F;
			scrolledPixels = Math.max(Math.min(scrolledPixels, maxPixels), 0);
		}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		if(cHover >= 0 && cHoverTime >= 50)
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
	public void applySkillData(PlayerSkillData data)
	{
		this.data = data;
	}
}