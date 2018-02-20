package com.endie.is.skills;

import com.endie.is.InfoIS;
import com.endie.is.api.PlayerSkillBase;
import com.endie.is.api.PlayerSkillData;

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