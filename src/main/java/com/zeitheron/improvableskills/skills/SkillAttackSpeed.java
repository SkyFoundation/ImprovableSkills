package com.zeitheron.improvableskills.skills;

import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.PlayerSkillBase;
import com.zeitheron.improvableskills.api.PlayerSkillData;

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