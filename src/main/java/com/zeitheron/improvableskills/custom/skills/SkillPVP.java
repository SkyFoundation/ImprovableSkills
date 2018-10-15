package com.zeitheron.improvableskills.custom.skills;

import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.api.registry.PlayerSkillBase;

public class SkillPVP extends PlayerSkillBase
{
	public SkillPVP()
	{
		super(20);
		setRegistryName(InfoIS.MOD_ID, "pvp");
	}
	
	@Override
	public int getXPToUpgrade(PlayerSkillData data, short targetLvl)
	{
		return (int) Math.pow(targetLvl, 2);
	}
}