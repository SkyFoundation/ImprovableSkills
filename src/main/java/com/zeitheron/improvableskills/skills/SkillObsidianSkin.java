package com.zeitheron.improvableskills.skills;

import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.PlayerSkillBase;
import com.zeitheron.improvableskills.api.PlayerSkillData;

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
	}
	
	@Override
	public int getXPToUpgrade(PlayerSkillData data, short targetLvl)
	{
		return (int) Math.pow(targetLvl, 2);
	}
}