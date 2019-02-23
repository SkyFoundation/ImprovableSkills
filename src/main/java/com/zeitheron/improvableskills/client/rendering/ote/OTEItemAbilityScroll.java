package com.zeitheron.improvableskills.client.rendering.ote;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.zeitheron.hammercore.client.utils.TexturePixelGetter;
import com.zeitheron.improvableskills.api.registry.PlayerAbilityBase;
import com.zeitheron.improvableskills.client.rendering.OTEffect;
import com.zeitheron.improvableskills.client.rendering.OnTopEffects;
import com.zeitheron.improvableskills.utils.Trajectory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;

public class OTEItemAbilityScroll extends OTEffect
{
	public ItemStack item;
	private double tx, ty;
	private int totTime, prevTime, time;
	public double[] xPoints, yPoints;
	public PlayerAbilityBase[] abilities;
	
	public OTEItemAbilityScroll(double x, double y, double tx, double ty, int time, ItemStack item, PlayerAbilityBase... skills)
	{
		renderGui = false;
		this.abilities = skills;
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
		
		int cframe = Math.min((int) Math.round(time / (float) totTime * tt), xPoints.length - 1);
		
		x = xPoints[cframe];
		y = yPoints[cframe];
		
		time++;
		
		int spawnTime = 10 * abilities.length;
		
		if(time >= totTime)
		{
			int cur = (time - totTime) / 10;
			
			if((time - totTime) % 10 == 0 && cur < abilities.length)
			{
				Minecraft mc = Minecraft.getMinecraft();
				ScaledResolution sr = new ScaledResolution(mc);
				
				OnTopEffects.effects.add(new OTEAbility(x, y, sr.getScaledWidth() - 12, sr.getScaledHeight() - 12, 40, abilities[cur]));
				Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 1));
			}
		} else
		{
			int lcf = Math.max(cframe - 10, 0);
			
			Random r = new Random();
			if(r.nextBoolean())
			{
				int[] rgbs = TexturePixelGetter.getAllColors(abilities[r.nextInt(abilities.length)].tex.toUV(true).path + "");
				if(rgbs.length > 0)
				{
					int col = rgbs[r.nextInt(rgbs.length)];
					double tx = xPoints[lcf] + (r.nextInt(16) - r.nextInt(16)) / 2F;
					double ty = yPoints[cframe] + (r.nextInt(16) - r.nextInt(16)) / 2F;
					OnTopEffects.effects.add(new OTESkillSparkle(x - r.nextInt(8) + r.nextInt(8), y - r.nextInt(8) + r.nextInt(8), tx, ty, 20, col));
				}
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
		
		if(t >= totTime + 10 * abilities.length - 5)
			scale *= 1 - (t - totTime + 5 - 10 * abilities.length) / 5F;
		
		GL11.glPushMatrix();
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glTranslated(cx - 16 * scale / 2, cy - 16 * scale / 2, 0);
		GL11.glScaled(scale, scale, scale);
		Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(item, 0, 0);
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glPopMatrix();
	}
}