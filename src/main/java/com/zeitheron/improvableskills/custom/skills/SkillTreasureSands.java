package com.zeitheron.improvableskills.custom.skills;

import java.util.List;
import java.util.Random;

import com.zeitheron.hammercore.utils.WorldLocation;
import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.api.registry.PlayerSkillBase;
import com.zeitheron.improvableskills.api.treasures.DropUtil;
import com.zeitheron.improvableskills.api.treasures.TreasureContext;
import com.zeitheron.improvableskills.api.treasures.TreasureDropBase;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;

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
		
		xpCalculator.baseFormula = "(%lvl%+1)^7+200";
	}
	
	public void handleDropAdd(BlockEvent.HarvestDropsEvent loc, PlayerSkillData data, List<ItemStack> drops)
	{
		if(data == null || !(loc.getWorld() instanceof WorldServer))
			return;
		
		WorldLocation l = new WorldLocation(loc.getWorld(), loc.getPos());
		
		if(loc.getState().getMaterial() == Material.SAND && l.getBiome().getDefaultTemperature() >= 2F)
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