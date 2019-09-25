package com.zeitheron.improvableskills.custom.skills;

import com.zeitheron.hammercore.net.HCNet;
import com.zeitheron.hammercore.utils.WorldUtil;
import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.api.registry.PlayerSkillBase;

import net.minecraft.block.BlockFurnace;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SkillAcceleratedFurnace extends PlayerSkillBase
{
	public SkillAcceleratedFurnace()
	{
		super(15);
		xpCalculator.xpValue = 2;
		setRegistryName(InfoIS.MOD_ID, "accelerated_furnace");
	}
	
	@Override
	public void tick(PlayerSkillData data)
	{
		int lvl = data.getSkillLevel(this);
		boolean acquired = lvl > 0;
		
		if(!acquired || data.player.world.isRemote)
			return;
		
		World w = data.player.world;
		
		int rad = 3;
		for(int x = -rad; x <= rad; ++x)
			for(int y = -rad; y <= rad; ++y)
				for(int z = -rad; z <= rad; ++z)
				{
					BlockPos pos = data.player.getPosition().add(x, y, z);
					
					TileEntityFurnace tef = WorldUtil.cast(w.getTileEntity(pos), TileEntityFurnace.class);
					if(tef != null)
					{
						int burnTime = tef.getField(0);
						int progress = tef.getField(2);
						
						if(burnTime > 0)
						{
							int add = 2 * (int) Math.round(Math.sqrt(lvl));
							tef.setField(2, progress + add);
							tef.setField(0, (int) Math.max(0, burnTime - add * .8F));
							if(tef.getField(2) >= tef.getField(3))
							{
								tef.smeltItem();
								tef.setField(2, 0);
							}
						} else if(tef.getField(2) < 1)
						{
							IBlockState state = w.getBlockState(pos);
							if(state.getBlock() == Blocks.LIT_FURNACE)
							{
								int meta = state.getBlock().getMetaFromState(state);
								state = Blocks.FURNACE.getStateFromMeta(meta);
								
								w.removeTileEntity(pos);
								
								w.setBlockState(pos, state);
								
								tef.validate();
								w.setTileEntity(pos, tef);
							}
						}
						
						if(tef.getWorld().rand.nextInt(9) == 0)
						{
							EnumFacing face = tef.getWorld().getBlockState(tef.getPos()).getValue(BlockFurnace.FACING);
							
							Vec3d vec = new Vec3d(tef.getPos().offset(face));
							
							face = face.getOpposite();
							vec = vec.add(.5 + face.getXOffset() * .5, .65 + face.getYOffset() * .5, .5 + face.getZOffset() * .5);
							
							HCNet.spawnParticle(tef.getWorld(), EnumParticleTypes.REDSTONE, vec.x, vec.y, vec.z, 0, 0, 0, 0xFFFF00);
						}
					}
				}
	}
}