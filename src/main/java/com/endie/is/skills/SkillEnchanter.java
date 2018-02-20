package com.endie.is.skills;

import com.endie.is.InfoIS;
import com.endie.is.api.PlayerSkillBase;
import com.endie.is.api.PlayerSkillData;

import net.minecraft.world.storage.loot.LootTableList;

public class SkillEnchanter extends PlayerSkillBase
{
	public SkillEnchanter()
	{
		super(20);
		setRegistryName(InfoIS.MOD_ID, "enchanter");
		
		hasScroll = true;
		genScroll = true;
		
		getLoot().chance.n = 4;
		getLoot().setLootTable(LootTableList.CHESTS_STRONGHOLD_LIBRARY);
	}
	
	@Override
	public int getXPToUpgrade(PlayerSkillData data, short targetLvl)
	{
		return (int) Math.pow(targetLvl, 2);
	}
}