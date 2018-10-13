package com.zeitheron.improvableskills.skills;

import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.PlayerSkillBase;
import com.zeitheron.improvableskills.api.PlayerSkillData;

import net.minecraft.entity.MoverType;

public class SkillLadderKing extends PlayerSkillBase
{
	public SkillLadderKing()
	{
		super(15);
		setRegistryName(InfoIS.MOD_ID, "ladder_king");
	}
	
	@Override
	public int getXPToUpgrade(PlayerSkillData data, short targetLvl)
	{
		return (int) Math.pow(targetLvl, 2);
	}
	
	@Override
	public void tick(PlayerSkillData data)
	{
		if(data.player.isOnLadder() && !data.player.isSneaking())
		{
			float multiplier = data.getSkillLevel(this) / (float) maxLvl;
			if(!data.player.collidedHorizontally)
				multiplier *= 0.3F;
			data.player.move(MoverType.PISTON, 0.0D, data.player.motionY * multiplier, 0.0D);
		}
	}
}