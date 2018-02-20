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
		this.totTime = time;
		this.x = this.prevX = x;
		this.y = this.prevY = y;
		this.tx = tx;
		this.ty = ty;
		this.item = item;
		double[][] path = Trajectory.makeBroken2DTrajectory(x, y, tx, ty, time, (float) (System.currentTimeMillis() % 1000000L));
		xPoints = path[0];
		yPoints = path[1];
	}
	
	@Override
	public void update()
	{
		super.update();
		prevTime = time;
		
		int tt = xPoints.length;
		
		int cframe = (int) Math.round(time / (float) totTime * tt);
		
		x = xPoints[cframe];
		y = yPoints[cframe];
		
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
		
		int tx = 64 * (int) (time / (float) totTime * 3F);
		
		GlStateManager.enableAlpha();
		RenderHelper.disableStandardItemLighting();
		
		float scale = 1F;
		
		if(t < 5)
			scale *= t / 5F;
		
		if(t >= totTime - 5)
			scale *= 1 - (t - totTime + 5) / 5F;
		
		GL11.glPushMatrix();
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glTranslated(cx - 24 * scale / 2, cy - 24 * scale / 2, 0);
		GL11.glScaled(scale, scale, scale);
		item.tex.toUV(false).render(0, 0, 24, 24);
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glPopMatrix();
	}
}