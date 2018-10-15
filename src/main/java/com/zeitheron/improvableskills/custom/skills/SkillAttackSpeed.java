package com.zeitheron.improvableskills.custom.skills;

import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.api.registry.PlayerSkillBase;

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