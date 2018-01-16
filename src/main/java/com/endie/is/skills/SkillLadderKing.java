package com.endie.is.skills;

import com.endie.is.InfoIS;
import com.endie.is.api.PlayerSkillBase;
import com.endie.is.api.PlayerSkillData;

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
			if(!data.player.isCollidedHorizontally)
				multiplier *= 0.3F;
			data.player.move(MoverType.PISTON, 0.0D, data.player.motionY * multiplier, 0.0D);
		}
	}
}