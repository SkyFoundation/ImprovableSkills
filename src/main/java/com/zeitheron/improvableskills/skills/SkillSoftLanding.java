package com.zeitheron.improvableskills.skills;

import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.PlayerSkillBase;
import com.zeitheron.improvableskills.api.PlayerSkillData;

public class SkillSoftLanding extends PlayerSkillBase
{
	public SkillSoftLanding()
	{
		super(10);
		setRegistryName(InfoIS.MOD_ID, "soft_landing");
	}
	
	@Override
	public int getXPToUpgrade(PlayerSkillData data, short targetLvl)
	{
		return (int) Math.pow(targetLvl, 2);
	}
}