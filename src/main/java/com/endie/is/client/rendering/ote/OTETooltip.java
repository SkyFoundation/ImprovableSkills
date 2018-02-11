package com.endie.is.client.rendering.ote;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.endie.is.client.rendering.OTEffect;
import com.endie.is.client.rendering.OnTopEffects;
import com.pengu.hammercore.client.utils.UtilsFX;

import net.minecraft.client.Minecraft;

public class OTETooltip extends OTEffect
{
	public final List<String> tooltip = new ArrayList<>();
	
	private int time;
	private static OTETooltip cinst;
	
	public static void showTooltip(String... tip)
	{
		if(cinst == null)
			cinst = new OTETooltip();
		cinst.tooltip.clear();
		for(String t : tip)
			cinst.tooltip.add(t);
		
		/** Always on top */
		if(OnTopEffects.effects.indexOf(cinst) != OnTopEffects.effects.size() - 1)
		{
			OnTopEffects.effects.remove(cinst);
			OnTopEffects.effects.add(cinst);
		}
	}
	
	public static void showTooltip(List<String> tip)
	{
		if(cinst == null)
			cinst = new OTETooltip();
		cinst.tooltip.clear();
		cinst.tooltip.addAll(tip);
		
		/** Always on top */
		if(OnTopEffects.effects.indexOf(cinst) != OnTopEffects.effects.size() - 1)
		{
			OnTopEffects.effects.remove(cinst);
			OnTopEffects.effects.add(cinst);
		}
	}
	
	{
		OnTopEffects.effects.add(this);
	}
	
	@Override
	public void update()
	{
		if(time++ >= 8)
			setExpired();
		else
		{
			cinst = this;
			
			/** Always on top */
			if(OnTopEffects.effects.indexOf(this) != OnTopEffects.effects.size() - 1)
			{
				OnTopEffects.effects.remove(this);
				OnTopEffects.effects.add(this);
			}
		}
	}
	
	@Override
	public void setExpired()
	{
		super.setExpired();
		cinst = null;
	}
	
	@Override
	public void render(float partialTime)
	{
		if(!tooltip.isEmpty())
		{
			GL11.glPushMatrix();
			GL11.glTranslated(0, 0, 200);
			UtilsFX.drawCustomTooltip(currentGui, Minecraft.getMinecraft().getRenderItem(), Minecraft.getMinecraft().fontRenderer, tooltip, mouseX, mouseY, 15);
			GL11.glPopMatrix();
			tooltip.clear();
		}
	}
}