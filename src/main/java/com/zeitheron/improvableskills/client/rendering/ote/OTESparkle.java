package com.zeitheron.improvableskills.client.rendering.ote;

import org.lwjgl.opengl.GL11;

import com.zeitheron.hammercore.client.utils.RenderUtil;
import com.zeitheron.hammercore.client.utils.UtilsFX;
import com.zeitheron.hammercore.utils.color.ColorHelper;
import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.client.gui.GuiSkillViewer;
import com.zeitheron.improvableskills.client.gui.base.GuiTabbable;
import com.zeitheron.improvableskills.client.rendering.OTEffect;
import com.zeitheron.improvableskills.utils.Trajectory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;

public class OTESparkle extends OTEffect
{
	private int color;
	private double tx, ty;
	private int totTime, prevTime, time;
	public double[] xPoints, yPoints;
	
	Class<? extends GuiScreen> screen;
	
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
		
		screen = Minecraft.getMinecraft().currentScreen != null ? Minecraft.getMinecraft().currentScreen.getClass() : null;
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
		GuiScreen gui = Minecraft.getMinecraft().currentScreen;
		if((gui == null && screen == null) || (screen != null && gui != null && screen.isAssignableFrom(gui.getClass())))
		{
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
			
			GL11.glColor4f(ColorHelper.getRed(color), ColorHelper.getGreen(color), ColorHelper.getBlue(color), .9F * ColorHelper.getAlpha(color));
			
			for(int i = 0; i < 3; ++i)
			{
				float ps = i == 0 ? scale : i == 2 ? (float) ((Math.sin(hashCode() % 90 + t / 2) + 1) / 2.5 * scale) : scale / 2;
				
				GL11.glPushMatrix();
				GL11.glBlendFunc(770, i == 0 ? 771 : 772);
				GL11.glTranslated(cx - 64 * ps / 2, cy - 64 * ps / 2, 5);
				GL11.glScaled(ps, ps, ps);
				RenderUtil.drawTexturedModalRect(0, 0, tx, 0, 64, 64);
				GL11.glPopMatrix();
			}
			
			GL11.glBlendFunc(770, 771);
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glDisable(GL11.GL_BLEND);
		}
	}
}