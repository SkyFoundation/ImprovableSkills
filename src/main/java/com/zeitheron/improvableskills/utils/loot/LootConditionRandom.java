package com.zeitheron.improvableskills.utils.loot;

import java.util.Random;

import com.zeitheron.improvableskills.api.loot.RandomBoolean;

import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;

public class LootConditionRandom implements LootCondition
{
	public RandomBoolean oneInN;
	
	public LootConditionRandom(RandomBoolean oneInN)
	{
		this.oneInN = oneInN;
	}
	
	@Override
	public boolean testCondition(Random rand, LootContext context)
	{
		if(!oneInN.get(rand))
			return false;
		return true;
	}
}