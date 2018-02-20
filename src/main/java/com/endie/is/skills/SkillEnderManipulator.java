package com.endie.is.skills;

import com.endie.is.InfoIS;
import com.endie.is.api.PlayerSkillBase;
import com.endie.is.api.PlayerSkillData;

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
	}
	
	@Override
	public int getXPToUpgrade(PlayerSkillData data, short targetLvl)
	{
		return (int) Math.pow(targetLvl, 3);
	}
}