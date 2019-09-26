package com.zeitheron.improvableskills.custom.skills;

import java.util.UUID;

import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.api.registry.PlayerSkillBase;

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
		xpCalculator.xpValue = 3;
		
		getLoot().chance.n = 9;
		getLoot().setLootTable(LootTableList.CHESTS_END_CITY_TREASURE);
	}
	
	@Override
	public void tick(PlayerSkillData data)
	{
		IAttributeInstance hp = data.player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
		
		AttributeModifier mod = hp.getModifier(HP_ID);
		
		double val = data.getSkillLevel(this);
		
		if(mod == null || mod.getAmount() != val)
		{
			if(mod != null)
				hp.removeModifier(HP_ID);
			hp.applyModifier(new AttributeModifier(HP_ID, "IS3 Health", val, 0));
		}
		
		if(data.player.getHealth() > hp.getAttributeValue())
			data.player.setHealth(data.player.getHealth());
	}
}