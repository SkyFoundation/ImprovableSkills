package com.zeitheron.improvableskills.utils.loot;

import java.util.Random;

import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.api.loot.RandomBoolean;
import com.zeitheron.improvableskills.api.registry.PlayerSkillBase;
import com.zeitheron.improvableskills.data.PlayerDataManager;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;

public class LootConditionSkillScroll implements LootCondition
{
	public RandomBoolean oneInN;
	private final String id;
	
	public LootConditionSkillScroll(PlayerSkillBase used, RandomBoolean oneInN)
	{
		id = used.getRegistryName().toString();
		this.oneInN = oneInN;
	}
	
	@Override
	public boolean testCondition(Random rand, LootContext context)
	{
		if(!oneInN.get(rand))
			return false;
		Entity ent = context.getKillerPlayer();
		if(ent instanceof EntityPlayer)
			return !PlayerDataManager.handleDataSafely((EntityPlayer) ent, data -> data.stat_scrolls.contains(id), false).booleanValue();
		return false;
	}
}