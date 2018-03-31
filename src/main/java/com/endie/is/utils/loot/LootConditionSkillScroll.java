package com.endie.is.utils.loot;

import java.util.Random;

import com.endie.is.api.PlayerSkillBase;
import com.endie.is.api.PlayerSkillData;
import com.endie.is.api.loot.RandomBoolean;
import com.endie.is.data.PlayerDataManager;

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
		{
			PlayerSkillData data = PlayerDataManager.getDataFor((EntityPlayer) ent);
			if(data != null)
				return !data.stat_scrolls.contains(id);
		}
		return false;
	}
}