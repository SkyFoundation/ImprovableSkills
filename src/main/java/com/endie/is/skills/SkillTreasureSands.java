package com.endie.is.skills;

import java.util.List;
import java.util.Random;

import com.endie.is.InfoIS;
import com.endie.is.api.PlayerSkillBase;
import com.endie.is.api.PlayerSkillData;
import com.endie.is.api.treasures.DropUtil;
import com.endie.is.api.treasures.TreasureContext;
import com.endie.is.api.treasures.TreasureDropBase;
import com.endie.is.data.PlayerDataManager;
import com.pengu.hammercore.utils.WorldLocation;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SkillTreasureSands extends PlayerSkillBase
{
	public SkillTreasureSands()
	{
		super(3);
		MinecraftForge.EVENT_BUS.register(this);
		setRegistryName(InfoIS.MOD_ID, "treasure_sands");
		hasScroll = true;
		genScroll = true;
		
		getLoot().chance.n = 8;
		getLoot().setLootTable(LootTableList.CHESTS_DESERT_PYRAMID);
	}
	
	@Override
	public int getXPToUpgrade(PlayerSkillData data, short targetLvl)
	{
		return (int) Math.pow(targetLvl + 1, 7) + 200;
	}
	
	public void handleDropAdd(BlockEvent.HarvestDropsEvent loc, PlayerSkillData data, List<ItemStack> drops)
	{
		if(data == null || !(loc.getWorld() instanceof WorldServer))
			return;
		
		WorldLocation l = new WorldLocation(loc.getWorld(), loc.getPos());
		
		if(loc.getState().getBlock() == Blocks.SAND && l.getBiome().getTemperature() >= 2F)
		{
			Random rng = data.player.getRNG();
			
			if(rng.nextInt(100) < 5 * data.getSkillLevel(this))
			{
				TreasureContext ctx = new TreasureContext.Builder() //
				        .withCaller(this) //
				        .withData(data) //
				        .withLocation(l) //
				        .withRNG(rng) //
				        .build();
				TreasureDropBase dr = DropUtil.chooseDrop(ctx);
				if(dr != null)
					dr.drop(ctx, drops);
			}
		}
	}
}