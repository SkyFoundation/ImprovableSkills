package com.zeitheron.improvableskills.client.rendering.ote;

import org.lwjgl.opengl.GL11;

import com.zeitheron.improvableskills.client.rendering.OTEffect;
import com.zeitheron.improvableskills.client.rendering.OnTopEffects;
import com.zeitheron.improvableskills.init.ItemsIS;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

public class OTEBook extends OTEffect
{
	public ItemStack item = new ItemStack(ItemsIS.SKILLS_BOOK);
	private double tx, ty;
	private int totTime, prevTime, time;
	
	private static OTEBook book;
	
	public static void show(int time)
	{
		if(time == 0)
		{
			if(book != null && !book.expired)
				book.totTime = time + 8;
			return;
		}
		
		if(book != null && !book.expired)
		{
			book.totTime = Math.max(book.totTime, time);
			book.time = Math.min(5, book.time);
			book.prevTime = book.time;
		}
		else
		{
			Minecraft mc = Minecraft.getMinecraft();
			
			ScaledResolution scaledresolution = new ScaledResolution(mc);
			int w = scaledresolution.getScaledWidth();
			int h = scaledresolution.getScaledHeight();
			
			new OTEBook(w - 12, h - 12, time);
		}
	}
	
	public OTEBook(double x, double y, int time)
	{
		renderGui = false;
		this.totTime = time;
		this.x = this.prevX = x;
		this.y = this.prevY = y;
		OnTopEffects.effects.add(this);
	}
	
	@Override
	public void resize(ScaledResolution prev, ScaledResolution nev)
	{
		super.resize(prev, nev);
		tx = handleResizeXd(tx, prev, nev);
		ty = handleResizeYd(ty, prev, nev);
	}
	
	@Override
	public void update()
	{
		super.update();
		prevTime = time;
		
		time++;
		
		if(time >= totTime)
		{
			setExpired();
			book = null;
		} else
			book = this;
	}
	
	@Override
	public void render(float partialTime)
	{
		double cx = prevX + (x - prevX) * partialTime;
		double cy = prevY + (y - prevY) * partialTime;
		float t = prevTime + partialTime;
		
		GlStateManager.enableAlpha();
		RenderHelper.disableStandardItemLighting();
		
		float scale = 1F;
		
		if(t < 5)
			scale *= t / 5F;
		
		if(t >= totTime - 5)
			scale *= 1 - (t - totTime + 5) / 5F;
		
		GL11.glPushMatrix();
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glTranslated(cx - 16 * scale / 2, cy - 16 * scale / 2, 0);
		GL11.glScaled(scale, scale, scale);
		Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(item, 0, 0);
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glPopMatrix();
	}
}