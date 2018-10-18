package com.zeitheron.improvableskills.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.zeitheron.hammercore.client.gui.GuiCentered;
import com.zeitheron.hammercore.client.utils.RenderUtil;
import com.zeitheron.hammercore.client.utils.TexturePixelGetter;
import com.zeitheron.hammercore.client.utils.UV;
import com.zeitheron.hammercore.client.utils.texture.TextureUtils;
import com.zeitheron.hammercore.client.utils.texture.gui.theme.GuiTheme;
import com.zeitheron.hammercore.lib.zlib.tuple.TwoTuple;
import com.zeitheron.hammercore.utils.color.ColorHelper;
import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.api.SkillTex;
import com.zeitheron.improvableskills.api.IGuiSkillDataConsumer;
import com.zeitheron.improvableskills.api.registry.PageletBase;
import com.zeitheron.improvableskills.api.registry.PlayerSkillBase;
import com.zeitheron.improvableskills.client.gui.base.GuiTabbable;
import com.zeitheron.improvableskills.client.rendering.OnTopEffects;
import com.zeitheron.improvableskills.client.rendering.ote.OTEFadeOutUV;
import com.zeitheron.improvableskills.client.rendering.ote.OTESparkle;
import com.zeitheron.improvableskills.init.SkillsIS;
import com.zeitheron.improvableskills.items.ItemSkillScroll;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class GuiSkillsBook extends GuiTabbable implements IGuiSkillDataConsumer
{
	public final UV gui1, star;
	public double scrolledPixels;
	public double prevScrolledPixels;
	public int row = 6;
	
	public Map<SkillTex<PlayerSkillBase>, TwoTuple.Atomic<Integer, Integer>> hoverAnims = new HashMap<>();
	
	public int cHover;
	
	public PlayerSkillData data;
	public List<SkillTex<PlayerSkillBase>> texes = new ArrayList<>();
	
	public GuiSkillsBook(PageletBase pagelet, PlayerSkillData data)
	{
		super(pagelet);
		
		this.data = data;
		
		xSize = 195;
		ySize = 168;
		
		gui1 = new UV(new ResourceLocation(InfoIS.MOD_ID, "textures/gui/skills_gui_paper.png"), 0, 0, xSize, ySize);
		star = new UV(new ResourceLocation(InfoIS.MOD_ID, "textures/gui/skills_gui_overlay.png"), xSize + 1, 0, 10, 10);
		
		List<PlayerSkillBase> skills = new ArrayList<>(GameRegistry.findRegistry(PlayerSkillBase.class).getValues());
		
		skills.remove(SkillsIS.XP_STORAGE);
		
		texes.add(SkillsIS.XP_STORAGE.tex);
		
		skills //
		        .stream() //
		        .sorted((t1, t2) -> t1.getLocalizedName(data).compareTo(t2.getLocalizedName(data))) //
		        .filter(skill -> skill.isVisible(data)) //
		        .forEach(skill -> texes.add(skill.tex));
	}
	
	@Override
	protected void drawBack(float partialTicks, int mouseX, int mouseY)
	{
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
			SkillTex<PlayerSkillBase> tex = texes.get(j);
			
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
				
				Random r = new Random();
				
				if(r.nextInt(15) == 0)
				{
					int[] rgbs = TexturePixelGetter.getAllColors(tex.skill.tex.toUV(true).path + "");
					int col = rgbs[r.nextInt(rgbs.length)];
					double tx = x + 2 + r.nextFloat() * 20F;
					double ty = y + 2 + r.nextFloat() * 20F;
					OnTopEffects.effects.add(new OTESparkle(tx, ty, tx, ty, 11, col));
				}
			}
			
			if(cHoverTime > 0)
			{
				cht = (int) (cHoverTimePrev + (cHoverTime - cHoverTimePrev) * partialTicks);
				
				UV norm = tex.toUV(false);
				UV hov = tex.toUV(true);
				
				norm.render(x, y, 24, 24);
				
				GL11.glColor4f(1, 1, 1, (float) Math.sin(Math.toRadians(cht / 255F * 90)));
				hov.render(x, y, 24, 24);
				GL11.glColor4f(1, 1, 1, 1);
			} else
				tex.toUV(false).render(x, y, 24, 24);
			
			if(tex.skill != SkillsIS.XP_STORAGE && data.getSkillLevel(tex.skill) >= tex.skill.maxLvl)
				star.render(x + 15, y + 17, 10, 10);
			
			if(tex.skill.getScrollState().hasScroll())
			{
				GL11.glPushMatrix();
				GL11.glTranslated(x + .5, y + 19.5, 0);
				GL11.glScaled(1 / 2D, 1 / 2D, 1);
				mc.getRenderItem().renderItemAndEffectIntoGUI(ItemSkillScroll.of(tex.skill), 0, 0);
				GL11.glPopMatrix();
			}
		}
		
		if(!singleHover)
			cHover = -1;
		
		GL11.glDisable(3089);
		
		int rgb = GuiTheme.CURRENT_THEME.name.equalsIgnoreCase("Vanilla") ? 0x0000FF : GuiTheme.CURRENT_THEME.bodyColor;
		
		ColorHelper.gl(255 << 24 | rgb);
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, 5);
		gui2.render(guiLeft, guiTop);
		GlStateManager.popMatrix();
		
		GL11.glColor4f(1, 1, 1, 1);
		
		if(cHover >= 0 && chtni >= 200)
		{
			SkillTex<PlayerSkillBase> tex = texes.get(cHover % co);
			GL11.glPushMatrix();
			GL11.glTranslatef(0, 0, 500);
			List<String> ls = new ArrayList<>();
			boolean maxLvl = tex.skill != SkillsIS.XP_STORAGE && data.getSkillLevel(tex.skill) >= tex.skill.maxLvl;
			ls.add(tex.skill.getLocalizedName(data) + (maxLvl ? "  " : ""));
			drawHoveringText(ls, mouseX, mouseY);
			if(maxLvl)
			{
				GL11.glPushMatrix();
				RenderHelper.disableStandardItemLighting();
				GL11.glColor4f(1, 1, 1, 1);
				
				int sw = fontRenderer.getStringWidth(ls.get(0));
				int l1 = mouseX + 12;
				if(l1 + sw + 4 > this.width)
					l1 = mouseX - 16 - sw;
				
				star.render(l1 + sw - 7, mouseY - 14, 10, 10);
				GL11.glPopMatrix();
			}
			GL11.glPopMatrix();
		}
		
		GL11.glDisable(GL11.GL_BLEND);
		GlStateManager.disableDepth();
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
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
			SkillTex<PlayerSkillBase> tex = texes.get(j);
			
			TwoTuple.Atomic<Integer, Integer> hovt = hoverAnims.get(tex);
			if(hovt == null)
				hoverAnims.put(tex, hovt = new TwoTuple.Atomic<>(0, 0));
			
			int cHoverTime = hovt.get1();
			int pht = cHoverTime;
			
			if(cHover == i)
				cHoverTime = Math.min(cHoverTime + 25, 255);
			else
				cHoverTime = Math.max(cHoverTime - 10, 0);
			
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
			
			int co = texes.size();
			for(int i = 0; i < co; ++i)
			{
				int j = i % co;
				SkillTex<PlayerSkillBase> tex = texes.get(j);
				
				double x = (i % row) * 28 + guiLeft + 16;
				double y = (i / row) * 28 - (prevScrolledPixels + (scrolledPixels - prevScrolledPixels) * mc.getRenderPartialTicks());
				
				if(tex == skill.tex)
					new OTEFadeOutUV(tex.toUV(true), 24, 24, x, y + guiTop + 9, 2);
			}
			
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