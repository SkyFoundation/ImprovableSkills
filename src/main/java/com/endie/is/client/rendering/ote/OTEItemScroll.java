package com.endie.is.client.rendering.ote;

import org.lwjgl.opengl.GL11;

import com.endie.is.api.PlayerSkillBase;
import com.endie.is.client.rendering.OTEffect;
import com.endie.is.client.rendering.OnTopEffects;
import com.endie.is.utils.Trajectory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;

public class OTEItemScroll extends OTEffect
{
	public ItemStack item;
	private double tx, ty;
	private int totTime, prevTime, time;
	public double[] xPoints, yPoints;
	public PlayerSkillBase[] skills;
	
	public OTEItemScroll(double x, double y, double tx, double ty, int time, ItemStack item, PlayerSkillBase... skills)
	{
		renderGui = false;
		this.skills = skills;
		this.totTime = time;
		this.x = this.prevX = x;
		this.y = this.prevY = y;
		this.tx = tx;
		this.ty = ty;
		this.item = item;
		double[][] path = Trajectory.makeBroken2DTrajectory(x, y, tx, ty, time, (float) (System.currentTimeMillis() % 1000000L) / 90F);
		xPoints = path[0];
		yPoints = path[1];
	}
	
	@Override
	public void update()
	{
		super.update();
		prevTime = time;
		
		int tt = xPoints.length;
		
		int cframe = Math.min((int) Math.round(time / (float) totTime * tt), xPoints.length - 1);
		
		x = xPoints[cframe];
		y = yPoints[cframe];
		
		time++;
		
		int spawnTime = 10 * skills.length;
		
		if(time >= totTime)
		{
			int cur = (time - totTime) / 10;
			
			if((time - totTime) % 10 == 0 && cur < skills.length)
			{
				Minecraft mc = Minecraft.getMinecraft();
				ScaledResolution sr = new ScaledResolution(mc);
				OnTopEffects.effects.add(new OTESkill(x, y, sr.getScaledWidth() - 20, sr.getScaledHeight() - 12, 40, skills[cur]));
				Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 1));
			}
		}
		
		if(time >= totTime + spawnTime)
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
		
		// if(t < 5)
		// scale *= t / 5F;
		
		if(t >= totTime + 10 * skills.length - 5)
			scale *= 1 - (t - totTime + 5 - 10 * skills.length) / 5F;
		
		GL11.glPushMatrix();
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glTranslated(cx - 16 * scale / 2, cy - 16 * scale / 2, 0);
		GL11.glScaled(scale, scale, scale);
		Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(item, 0, 0);
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glPopMatrix();
	}
}