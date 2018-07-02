package com.zeitheron.improvableskills.skills;

import com.zeitheron.hammercore.net.HCNet;
import com.zeitheron.hammercore.utils.WorldUtil;
import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.PlayerSkillBase;
import com.zeitheron.improvableskills.api.PlayerSkillData;

import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.storage.loot.LootTableList;

public class SkillAlchemist extends PlayerSkillBase
{
	public SkillAlchemist()
	{
		super(15);
		xpValue = 2;
		setRegistryName(InfoIS.MOD_ID, "alchemist");
		hasScroll = true;
		genScroll = true;
		
		getLoot().chance.n = 10;
		getLoot().setLootTable(LootTableList.ENTITIES_WITCH);
	}
	
	@Override
	public void tick(PlayerSkillData data)
	{
		int lvl = data.getSkillLevel(this);
		boolean acquired = lvl > 0;
		
		if(!acquired && data.player.world.isRemote)
			return;
		
		int rad = 3;
		for(int x = -rad; x <= rad; ++x)
			for(int y = -rad; y <= rad; ++y)
				for(int z = -rad; z <= rad; ++z)
				{
					TileEntityBrewingStand tef = WorldUtil.cast(data.player.world.getTileEntity(data.player.getPosition().add(x, y, z)), TileEntityBrewingStand.class);
					if(tef != null)
					{
						int progress = tef.getField(0);
						
						if(progress > 0)
						{
							int add = 2 * (int) Math.sqrt(lvl * 2);
							tef.setField(0, Math.max(progress - add, 1));
						}
						
						if(tef.getWorld().rand.nextInt(9) == 0)
						{
							Vec3d vec = new Vec3d(tef.getPos()).addVector(.5, .85, .5);
							HCNet.spawnParticle(tef.getWorld(), EnumParticleTypes.REDSTONE, vec.x, vec.y, vec.z, 0, 0, 0, 0xFFFF00);
						}
					}
				}
	}
}