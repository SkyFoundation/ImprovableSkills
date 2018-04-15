package com.endie.is.client.rendering.ote;

import org.lwjgl.opengl.GL11;

import com.endie.is.client.rendering.OTEffect;
import com.endie.is.client.rendering.OnTopEffects;
import com.endie.is.init.ItemsIS;
import com.pengu.hammercore.client.UV;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

public class OTEFadeOutUV extends OTEffect
{
	public ItemStack item = new ItemStack(ItemsIS.SKILLS_BOOK);
	private double w, h;
	private int totTime, prevTime, time;
	private UV uv;
	
	public OTEFadeOutUV(UV uv, double w, double h, double x, double y, int time)
	{
		renderHud = false;
		this.uv = uv;
		this.totTime = time;
		this.x = this.prevX = x;
		this.y = this.prevY = y;
		this.w = w;
		this.h = h;
		OnTopEffects.effects.add(this);
	}
	
	@Override
	public void update()
	{
		super.update();
		prevTime = time;
		
		time++;
		
		if(time >= totTime)
			setExpired();
	}
	
	@Override
	public void render(float partialTime)
	{
		double cx = prevX + (x - prevX) * partialTime;
		double cy = prevY + (y - prevY) * partialTime;
		float t = prevTime + partialTime;

		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		RenderHelper.disableStandardItemLighting();
		
		float scale = 1F + (float) Math.sqrt(t);
		
		GL11.glPushMatrix();
		GL11.glBlendFunc(770, 1);
		GL11.glColor4f(1, 1, 1, (1 - t / totTime) * .75F);
		uv.render(x - scale / 2, y - scale / 2, w + scale, h + scale);
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glBlendFunc(770, 771);
		GL11.glPopMatrix();
	}
}