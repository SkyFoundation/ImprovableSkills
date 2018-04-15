package com.endie.is.client.rendering;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class OTEffect
{
	protected GuiScreen currentGui;
	protected int mouseX, mouseY;
	
	/** Should this effect render in gui? */
	public boolean renderGui = true;
	
	/** Should this effect render in HUD? */
	public boolean renderHud = true;
	
	public double x, y;
	public double prevX, prevY;
	
	public boolean expired = false;
	
	public void render(float partialTime)
	{
		
	}
	
	public void update()
	{
		prevX = x;
		prevY = y;
	}
	
	public void setExpired()
	{
		this.expired = true;
	}
	
	public void resize(ScaledResolution prev, ScaledResolution nev)
	{
		x = handleResizeXd(x, prev, nev);
		prevX = handleResizeXd(prevX, prev, nev);
		
		y = handleResizeYd(y, prev, nev);
		prevY = handleResizeYd(prevY, prev, nev);
	}
	
	protected static double handleResizeXd(double x, ScaledResolution prev, ScaledResolution nev)
	{
		return x / prev.getScaledWidth_double() * nev.getScaledWidth_double();
	}
	
	protected static double handleResizeYd(double y, ScaledResolution prev, ScaledResolution nev)
	{
		return y / prev.getScaledHeight_double() * nev.getScaledHeight_double();
	}
	
	protected static int handleResizeXi(int x, ScaledResolution prev, ScaledResolution nev)
	{
		return x / prev.getScaledWidth() * nev.getScaledWidth();
	}
	
	protected static int handleResizeYi(int y, ScaledResolution prev, ScaledResolution nev)
	{
		return y / prev.getScaledHeight() * nev.getScaledHeight();
	}
	
	protected static int[] handleResizeXiv(int[] x, ScaledResolution prev, ScaledResolution nev)
	{
		int[] v = x.clone();
		for(int i = 0; i < v.length; ++i)
			v[i] = handleResizeXi(x[i], prev, nev);
		return v;
	}
	
	protected static int[] handleResizeYiv(int[] y, ScaledResolution prev, ScaledResolution nev)
	{
		int[] v = y.clone();
		for(int i = 0; i < v.length; ++i)
			v[i] = handleResizeYi(y[i], prev, nev);
		return v;
	}
	
	protected static double[] handleResizeXdv(double[] x, ScaledResolution prev, ScaledResolution nev)
	{
		double[] v = x.clone();
		for(int i = 0; i < v.length; ++i)
			v[i] = handleResizeXd(x[i], prev, nev);
		return v;
	}
	
	protected static double[] handleResizeYdv(double[] y, ScaledResolution prev, ScaledResolution nev)
	{
		double[] v = y.clone();
		for(int i = 0; i < v.length; ++i)
			v[i] = handleResizeYd(y[i], prev, nev);
		return v;
	}
}