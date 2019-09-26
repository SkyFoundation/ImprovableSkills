package com.zeitheron.improvableskills.custom.skills;

import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.api.registry.PlayerSkillBase;

import net.minecraft.world.storage.loot.LootTableList;

public class SkillObsidianSkin extends PlayerSkillBase
{
	public SkillObsidianSkin()
	{
		super(20);
		setRegistryName(InfoIS.MOD_ID, "obsidian_skin");
		hasScroll = true;
		genScroll = true;
		
		getLoot().chance.n = 3;
		getLoot().setLootTable(LootTableList.CHESTS_NETHER_BRIDGE);
		
		xpCalculator.xpValue = 2;
	}
}