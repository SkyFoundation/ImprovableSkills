package com.endie.is.skills;

import java.util.List;

import com.endie.is.InfoIS;
import com.endie.is.api.PlayerSkillBase;
import com.endie.is.api.PlayerSkillData;
import com.endie.is.utils.TileHelper;
import com.pengu.hammercore.net.HCNetwork;
import com.pengu.hammercore.utils.WorldLocation;

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
		
		if(!acquired)
			return;
		
		List<TileEntityBrewingStand> tiles = TileHelper.collectTiles(new WorldLocation(data.player.getEntityWorld(), data.player.getPosition()), 5, TileEntityBrewingStand.class);
		
		for(TileEntityBrewingStand tef : tiles)
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
				HCNetwork.spawnParticle(tef.getWorld(), EnumParticleTypes.REDSTONE, vec.x, vec.y, vec.z, 0, 0, 0, 0xFFFF00);
			}
		}
	}
}