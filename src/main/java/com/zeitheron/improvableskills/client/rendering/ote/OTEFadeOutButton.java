package com.zeitheron.improvableskills.client.rendering.ote;

import org.lwjgl.opengl.GL11;

import com.zeitheron.hammercore.client.utils.UV;
import com.zeitheron.improvableskills.client.rendering.OTEffect;
import com.zeitheron.improvableskills.client.rendering.OnTopEffects;
import com.zeitheron.improvableskills.init.ItemsIS;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class OTEFadeOutButton extends OTEffect
{
	public ItemStack item = new ItemStack(ItemsIS.SKILLS_BOOK);
	private int totTime, prevTime, time;
	private GuiButton uv;
	
	public OTEFadeOutButton(GuiButton uv, int time)
	{
		renderHud = false;
		this.uv = uv;
		this.totTime = time;
		this.x = this.prevX = x;
		this.y = this.prevY = y;
		OnTopEffects.effects.add(this);
	}
	
	@Override
	public void update()
	{
		super.update();
		prevTime = time;
		
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
		
		Minecraft mc = Minecraft.getMinecraft();
		FontRenderer fontrenderer = mc.fontRenderer;
		ResourceLocation rl = new ResourceLocation("minecraft", "textures/gui/widgets.png");
		
		float scale = 1F + (float) Math.sqrt(t);
		int a = (int) ((1 - t / totTime) * .75F * 255);
		
		GL11.glPushMatrix();
		
		GL11.glEnable(GL11.GL_BLEND);
		
		{
			int i = !uv.enabled ? 0 : uv.isMouseOver() ? 2 : 1;
			GL11.glColor4f(1, 1, 1, a / 255F);
			
			new UV(rl, 0, 46 + i * 20, uv.width / 2 - scale / 2, uv.height) //
			        .render(uv.x - scale / 2, uv.y - scale / 2, uv.width / 2 + scale / 2, uv.height + scale);
			
			new UV(rl, 200 - uv.width / 2 + scale / 2, 46 + i * 20, uv.width / 2 - scale / 2, uv.height) //
			        .render(uv.x + uv.width / 2, uv.y - scale / 2, uv.width / 2 + scale / 2, uv.height + scale);
		}
		
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glPopMatrix();
	}
}