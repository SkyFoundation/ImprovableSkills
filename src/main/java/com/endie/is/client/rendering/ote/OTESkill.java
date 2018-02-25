package com.endie.is.client.rendering.ote;

import org.lwjgl.opengl.GL11;

import com.endie.is.api.PlayerSkillBase;
import com.endie.is.client.rendering.OTEffect;
import com.endie.is.utils.Trajectory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

public class OTESkill extends OTEffect
{
	public PlayerSkillBase item;
	private double tx, ty;
	private int totTime, prevTime, time;
	public double[] xPoints, yPoints;
	
	public OTESkill(double x, double y, double tx, double ty, int time, PlayerSkillBase item)
	{
		renderGui = false;
		this.totTime = time + 5;
		this.x = this.prevX = x;
		this.y = this.prevY = y;
		this.tx = tx;
		this.ty = ty;
		this.item = item;
		double[][] path = Trajectory.makeBroken2DTrajectory(x, y, tx, ty, time, (float) (System.currentTimeMillis() % 1000000L) / 100F);
		xPoints = path[0];
		yPoints = path[1];
		x = xPoints[0];
		y = yPoints[0];
	}
	
	@Override
	public void update()
	{
		super.update();
		prevTime = time;
		
		int tt = xPoints.length;
		
		if(time > 5)
		{
			int cframe = (int) Math.round((time - 5) / (float) (totTime - 5) * tt);
			x = xPoints[cframe];
			y = yPoints[cframe];
		}
		
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
		RenderHelper.disableStandardItemLighting();
		
		float scale = 1F;
		
		if(t < 5)
			scale *= t / 5F;
		
		if(t >= totTime - 5)
			scale *= 1 - (t - totTime + 5) / 5F;
		
		scale *= 16;
		
		GL11.glPushMatrix();
		GL11.glColor4f(1, 1, 1, 1);
		item.tex.toUV(false).render(x - scale / 2, y - scale / 2, scale, scale);
		GL11.glPopMatrix();
	}
}