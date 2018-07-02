package com.zeitheron.improvableskills.client.rendering.ote;

import org.lwjgl.opengl.GL11;

import com.zeitheron.hammercore.client.utils.RenderUtil;
import com.zeitheron.hammercore.client.utils.UtilsFX;
import com.zeitheron.improvableskills.client.gui.GuiXPBank;
import com.zeitheron.improvableskills.client.rendering.OTEffect;
import com.zeitheron.improvableskills.utils.Trajectory;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;

public class OTEXpOrb extends OTEffect
{
	private double tx, ty;
	private int totTime, prevTime, time;
	public double[] xPoints, yPoints;
	
	public OTEXpOrb(double x, double y, double tx, double ty, int time)
	{
		renderHud = false;
		this.totTime = time;
		this.x = this.prevX = x;
		this.y = this.prevY = y;
		this.tx = tx;
		this.ty = ty;
		double[][] path = Trajectory.makeBroken2DTrajectory(x, y, tx, ty, time, Math.abs(hashCode() / 25F));
		xPoints = path[0];
		yPoints = path[1];
	}
	
	@Override
	public void resize(ScaledResolution prev, ScaledResolution nev)
	{
		super.resize(prev, nev);
		tx = handleResizeXd(tx, prev, nev);
		ty = handleResizeYd(ty, prev, nev);
		xPoints = handleResizeXdv(xPoints, prev, nev);
		yPoints = handleResizeYdv(yPoints, prev, nev);
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
		if(!(currentGui instanceof GuiXPBank))
			return;
		
		double cx = prevX + (x - prevX) * partialTime;
		double cy = prevY + (y - prevY) * partialTime;
		float t = prevTime + partialTime;
		float r = (float) ((System.currentTimeMillis() + Math.abs(hashCode())) % 2000L) / 2000.0F;
		r = r > 0.5F ? 1.0F - r : r;
		r += 0.45F;
		
		UtilsFX.bindTexture("minecraft", "textures/entity/experience_orb.png");
		
		int tx = 64 * (hashCode() % 3);
		
		GlStateManager.enableAlpha();
		RenderHelper.disableStandardItemLighting();
		
		float scale = 1 / 8F;

		if(t < 5)
			scale *= t / 5F;
		
		if(t >= totTime - 5)
			scale *= 1 - (t - totTime + 5) / 5F;
		
		GL11.glPushMatrix();
		GL11.glColor4f(r, 1, 0, 1);
		GL11.glTranslated(cx - 64 * scale / 2, cy - 64 * scale / 2, 0);
		GL11.glScaled(scale, scale, scale);
		RenderUtil.drawTexturedModalRect(0, 0, tx, 0, 64, 64);
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glPopMatrix();
	}
}