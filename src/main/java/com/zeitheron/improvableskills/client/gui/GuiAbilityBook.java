package com.zeitheron.improvableskills.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.zeitheron.hammercore.client.utils.UV;
import com.zeitheron.hammercore.client.utils.texture.gui.theme.GuiTheme;
import com.zeitheron.hammercore.lib.zlib.tuple.TwoTuple;
import com.zeitheron.hammercore.utils.color.ColorHelper;
import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.IGuiSkillDataConsumer;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.api.SkillTex;
import com.zeitheron.improvableskills.api.registry.PageletBase;
import com.zeitheron.improvableskills.api.registry.PlayerAbilityBase;
import com.zeitheron.improvableskills.client.gui.base.GuiTabbable;
import com.zeitheron.improvableskills.client.rendering.ote.OTEFadeOutUV;
import com.zeitheron.improvableskills.init.SoundsIS;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class GuiAbilityBook extends GuiTabbable implements IGuiSkillDataConsumer
{
	public final UV gui1, star;
	public double scrolledPixels;
	public double prevScrolledPixels;
	public int row = 6;
	
	public Map<SkillTex<PlayerAbilityBase>, TwoTuple.Atomic<Integer, Integer>> hoverAnims = new HashMap<>();
	
	public int cHover;
	
	public PlayerSkillData data;
	public List<SkillTex<PlayerAbilityBase>> texes = new ArrayList<>();
	
	public GuiAbilityBook(PageletBase pagelet, PlayerSkillData data)
	{
		super(pagelet);
		
		this.data = data;
		
		xSize = 195;
		ySize = 168;
		
		gui1 = new UV(new ResourceLocation(InfoIS.MOD_ID, "textures/gui/skills_gui_paper.png"), 0, 0, xSize, ySize);
		star = new UV(new ResourceLocation(InfoIS.MOD_ID, "textures/gui/skills_gui_overlay.png"), xSize + 1, 0, 10, 10);
		
		List<PlayerAbilityBase> skills = new ArrayList<>(GameRegistry.findRegistry(PlayerAbilityBase.class).getValuesCollection());
		
		skills //
		        .stream() //
		        .sorted((t1, t2) -> t1.getLocalizedName(data).compareTo(t2.getLocalizedName(data))) //
		        .filter(skill -> skill.isVisible(data)) //
		        .forEach(skill -> texes.add(skill.tex));
	}
	
	String[] warn = { "Note: This tab is under heavy development!", "More abilities will come soon!" };
	
	@Override
	protected void drawBack(float partialTicks, int mouseX, int mouseY)
	{
		for(int i = 0; i < warn.length; ++i)
			drawCenteredString(fontRenderer, warn[i], (int) (guiLeft + xSize / 2), (int) (guiTop + ySize + 4) + (fontRenderer.FONT_HEIGHT + 2) * i, 0x55FFFFFF);
		
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
			SkillTex<PlayerAbilityBase> tex = texes.get(j);
			
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
				
				GL11.glColor4f(1, 1, 1, (float) Math.sin(Math.toRadians(cht / 255F * 90F)));
				hov.render(x, y, 24, 24);
				GL11.glColor4f(1, 1, 1, 1);
			} else
				tex.toUV(false).render(x, y, 24, 24);
				
			// if(tex.skill.getScrollState().hasScroll())
			// {
			// GL11.glPushMatrix();
			// GL11.glTranslated(x + .5, y + 19.5, 0);
			// GL11.glScaled(1 / 2D, 1 / 2D, 1);
			// mc.getRenderItem().renderItemAndEffectIntoGUI(ItemSkillScroll.of(tex.skill),
			// 0, 0);
			// GL11.glPopMatrix();
			// }
		}
		
		if(!singleHover)
			cHover = -1;
		
		GL11.glDisable(3089);
		
		int rgb = GuiTheme.CURRENT_THEME.name.equalsIgnoreCase("Vanilla") ? 0x0088FF : GuiTheme.CURRENT_THEME.bodyColor;
		
		ColorHelper.gl(255 << 24 | rgb);
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, 5);
		gui2.render(guiLeft, guiTop);
		GlStateManager.popMatrix();
		
		GL11.glColor4f(1, 1, 1, 1);
		
		if(cHover >= 0 && chtni >= 200)
		{
			SkillTex<PlayerAbilityBase> tex = texes.get(cHover % co);
			GL11.glPushMatrix();
			GL11.glTranslatef(0, 0, 500);
			drawHoveringText(tex.skill.getLocalizedName(data), mouseX, mouseY);
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
			SkillTex<PlayerAbilityBase> tex = texes.get(j);
			
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
			PlayerAbilityBase skill = texes.get(cHover % texes.size()).skill;
			
			skill.onClickClient(mc.player, mouseButton);
			
			int co = texes.size();
			for(int i = 0; i < co; ++i)
			{
				int j = i % co;
				SkillTex<PlayerAbilityBase> tex = texes.get(j);
				
				double x = (i % row) * 28 + guiLeft + 16;
				double y = (i / row) * 28 - (prevScrolledPixels + (scrolledPixels - prevScrolledPixels) * mc.getRenderPartialTicks());
				
				if(tex == skill.tex)
					new OTEFadeOutUV(tex.toUV(true), 24, 24, x, y + guiTop + 9, 2);
			}
			
			mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundsIS.PAGE_TURNS, 1F));
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