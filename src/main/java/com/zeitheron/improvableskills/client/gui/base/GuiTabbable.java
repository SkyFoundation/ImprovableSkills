package com.zeitheron.improvableskills.client.gui.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.zeitheron.hammercore.client.gui.GuiCentered;
import com.zeitheron.hammercore.client.utils.RenderUtil;
import com.zeitheron.hammercore.client.utils.UV;
import com.zeitheron.hammercore.client.utils.UtilsFX;
import com.zeitheron.hammercore.client.utils.texture.ITexBindable;
import com.zeitheron.hammercore.client.utils.texture.def.IBindableImage;
import com.zeitheron.hammercore.client.utils.texture.gui.theme.GuiTheme;
import com.zeitheron.hammercore.lib.zlib.tuple.TwoTuple;
import com.zeitheron.hammercore.lib.zlib.tuple.TwoTuple.Atomic;
import com.zeitheron.hammercore.lib.zlib.utils.Threading;
import com.zeitheron.hammercore.utils.HolidayTrigger;
import com.zeitheron.hammercore.utils.color.ColorHelper;
import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.registry.PageletBase;
import com.zeitheron.improvableskills.client.rendering.ote.OTEConfetti;
import com.zeitheron.improvableskills.client.rendering.ote.OTETooltip;
import com.zeitheron.improvableskills.custom.pagelets.PageletUpdate;
import com.zeitheron.improvableskills.init.PageletsIS;
import com.zeitheron.improvableskills.init.SoundsIS;
import com.zeitheron.improvableskills.proxy.SyncSkills;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
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
	public final UV gui1, gui2;
	
	List<String> pageletTooltip = new ArrayList<>();
	
	protected int liveAnimationTime;
	protected boolean zeithBDay = false;
	
	public GuiTabbable(PageletBase pagelet)
	{
		this.pagelet = pagelet;
		
		lastPagelet = pagelet;
		
		xSize = 195;
		ySize = 168;
		
		gui1 = new UV(new ResourceLocation(InfoIS.MOD_ID, "textures/gui/skills_gui_paper.png"), 0, 0, xSize, ySize);
		gui2 = new UV(new ResourceLocation(InfoIS.MOD_ID, "textures/gui/skills_gui_overlay.png"), 0, 0, xSize, ySize);
		
		if(GuiCustomButton.ZEITH_AVATAR == null)
			Threading.createAndStart(() -> GuiCustomButton.ZEITH_AVATAR = IBindableImage.probe("https://dccg.herokuapp.com/cfmember/Zeitheron/avatar/128"));
	}
	
	protected void drawBack(float partialTicks, int mouseX, int mouseY)
	{
		
	}
	
	@Override
	public void updateScreen()
	{
		super.updateScreen();
		
		final ScaledResolution scaledresolution = new ScaledResolution(this.mc);
		int i1 = scaledresolution.getScaledWidth();
		int j1 = scaledresolution.getScaledHeight();
		final int mouseX = Mouse.getX() * i1 / this.mc.displayWidth;
		final int mouseY = j1 - Mouse.getY() * j1 / this.mc.displayHeight - 1;
		
		zeithBDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 10 && Calendar.getInstance().get(Calendar.MONTH) == Calendar.NOVEMBER;
		
		if(zeithBDay)
		{
			int[] colors = { 0xFF_FF0000, 0xFF_FF6600, 0xFF_FFFF00, 0xFF_00FF00, 0xFF_0000FF, 0xFF_FF00FF };
			int color = colors[colors.length - 1 - (int) ((System.currentTimeMillis() % (colors.length * 3000L)) / 3000L) % colors.length];
			
			if(mouseX > width / 2 - 16 && mouseY > guiTop - 36 && mouseX < width / 2 + 16 && mouseY < guiTop - 4)
				for(int i = 0; i < 4; ++i)
				{
					OTEConfetti cft = new OTEConfetti(width / 2, guiTop - 36 + OTEConfetti.random.nextFloat() * 32);
					cft.motionY = -1.25F;
					cft.motionX = (OTEConfetti.random.nextFloat() - OTEConfetti.random.nextFloat()) * 6F;
					cft.color = color;
				}
		}
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		drawDefaultBackground();
		GL11.glColor4f(1, 1, 1, 1);
		
		GlStateManager.enableDepth();
		
		GL11.glEnable(GL11.GL_BLEND);
		
		IBindableImage z = GuiCustomButton.ZEITH_AVATAR;
		if(z != null && zeithBDay)
		{
			z.glBind(0);
			
			GlStateManager.pushMatrix();
			GlStateManager.translate(width / 2 - 16, guiTop - 36, 350);
			GlStateManager.translate(16, 16, 0);
			GlStateManager.rotate(6 * OTEConfetti.sineF(System.currentTimeMillis() % 4000L / 1000F), 0, 0, 1);
			GlStateManager.translate(-16, -16, 0);
			RenderUtil.drawFullTexturedModalRect(0, 0, 32, 32);
			GlStateManager.popMatrix();
		}
		
		if(z != null && PageletUpdate.liveURL != null)
		{
			z.glBind(0);
			
			float s = 16F / fontRenderer.FONT_HEIGHT;
			float w = s * fontRenderer.getStringWidth("LIVE");
			
			boolean hover = mouseX >= (width - (w + 64)) / 2 && mouseX < (width - (w + 64)) / 2 + w + 64 && mouseY >= guiTop - 36 && mouseY < guiTop - 4;
			
			GL11.glColor4f(1, 1, 1, 1);
			GlStateManager.pushMatrix();
			GlStateManager.translate((width - (w + 64)) / 2, guiTop - 36, 350);
			RenderUtil.drawFullTexturedModalRect(0, 0, 32, 32);
			GlStateManager.pushMatrix();
			GlStateManager.translate(32, 4, 0);
			GlStateManager.scale(s, s, 1F);
			fontRenderer.drawString("LIVE", 0, 3, hover ? 0xFFAAAA : 0xFFFFFF);
			GL11.glColor4f(1, 1, 1, 1);
			GlStateManager.popMatrix();
			UtilsFX.bindTexture("minecraft", "textures/gui/stream_indicator.png");
			GlStateManager.pushMatrix();
			GlStateManager.translate(w + 32, 0, 0);
			GlStateManager.scale(1 / 4F, 1, 1F);
			GlStateManager.scale(32 / 60F, 32 / 60F, 1);
			RenderUtil.drawTexturedModalRect(0, 0, 0, 0, 240, 60);
			GlStateManager.popMatrix();
			GlStateManager.popMatrix();
		}
		
		int rgb = GuiTheme.current().name.equalsIgnoreCase("Vanilla") ? 0x0088FF : GuiTheme.current().bodyColor;
		
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
			
			OTETooltip.showTooltip(pageletTooltip);
		}
		
		//
		
		float s = 16F / fontRenderer.FONT_HEIGHT;
		float w = s * fontRenderer.getStringWidth("LIVE");
		
		if(PageletUpdate.liveURL != null && mouseX >= (width - (w + 64)) / 2 && mouseX < (width - (w + 64)) / 2 + w + 64 && mouseY >= guiTop - 36 && mouseY < guiTop - 4)
		{
			GlStateManager.pushMatrix();
			GlStateManager.translate(0, 0, 700);
			OTETooltip.showTooltip("Zeitheron is LIVE!", "\"" + PageletUpdate.liveTitle + "\"", "Click to watch!");
			GlStateManager.popMatrix();
		} else if(zeithBDay && mouseX > width / 2 - 16 && mouseY > guiTop - 36 && mouseX < width / 2 + 16 && mouseY < guiTop - 4)
		{
			GlStateManager.pushMatrix();
			GlStateManager.translate(0, 0, 700);
			OTETooltip.showTooltip("Happy birthday, Zeitheron!");
			GlStateManager.popMatrix();
		}
		
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
			
			mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundsIS.PAGE_TURNS, 1F));
		}
		
		float s = 16F / fontRenderer.FONT_HEIGHT;
		float w = s * fontRenderer.getStringWidth("LIVE");
		
		if(PageletUpdate.liveURL != null && mouseX >= (width - (w + 64)) / 2 && mouseX < (width - (w + 64)) / 2 + w + 64 && mouseY >= guiTop - 36 && mouseY < guiTop - 4)
		{
			mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, .5F));
			Sys.openURL(PageletUpdate.liveURL);
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