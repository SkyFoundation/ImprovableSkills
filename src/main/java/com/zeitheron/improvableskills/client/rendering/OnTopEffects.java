package com.zeitheron.improvableskills.client.rendering;

import java.util.ArrayList;
import java.util.List;

import com.zeitheron.hammercore.lib.zlib.tuple.TwoTuple;
import com.zeitheron.improvableskills.client.gui.base.GuiTabbable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.Type;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class OnTopEffects
{
	public static List<OTEffect> effects = new ArrayList<>();
	
	@SubscribeEvent
	public void renderScreen(RenderGameOverlayEvent e)
	{
		if(!(e instanceof RenderGameOverlayEvent.Post))
			return;
		if(e.getType() != RenderGameOverlayEvent.ElementType.ALL)
			return;
		
		Minecraft mc = Minecraft.getMinecraft();
		
		float pt = Minecraft.getMinecraft().getRenderPartialTicks();
		
		for(int i = 0; i < effects.size(); ++i)
		{
			OTEffect eff = effects.get(i);
			
			if(eff.expired || !eff.renderHud)
				continue;
			
			eff.render(pt);
		}
	}
	
	@SubscribeEvent
	public void renderOnTop(GuiScreenEvent.DrawScreenEvent.Post e)
	{
		GuiScreen gs = e.getGui();
		int mx = e.getMouseX(), my = e.getMouseY();
		float pt = Minecraft.getMinecraft().getRenderPartialTicks();
		
		for(int i = 0; i < effects.size(); ++i)
		{
			OTEffect eff = effects.get(i);
			
			if(eff.expired || !eff.renderGui)
				continue;
			
			eff.currentGui = gs;
			eff.mouseX = mx;
			eff.mouseY = my;
			
			eff.render(pt);
		}
	}
	
	public ScaledResolution resolution;
	
	@SubscribeEvent
	public void tick(ClientTickEvent e)
	{
		if(e.phase == Phase.START)
		{
			for(int i = 0; i < effects.size(); ++i)
			{
				OTEffect eff = effects.get(i);
				
				if(eff.expired)
				{
					effects.remove(i);
					continue;
				}
				
				eff.update();
			}
			
			ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
			
			if(resolution == null)
				resolution = sr;
			
			if(sr.getScaledHeight() != resolution.getScaledHeight() || sr.getScaledWidth() != resolution.getScaledWidth())
			{
				for(int i = 0; i < effects.size(); ++i)
				{
					OTEffect eff = effects.get(i);
					eff.resize(resolution, sr);
				}
			}
			
			resolution = sr;
			
			for(ResourceLocation key : GuiTabbable.EXTENSIONS.keySet())
			{
				TwoTuple.Atomic<Float, Float> val = GuiTabbable.EXTENSIONS.get(key);
				
				Float target = val.get1();
				Float current = val.get2();
				
				float dif = Math.max(-.125F, Math.min(.125F, target - current));
				
				val.set2(current + dif);
				
//				if(target < .5)
//				{
//					float v = System.currentTimeMillis() % 10000L / 10000F;
//					
//					if(current < v)
//						val.set2(v);
//				}
			}
		}
	}
}