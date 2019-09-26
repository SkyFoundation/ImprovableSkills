package com.zeitheron.improvableskills.custom.skills;

import java.util.UUID;

import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.api.registry.PlayerSkillBase;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.world.storage.loot.LootTableList;

public class SkillLuckOfTheSea extends PlayerSkillBase
{
	public static final UUID LOTS_LUCK = UUID.fromString("d489061e-0b53-4aa3-a7f4-f1a9a726ef49");
	
	public SkillLuckOfTheSea()
	{
		super(15);
		setRegistryName(InfoIS.MOD_ID, "luck_of_the_sea");
		
		hasScroll = true;
		genScroll = true;
		
		getLoot().chance.n = 10;
		getLoot().setLootTable(LootTableList.GAMEPLAY_FISHING);
		
		xpCalculator.xpValue = 2;
	}
	
	@Override
	public void tick(PlayerSkillData data)
	{
		EntityPlayer player = data.player;
		EntityFishHook hook = player.fishEntity;
		int level = data.getSkillLevel(this);
		IAttributeInstance luck = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.LUCK);
		luck.removeModifier(LOTS_LUCK);
		if((hook != null) && (!hook.isDead))
			luck.applyModifier(new AttributeModifier(LOTS_LUCK, "IS3 Fishing Luck", level * 2D, 0));
	}
}