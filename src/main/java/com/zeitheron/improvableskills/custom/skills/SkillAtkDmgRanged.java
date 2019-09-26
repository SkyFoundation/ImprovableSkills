package com.zeitheron.improvableskills.custom.skills;

import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.registry.PlayerSkillBase;

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
		
		xpCalculator.xpValue = 3;
	}
}