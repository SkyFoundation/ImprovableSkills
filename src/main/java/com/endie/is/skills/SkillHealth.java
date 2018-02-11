package com.endie.is.skills;

import java.util.UUID;

import com.endie.is.InfoIS;
import com.endie.is.api.PlayerSkillBase;
import com.endie.is.api.PlayerSkillData;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.world.storage.loot.LootTableList;

public class SkillHealth extends PlayerSkillBase
{
	public static final UUID HP_ID = UUID.fromString("a6c5d900-a39b-4e1f-9572-f48e174335f2");
	
	public SkillHealth()
	{
		super(20);
		setRegistryName(InfoIS.MOD_ID, "health");
		hasScroll = true;
		genScroll = true;
		
		getLoot().chance.n = 9;
		getLoot().setLootTable(LootTableList.CHESTS_END_CITY_TREASURE);
	}
	
	@Override
	public int getXPToUpgrade(PlayerSkillData data, short targetLvl)
	{
		return (int) Math.pow(targetLvl, 3);
	}
	
	@Override
	public void tick(PlayerSkillData data)
	{
		IAttributeInstance hp = data.player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
		
		hp.removeModifier(HP_ID);
		hp.applyModifier(new AttributeModifier(HP_ID, "IS3 Health", data.getSkillLevel(this), 0));
		
		if(data.player.getHealth() > hp.getAttributeValue())
			data.player.heal(0.001F);
	}
}