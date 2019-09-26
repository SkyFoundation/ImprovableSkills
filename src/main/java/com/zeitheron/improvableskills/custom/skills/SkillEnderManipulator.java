package com.zeitheron.improvableskills.custom.skills;

import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.registry.PlayerSkillBase;

import net.minecraft.world.storage.loot.LootTableList;

public class SkillEnderManipulator extends PlayerSkillBase
{
	public SkillEnderManipulator()
	{
		super(5);
		setRegistryName(InfoIS.MOD_ID, "ender_manipulator");
		
		hasScroll = true;
		genScroll = true;
		
		getLoot().chance.n = 20;
		getLoot().setLootTable(LootTableList.ENTITIES_ENDERMAN);
		
		xpCalculator.xpValue = 3;
	}
}