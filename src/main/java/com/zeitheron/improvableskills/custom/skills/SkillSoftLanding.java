package com.zeitheron.improvableskills.custom.skills;

import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.registry.PlayerSkillBase;

public class SkillSoftLanding extends PlayerSkillBase
{
	public SkillSoftLanding()
	{
		super(10);
		setRegistryName(InfoIS.MOD_ID, "soft_landing");
		xpCalculator.xpValue = 2;
	}
}