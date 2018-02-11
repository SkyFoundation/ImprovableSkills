package com.endie.is.skills;

import com.endie.is.InfoIS;
import com.endie.is.api.PlayerSkillBase;
import com.endie.is.api.PlayerSkillData;

import net.minecraft.world.storage.loot.LootTableList;

public class SkillAtkDmgRanged extends PlayerSkillBase
{
	public SkillAtkDmgRanged()
	{
		super(15);
		setRegistryName(InfoIS.MOD_ID, "atkdmg_ranged");
		hasScroll = true;
		genScroll = true;
		
		getLoot().chance.n = 40;
		getLoot().setLootTable(LootTableList.ENTITIES_SKELETON);
	}
	
	@Override
	public int getXPToUpgrade(PlayerSkillData data, short targetLvl)
	{
		return (int) Math.pow(targetLvl, 3);
	}
}