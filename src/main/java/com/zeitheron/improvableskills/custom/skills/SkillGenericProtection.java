package com.zeitheron.improvableskills.custom.skills;

import java.util.UUID;

import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.api.registry.PlayerSkillBase;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.world.storage.loot.LootTableList;

public class SkillGenericProtection extends PlayerSkillBase
{
	public static final UUID PROTECTION_ID = UUID.fromString("8e56f8a6-a695-42d5-899b-89605f38cf80");
	
	public SkillGenericProtection()
	{
		super(20);
		setRegistryName(InfoIS.MOD_ID, "generic_protection");
		hasScroll = true;
		hasScroll = true;
		genScroll = true;
		
		getLoot().chance.n = 4;
		getLoot().setLootTable(LootTableList.CHESTS_SIMPLE_DUNGEON);
		
		xpCalculator.baseFormula = "%lvl%^2.75";
	}
	
	@Override
	public void tick(PlayerSkillData data)
	{
		IAttributeInstance hp = data.player.getEntityAttribute(SharedMonsterAttributes.ARMOR);
		
		hp.removeModifier(PROTECTION_ID);
		hp.applyModifier(new AttributeModifier(PROTECTION_ID, "IS3 Protection", data.getSkillLevel(this), 0));
	}
}