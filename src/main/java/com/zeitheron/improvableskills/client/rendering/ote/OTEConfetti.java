package com.zeitheron.improvableskills.client.rendering.ote;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.zeitheron.hammercore.client.utils.RenderUtil;
import com.zeitheron.hammercore.client.utils.UtilsFX;
import com.zeitheron.hammercore.utils.color.ColorHelper;
import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.client.rendering.OTEffect;
import com.zeitheron.improvableskills.client.rendering.OnTopEffects;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class OTEConfetti extends OTEffect
{
	public static final Random random = new Random();
	
	public int color = 255 << 24 | ColorHelper.packRGB(Math.max(.5F, random.nextFloat()), Math.max(.5F, random.nextFloat()), Math.max(.5F, random.nextFloat()));
	
	public int ticksExisted;
	
	public float motionX, motionY;
	
	/**
	 * Converts a [0-1] value to another [0-1] value, but using sine function
	 */
	public static final float sineF(float val)
	{
		return (float) Math.sin(Math.toRadians(val * 90F));
	}
	
	public static ItemStack getSkull(String player)
	{
		ItemStack stack = new ItemStack(Items.SKULL, 1, 3);
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("SkullOwner", player);
		stack.setTagCompound(nbt);
		return stack;
	}
	
	public OTEConfetti(double x, double y)
	{
		this.x = this.prevX = x;
		this.y = this.prevY = y;
		renderHud = false;
		
		OnTopEffects.effects.add(this);
	}
	
	@Override
	public void update()
	{
		super.update();
		ticksExisted++;
		
		x += motionX;
		y += motionY;
		
		motionY += .05;
		
		motionX *= .98535735;
		motionY *= .98535735;
		
		int ma = 160 - (Math.abs(hashCode()) % 40);
		
		if(ticksExisted >= ma || y < -8 || x < -8 || y > height || x > width)
		{
			setExpired();
		}
	}
	
	@Override
	public void render(float partialTime)
	{
		float alpha = sineF((40F - ticksExisted - partialTime) / 40F);
		int ma = 160 - (Math.abs(hashCode()) % 40);
		
		double cx = prevX + (x - prevX) * partialTime;
		double cy = prevY + (y - prevY) * partialTime;
		float t = ticksExisted + partialTime;
		float r = (float) (System.currentTimeMillis() % 2000L) / 2000.0F;
		r = r > 0.5F ? 1.0F - r : r;
		r += 0.45F;
		
		UtilsFX.bindTexture(InfoIS.MOD_ID, "textures/particles/sparkle.png");
		
		int tx = 64 * (int) (ticksExisted / (float) ma * 3F);
		
		GlStateManager.enableAlpha();
		GL11.glEnable(GL11.GL_BLEND);
		RenderHelper.disableStandardItemLighting();
		GlStateManager.enableDepth();
		
		float scale = 1 / 8F;
		
		if(t < 5)
			scale *= t / 5F;
		
		if(t >= ma - 5)
			scale *= 1 - (t - ma + 5) / 5F;
		
		GL11.glColor4f(ColorHelper.getRed(color), ColorHelper.getGreen(color), ColorHelper.getBlue(color), .9F * ColorHelper.getAlpha(color));
		
		for(int i = 0; i < 3; ++i)
		{
			float ps = i == 0 ? scale : i == 2 ? (float) ((Math.sin(hashCode() % 90 + t / 2) + 1) / 2.5 * scale) : scale / 2;
			
			GL11.glPushMatrix();
			GL11.glBlendFunc(770, i == 0 ? 771 : 772);
			GL11.glTranslated(cx - 64 * ps / 2, cy - 64 * ps / 2, 0);
			GL11.glScaled(ps, ps, ps);
			RenderUtil.drawTexturedModalRect(0, 0, tx, 0, 64, 64);
			GL11.glPopMatrix();
		}
		
		GL11.glBlendFunc(770, 771);
		GL11.glColor4f(1, 1, 1, 1);
		GlStateManager.disableDepth();
		GL11.glDisable(GL11.GL_BLEND);
	}
}