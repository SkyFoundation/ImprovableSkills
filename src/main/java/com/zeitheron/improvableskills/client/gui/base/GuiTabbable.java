package com.zeitheron.improvableskills.client.gui.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.zeitheron.hammercore.client.gui.GuiCentered;
import com.zeitheron.hammercore.client.utils.RenderUtil;
import com.zeitheron.hammercore.client.utils.UV;
import com.zeitheron.hammercore.client.utils.UtilsFX;
import com.zeitheron.hammercore.client.utils.texture.gui.theme.GuiTheme;
import com.zeitheron.hammercore.lib.zlib.tuple.TwoTuple;
import com.zeitheron.hammercore.utils.color.ColorHelper;
import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.Pagelet;
import com.zeitheron.improvableskills.client.gui.GuiSkillsBook;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public class GuiTabbable extends GuiCentered
{
	public final UV gui1, gui2, star;
	public double scrolledPixels;
	public double prevScrolledPixels;
	public int row = 6;
	
	public Map<ResourceLocation, TwoTuple.Atomic<Integer, Integer>> hoverAnims = new HashMap<>();
	
	public int cHover;
	
	public GuiScreen parent;
	
	public GuiTabbable()
	{
		this.parent = Minecraft.getMinecraft().currentScreen;
		
		while(this.parent instanceof GuiSkillsBook)
			this.parent = ((GuiSkillsBook) this.parent).parent;
		
		xSize = 195;
		ySize = 168;
		
		gui1 = new UV(new ResourceLocation(InfoIS.MOD_ID, "textures/gui/skills_gui_paper.png"), 0, 0, xSize, ySize);
		gui2 = new UV(new ResourceLocation(InfoIS.MOD_ID, "textures/gui/skills_gui_overlay.png"), 0, 0, xSize, ySize);
		star = new UV(new ResourceLocation(InfoIS.MOD_ID, "textures/gui/skills_gui_overlay.png"), xSize + 1, 0, 10, 10);
	}
	
	protected void drawBack(float partialTicks, int mouseX, int mouseY)
	{
		
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		drawDefaultBackground();
		GL11.glColor4f(1, 1, 1, 1);
		
		GlStateManager.enableDepth();
		
		ScaledResolution sr = new ScaledResolution(mc);
		
		GL11.glEnable(GL11.GL_BLEND);
		
		int cht = 0, chtni = 0;
		boolean singleHover = false;
		
		if(!singleHover)
			cHover = -1;
		
		int rgb = GuiTheme.CURRENT_THEME.name.equalsIgnoreCase("Vanilla") ? 0x0000FF : GuiTheme.CURRENT_THEME.bodyColor;
		
		IForgeRegistry<Pagelet> pgreg = GameRegistry.findRegistry(Pagelet.class);
		List<Pagelet> pagelets = new ArrayList<>(pgreg.getValuesCollection());
		
		for(int i = 0; i < pagelets.size(); ++i)
		{
			Pagelet let = pagelets.get(i);
			boolean mouseOver = mouseX >= guiLeft + 193 && mouseY >= guiTop + 10 + i * 25 && mouseX < guiLeft + 193 + 20 && mouseY < guiTop + 10 + i * 25 + 24;
			
			gui2.bindTexture();
			
			GlStateManager.pushMatrix();
			ColorHelper.gl(255 << 24 | rgb);
			GlStateManager.translate(guiLeft + 193 - (!mouseOver ? 5 : 0), guiTop + 10 + i * 25, 0);
			RenderUtil.drawTexturedModalRect(0, 0, 236, 0, 20, 24, mouseOver ? 30 : 0);
			GlStateManager.translate(0, 0, -85 + (mouseOver ? 20 : 0));
			mc.getRenderItem().renderItemIntoGUI(let.getIcon(), 2, 4);
			GlStateManager.popMatrix();
		}
		
		GL11.glColor4f(1, 1, 1, 1);
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, 20);
		drawBack(partialTicks, mouseX, mouseY);
		GlStateManager.popMatrix();
		
		for(int i = 0; i < pagelets.size(); ++i)
		{
			Pagelet let = pagelets.get(i);
			boolean mouseOver = mouseX >= guiLeft + 193 && mouseY >= guiTop + 10 + i * 25 && mouseX < guiLeft + 193 + 20 && mouseY < guiTop + 10 + i * 25 + 24;
			
			if(mouseOver)
			{
				GlStateManager.pushMatrix();
				GlStateManager.translate(0, 0, 500);
				drawHoveringText(let.title.getUnformattedComponentText(), mouseX, mouseY);
				GlStateManager.popMatrix();
				GlStateManager.disableLighting();
			}
		}
		
		GL11.glDisable(GL11.GL_BLEND);
		GlStateManager.disableDepth();
	}
	
	@Override
	public void updateScreen()
	{
		super.updateScreen();
		prevScrolledPixels = scrolledPixels;
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		if(cHover >= 0)
		{
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
}