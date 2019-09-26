package com.zeitheron.improvableskills.custom.skills;

import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.registry.PlayerSkillBase;

public class SkillAtkDmgMelee extends PlayerSkillBase
{
	public SkillAtkDmgMelee()
	{
		super(15);
		setRegistryName(InfoIS.MOD_ID, "atkdmg_melee");
		
		xpCalculator.xpValue = 3;
	}
}