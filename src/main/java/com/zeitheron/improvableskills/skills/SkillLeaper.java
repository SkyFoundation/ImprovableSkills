package com.zeitheron.improvableskills.skills;

import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.PlayerSkillBase;
import com.zeitheron.improvableskills.api.PlayerSkillData;

public class SkillLeaper extends PlayerSkillBase
{
	public SkillLeaper()
	{
		super(15);
		setRegistryName(InfoIS.MOD_ID, "leaper");
	}
	
	@Override
	public int getXPToUpgrade(PlayerSkillData data, short targetLvl)
	{
		return (int) Math.pow(targetLvl, 2);
	}
}