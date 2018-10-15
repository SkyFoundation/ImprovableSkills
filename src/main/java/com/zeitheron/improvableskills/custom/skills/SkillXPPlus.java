package com.zeitheron.improvableskills.custom.skills;

import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.api.registry.PlayerSkillBase;

import net.minecraft.world.storage.loot.LootTableList;

public class SkillXPPlus extends PlayerSkillBase
{
	public SkillXPPlus()
	{
		super(10);
		setRegistryName(InfoIS.MOD_ID, "xp+");
		
		hasScroll = true;
		genScroll = true;
		
		getLoot().chance.n = 3;
		getLoot().setLootTable(LootTableList.ENTITIES_ELDER_GUARDIAN);
	}
	
	@Override
	public int getXPToUpgrade(PlayerSkillData data, short targetLvl)
	{
		return (int) Math.pow(targetLvl, 3) + (targetLvl + 1) * 100;
	}
}