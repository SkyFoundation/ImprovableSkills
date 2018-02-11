package com.endie.is.skills;

import com.endie.is.InfoIS;
import com.endie.is.api.PlayerSkillBase;
import com.endie.is.api.PlayerSkillData;

public class SkillAtkDmgMelee extends PlayerSkillBase
{
	public SkillAtkDmgMelee()
	{
		super(15);
		setRegistryName(InfoIS.MOD_ID, "atkdmg_melee");
	}
	
	@Override
	public int getXPToUpgrade(PlayerSkillData data, short targetLvl)
	{
		return (int) Math.pow(targetLvl, 3);
	}
}