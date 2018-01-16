package com.endie.is.skills;

import com.endie.is.InfoIS;
import com.endie.is.api.PlayerSkillBase;
import com.endie.is.api.PlayerSkillData;

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