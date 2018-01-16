package com.endie.is.skills;

import com.endie.is.InfoIS;
import com.endie.is.api.PlayerSkillBase;
import com.endie.is.api.PlayerSkillData;

public class SkillAttackSpeed extends PlayerSkillBase
{
	public SkillAttackSpeed()
	{
		super(25);
		setRegistryName(InfoIS.MOD_ID, "attack_speed");
	}
	
	@Override
	public int getXPToUpgrade(PlayerSkillData data, short targetLvl)
	{
		return (int) Math.pow(targetLvl, 1.5);
	}
	
	@Override
	public void tick(PlayerSkillData data)
	{
		data.player.ticksSinceLastSwing += Math.sqrt(data.getSkillLevel(this)) / 3;
	}
}