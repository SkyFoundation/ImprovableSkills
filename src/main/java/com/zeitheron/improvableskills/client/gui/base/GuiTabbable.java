package com.zeitheron.improvableskills.client.gui.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.zeitheron.hammercore.client.gui.GuiCentered;
import com.zeitheron.hammercore.client.utils.RenderUtil;
import com.zeitheron.hammercore.client.utils.UV;
import com.zeitheron.hammercore.client.utils.texture.ITexBindable;
import com.zeitheron.hammercore.client.utils.texture.def.IBindableImage;
import com.zeitheron.hammercore.client.utils.texture.gui.theme.GuiTheme;
import com.zeitheron.hammercore.lib.zlib.tuple.TwoTuple;
import com.zeitheron.hammercore.lib.zlib.tuple.TwoTuple.Atomic;
import com.zeitheron.hammercore.utils.color.ColorHelper;
import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.registry.PageletBase;
import com.zeitheron.improvableskills.init.PageletsIS;
import com.zeitheron.improvableskills.proxy.SyncSkills;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public class GuiTabbable extends GuiCentered
{
	public static PageletBase lastPagelet = PageletsIS.SKILLS;
	public static final Map<ResourceLocation, TwoTuple.Atomic<Float, Float>> EXTENSIONS = new HashMap<>();
	
	public final PageletBase pagelet;
	protected PageletBase selPgl;
	public GuiScreen parent;
	public final UV gui2;
	
	List<String> pageletTooltip = new ArrayList<>();
	
	public GuiTabbable(PageletBase pagelet)
	{
		this.pagelet = pagelet;
		
		lastPagelet = pagelet;
		
		xSize = 195;
		ySize = 168;
		
		gui2 = new UV(new ResourceLocation(InfoIS.MOD_ID, "textures/gui/skills_gui_overlay.png"), 0, 0, xSize, ySize);
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
		
		GL11.glEnable(GL11.GL_BLEND);
		
		int rgb = GuiTheme.CURRENT_THEME.name.equalsIgnoreCase("Vanilla") ? 0x0000FF : GuiTheme.CURRENT_THEME.bodyColor;
		
		IForgeRegistry<PageletBase> pgreg = GameRegistry.findRegistry(PageletBase.class);
		List<PageletBase> pagelets = new ArrayList<>(pgreg.getValuesCollection());
		
		selPgl = null;
		
		int i = 0;
		for(int j = 0; j < pagelets.size(); ++j)
		{
			PageletBase let = pagelets.get(j);
			
			if(!let.isVisible() || !let.isRight())
				continue;
			
			boolean mouseOver = mouseX >= guiLeft + 195 && mouseY >= guiTop + 10 + i * 25 && mouseX < guiLeft + 193 + 20 && mouseY < guiTop + 10 + i * 25 + 24;
			
			if(mouseOver)
				selPgl = let;
			
			mouseOver |= pagelet == let;
			
			gui2.bindTexture();
			
			TwoTuple.Atomic<Float, Float> t = EXTENSIONS.get(let.getRegistryName());
			if(t == null)
				EXTENSIONS.put(let.getRegistryName(), t = new Atomic<Float, Float>(0F, 0F));
			t.set1(mouseOver ? 1F : 0F);
			
			float progress = 5 * t.get2().floatValue();
			float dif = Math.max(-.125F, Math.min(.125F, t.get1() - t.get2()));
			progress += dif * partialTicks;
			
			progress = (float) (Math.sin(Math.toRadians(progress / 5D * 90)) * 5D);
			
			GlStateManager.pushMatrix();
			GlStateManager.enableDepth();
			GlStateManager.disableLighting();
			RenderHelper.disableStandardItemLighting();
			ColorHelper.gl(255 << 24 | rgb);
			GlStateManager.translate(guiLeft + 193 - 7 * ((5 - progress) / 5), guiTop + 10 + i * 25, progress >= 5F && let == pagelet ? 200 : 0);
			RenderUtil.drawTexturedModalRect(0, 0, 236, 0, 20, 24, mouseOver ? 30 : 0);
			GlStateManager.translate(0, 0, -50);
			Object icon = let.getIcon();
			
			if(icon instanceof ItemStack)
				mc.getRenderItem().renderItemIntoGUI((ItemStack) icon, 2, 4);
			if(icon instanceof ITextureObject || icon instanceof ITexBindable || icon instanceof IBindableImage)
			{
				GlStateManager.translate(0, 0, 150);
				
				ColorHelper.gl(0xFF_FFFFFF);
				
				if(icon instanceof ITextureObject)
					GlStateManager.bindTexture(((ITextureObject) icon).getGlTextureId());
				else if(icon instanceof ITexBindable)
					((ITexBindable) icon).bind();
				else if(icon instanceof IBindableImage)
					((IBindableImage) icon).glBind(mc.player.ticksExisted + partialTicks);
				
				RenderUtil.drawFullTexturedModalRect(2, 4, 16, 16);
			}
			
			GlStateManager.popMatrix();
			++i;
		}
		
		//
		
		i = 0;
		for(int j = 0; j < pagelets.size(); ++j)
		{
			PageletBase let = pagelets.get(j);
			
			if(!let.isVisible() || let.isRight())
				continue;
			
			boolean mouseOver = mouseX >= guiLeft - 17 && mouseY >= guiTop + 10 + i * 25 && mouseX < guiLeft && mouseY < guiTop + 10 + i * 25 + 24;
			
			if(mouseOver)
				selPgl = let;
			
			mouseOver |= pagelet == let;
			
			gui2.bindTexture();
			
			TwoTuple.Atomic<Float, Float> t = EXTENSIONS.get(let.getRegistryName());
			if(t == null)
				EXTENSIONS.put(let.getRegistryName(), t = new Atomic<Float, Float>(0F, 0F));
			t.set1(mouseOver ? 1F : 0F);
			
			float progress = 5 * t.get2().floatValue();
			float dif = Math.max(-.125F, Math.min(.125F, t.get1() - t.get2()));
			progress += dif * partialTicks;
			
			progress = (float) (Math.sin(Math.toRadians(progress / 5D * 90)) * 5D);
			
			GlStateManager.pushMatrix();
			GlStateManager.enableDepth();
			GlStateManager.disableLighting();
			RenderHelper.disableStandardItemLighting();
			ColorHelper.gl(255 << 24 | rgb);
			GlStateManager.translate(guiLeft - 18 + 7 * ((5 - progress) / 5), guiTop + 10 + i * 25, progress >= 5F && let == pagelet ? 200 : 0);
			
			GlStateManager.pushMatrix();
			GlStateManager.translate(10, 14, 0);
			GlStateManager.scale(-1, -1, 1);
			GlStateManager.translate(-10, -14, 0);
			RenderUtil.drawTexturedModalRect(0, 4, 236, 0, 20, 24, mouseOver ? 30 : 0);
			GlStateManager.popMatrix();
			
			GlStateManager.translate(0, 0, -50);
			Object icon = let.getIcon();
			
			if(icon instanceof ItemStack)
				mc.getRenderItem().renderItemIntoGUI((ItemStack) icon, 2, 4);
			if(icon instanceof ITextureObject)
			{
				GlStateManager.translate(0, 0, 150);
				
				ColorHelper.gl(0xFF_FFFFFF);
				
				GlStateManager.bindTexture(((ITextureObject) icon).getGlTextureId());
				RenderUtil.drawFullTexturedModalRect(2, 4, 16, 16);
			}
			
			GlStateManager.popMatrix();
			++i;
		}
		
		//
		
		GL11.glColor4f(1, 1, 1, 1);
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, 100);
		drawBack(partialTicks, mouseX, mouseY);
		GlStateManager.popMatrix();
		
		//
		
		if(selPgl != null)
		{
			pageletTooltip.clear();
			
			selPgl.addTitle(pageletTooltip);
			
			GlStateManager.pushMatrix();
			GlStateManager.translate(0, 0, 500);
			drawHoveringText(pageletTooltip, mouseX, mouseY);
			GlStateManager.popMatrix();
			GlStateManager.disableLighting();
		}
		
		//
		
		GL11.glDisable(GL11.GL_BLEND);
		GlStateManager.disableDepth();
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		if(selPgl != null)
		{
			if(selPgl.hasTab())
			{
				if(pagelet != selPgl)
					mc.displayGuiScreen(selPgl.createTab(SyncSkills.getData()));
			} else
				selPgl.onClick();
			
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