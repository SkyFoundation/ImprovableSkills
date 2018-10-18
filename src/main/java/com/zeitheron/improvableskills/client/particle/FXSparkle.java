package com.zeitheron.improvableskills.client.particle;

import org.lwjgl.opengl.GL11;

import com.zeitheron.hammercore.client.particle.api.SimpleParticle;
import com.zeitheron.hammercore.client.utils.UtilsFX;
import com.zeitheron.hammercore.utils.color.ColorHelper;
import com.zeitheron.improvableskills.InfoIS;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class FXSparkle extends SimpleParticle
{
	public int blendmode = 1;
	
	public FXSparkle(World worldIn, double posXIn, double posYIn, double posZIn, int color, int maxAge)
	{
		super(worldIn, posXIn, posYIn, posZIn);
		particleRed = ColorHelper.getRed(color);
		particleGreen = ColorHelper.getGreen(color);
		particleBlue = ColorHelper.getBlue(color);
		particleMaxAge = maxAge;
	}
	
	public FXSparkle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int color, int maxAge)
	{
		super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
		motionX = xSpeedIn;
		motionY = ySpeedIn;
		motionZ = zSpeedIn;
		particleRed = ColorHelper.getRed(color);
		particleGreen = ColorHelper.getGreen(color);
		particleBlue = ColorHelper.getBlue(color);
		particleMaxAge = maxAge;
	}
	
	@Override
	public void doRenderParticle(double x, double y, double z, final float f, final float f1, final float f2, final float f3, final float f4, final float f5)
	{
		GL11.glPushMatrix();
		GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_BLEND);
		UtilsFX.bindTexture(InfoIS.MOD_ID, "textures/particles/sparkle.png");
		GL11.glColor4f(1, 1, 1, .75F);
		final int part = (int) ((particleAge / (float) particleMaxAge) * 3);
		final float var8 = part % 4 / 4F;
		final float var9 = var8 + .25F;
		final float var10 = part / 4 / 4;
		final float var11 = var10 + .25F;
		
		Tessellator t = Tessellator.getInstance();
		BufferBuilder b = t.getBuffer();
		final float var16 = 1.0f;
		
		GL11.glBlendFunc(770, blendmode);
		
		for(int i = 0; i < 3; ++i)
		{
			float ps = i == 0 ? particleScale : i == 2 ? (float) ((Math.sin(hashCode() % 90 + (particleAge + f) / 2) + 1) / 2.5 * particleScale) : particleScale / 2;
			
			float var12 = ps * ((particleMaxAge - particleAge + 1F) / particleMaxAge) / 10;
			final float var13 = (float) (prevPosX + (posX - prevPosX) * f - FXSparkle.interpPosX);
			final float var14 = (float) (prevPosY + (posY - prevPosY) * f - FXSparkle.interpPosY);
			final float var15 = (float) (prevPosZ + (posZ - prevPosZ) * f - FXSparkle.interpPosZ);
			
			b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
			b.pos(var13 - f1 * var12 - f4 * var12, var14 - f2 * var12, var15 - f3 * var12 - f5 * var12).tex(var9, var11).color(particleRed * var16, particleGreen * var16, particleBlue * var16, 1.0f).endVertex();
			b.pos(var13 - f1 * var12 + f4 * var12, var14 + f2 * var12, var15 - f3 * var12 + f5 * var12).tex(var9, var10).color(particleRed * var16, particleGreen * var16, particleBlue * var16, 1.0f).endVertex();
			b.pos(var13 + f1 * var12 + f4 * var12, var14 + f2 * var12, var15 + f3 * var12 + f5 * var12).tex(var8, var10).color(particleRed * var16, particleGreen * var16, particleBlue * var16, 1.0f).endVertex();
			b.pos(var13 + f1 * var12 - f4 * var12, var14 - f2 * var12, var15 + f3 * var12 - f5 * var12).tex(var8, var11).color(particleRed * var16, particleGreen * var16, particleBlue * var16, 1.0f).endVertex();
			t.draw();
		}
		
		GL11.glDepthMask(true);
		GL11.glPopMatrix();
		GL11.glBlendFunc(770, 771);
	}
	
	@Override
	public void onUpdate()
	{
		Entity renderentity = Minecraft.getMinecraft().getRenderViewEntity();
		int visibleDistance = 50;
		if(!Minecraft.getMinecraft().gameSettings.fancyGraphics)
			visibleDistance = 25;
		
		if(renderentity.getDistance(posX, posY, posZ) > visibleDistance)
			setExpired();
		
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		
		if(particleAge++ >= particleMaxAge)
			setExpired();
		
		motionY -= 0.04 * particleGravity;
		
		move(motionX, motionY, motionZ);
		
		double air = 0.9800000190734863;
		
		motionX *= air;
		motionY *= air;
		motionZ *= air;
		
		if(onGround)
		{
			motionX *= 0.699999988079071;
			motionZ *= 0.699999988079071;
		}
	}
}