package com.endie.is.client.rendering.ote;

import org.lwjgl.opengl.GL11;

import com.endie.is.InfoIS;
import com.endie.is.client.gui.GuiSkillViewer;
import com.endie.is.client.rendering.OTEffect;
import com.endie.is.utils.Trajectory;
import com.pengu.hammercore.client.utils.RenderUtil;
import com.pengu.hammercore.client.utils.UtilsFX;
import com.pengu.hammercore.utils.ColorHelper;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.math.MathHelper;

public class OTESparkle extends OTEffect
{
	private int color;
	private double tx, ty;
	private int totTime, prevTime, time;
	public double[] xPoints, yPoints;
	
	public OTESparkle(double x, double y, double tx, double ty, int time, int color)
	{
		renderHud = false;
		this.totTime = time;
		this.x = this.prevX = x;
		this.y = this.prevY = y;
		this.tx = tx;
		this.ty = ty;
		this.color = color;
		double[][] path = Trajectory.makeBroken2DTrajectory(x, y, tx, ty, time, Math.abs(hashCode() / 25F));
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
		if(!(currentGui instanceof GuiSkillViewer))
			return;
		
		double cx = prevX + (x - prevX) * partialTime;
		double cy = prevY + (y - prevY) * partialTime;
		float t = prevTime + partialTime;
		float r = (float) (System.currentTimeMillis() % 2000L) / 2000.0F;
		r = r > 0.5F ? 1.0F - r : r;
		r += 0.45F;
		
		UtilsFX.bindTexture(InfoIS.MOD_ID, "textures/particles/sparkle.png");
		
		int tx = 64 * (int) (time / (float) totTime * 3F);
		
		GlStateManager.enableAlpha();
		GL11.glEnable(GL11.GL_BLEND);
		RenderHelper.disableStandardItemLighting();
		
		float scale = 1 / 8F;
		
		if(t < 5)
			scale *= t / 5F;
		
		if(t >= totTime - 5)
			scale *= 1 - (t - totTime + 5) / 5F;
		
		GL11.glPushMatrix();
		GL11.glColor4f(ColorHelper.getRed(color), ColorHelper.getGreen(color), ColorHelper.getBlue(color), 1);
		GL11.glTranslated(cx - 64 * scale / 2, cy - 64 * scale / 2, 0);
		GL11.glScaled(scale, scale, scale);
		RenderUtil.drawTexturedModalRect(0, 0, tx, 0, 64, 64);
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glPopMatrix();
		GL11.glDisable(GL11.GL_BLEND);
	}
}