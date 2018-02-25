package com.endie.is.utils;

import net.minecraft.util.math.MathHelper;

public class Trajectory
{
	public static double[][] makeBroken2DTrajectory(double x, double y, double tx, double ty, int coords, float timeOffset)
	{
		return makeBroken2DTrajectory(x, y, tx, ty, coords, timeOffset, 5F);
	}
	
	public static double[][] makeBroken2DTrajectory(double x, double y, double tx, double ty, int coords, float timeOffset, float offset)
	{
		double d3 = x - tx;
		double d4 = y - ty;
		float dx = (float) (d3 / coords);
		float dy = (float) (d4 / coords);
		
		if(Math.abs(d3) > Math.abs(d4))
			dx *= 2.0F;
		else
			dy *= 2.0F;
		
		double[] xPoints = new double[coords + 1];
		double[] yPoints = new double[coords + 1];
		
		for(int a = 0; a <= coords; ++a)
		{
			float mx = 0.0F;
			float my = 0.0F;
			float phase = (float) a / (float) coords;
			mx = MathHelper.sin((timeOffset + a) / 7.0F) * offset * (1.0F - phase);
			my = MathHelper.sin((timeOffset + a) / 5.0F) * offset * (1.0F - phase);
			xPoints[a] = x - dx * a + mx;
			yPoints[a] = y - dy * a + my;
			if(Math.abs(d3) > Math.abs(d4))
				dx *= 1.0F - 1.0F / (coords * 3.0F / 2.0F);
			else
				dy *= 1.0F - 1.0F / (coords * 3.0F / 2.0F);
		}
		
		return new double[][] { xPoints, yPoints };
	}
}