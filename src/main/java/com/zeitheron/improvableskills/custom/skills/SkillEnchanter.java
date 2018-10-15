package com.zeitheron.improvableskills.custom.skills;

import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.api.registry.PlayerSkillBase;

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