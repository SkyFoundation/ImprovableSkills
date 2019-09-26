package com.zeitheron.improvableskills.custom.skills;

import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.api.registry.PlayerSkillBase;

public class SkillLeaper extends PlayerSkillBase
{
	public SkillLeaper()
	{
		super(15);
		setRegistryName(InfoIS.MOD_ID, "leaper");
		
		xpCalculator.xpValue = 2;
	}
}