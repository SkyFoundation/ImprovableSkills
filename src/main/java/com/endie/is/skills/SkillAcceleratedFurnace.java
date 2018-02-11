package com.endie.is.skills;

import java.util.List;

import com.endie.is.InfoIS;
import com.endie.is.api.PlayerSkillBase;
import com.endie.is.api.PlayerSkillData;
import com.endie.is.utils.TileHelper;
import com.pengu.hammercore.net.HCNetwork;
import com.pengu.hammercore.utils.WorldLocation;

import net.minecraft.block.BlockFurnace;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;

public class SkillAcceleratedFurnace extends PlayerSkillBase
{
	public SkillAcceleratedFurnace()
	{
		super(15);
		xpValue = 2;
		setRegistryName(InfoIS.MOD_ID, "accelerated_furnace");
	}
	
	@Override
	public void tick(PlayerSkillData data)
	{
		int lvl = data.getSkillLevel(this);
		boolean acquired = lvl > 0;
		
		if(!acquired)
			return;
		
		List<TileEntityFurnace> tiles = TileHelper.collectTiles(new WorldLocation(data.player.getEntityWorld(), data.player.getPosition()), 5, TileEntityFurnace.class);
		
		for(TileEntityFurnace tef : tiles)
		{
			int burnTime = tef.getField(0);
			int progress = tef.getField(2);
			
			if(burnTime > 0)
			{
				int add = 2 * (int) Math.sqrt(lvl * 2);
				tef.setField(2, progress + add);
				tef.setField(0, (int) Math.max(0, burnTime - add * .8));
				if(tef.getField(2) >= tef.getField(3))
				{
					tef.smeltItem();
					tef.setField(2, 0);
				}
			}
			
			if(tef.getWorld().rand.nextInt(9) == 0)
			{
				EnumFacing face = tef.getWorld().getBlockState(tef.getPos()).getValue(BlockFurnace.FACING);
				
				Vec3d vec = new Vec3d(tef.getPos().offset(face));
				
				face = face.getOpposite();
				vec = vec.addVector(.5 + face.getFrontOffsetX() * .5, .65 + face.getFrontOffsetY() * .5, .5 + face.getFrontOffsetZ() * .5);
				
				HCNetwork.spawnParticle(tef.getWorld(), EnumParticleTypes.REDSTONE, vec.x, vec.y, vec.z, 0, 0, 0, 0xFFFF00);
			}
		}
	}
}