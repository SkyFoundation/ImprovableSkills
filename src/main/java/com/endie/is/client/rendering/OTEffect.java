package com.endie.is.client.rendering;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiScreenEvent;
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
}