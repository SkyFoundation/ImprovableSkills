package com.endie.is.skills;

import java.lang.reflect.Field;

import com.endie.is.InfoIS;
import com.endie.is.api.PlayerSkillBase;
import com.endie.is.api.PlayerSkillData;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;

public class SkillLuckOfTheSea extends PlayerSkillBase
{
	public SkillLuckOfTheSea()
	{
		super(15);
		setRegistryName(InfoIS.MOD_ID, "luck_of_the_sea");
	}
	
	@Override
	public int getXPToUpgrade(PlayerSkillData data, short targetLvl)
	{
		return (int) Math.pow(targetLvl, 2);
	}
	
	@Override
	public void tick(PlayerSkillData data)
	{
		EntityPlayer player = data.player;
		EntityFishHook hook = player.fishEntity;
		int level = data.getSkillLevel(this);
		if(hook != null && !hook.isDead && hook.isInWater())
		{
			try
			{
				Field f = EntityFishHook.class.getDeclaredFields()[7];
				Field tcd = EntityFishHook.class.getDeclaredFields()[8];
				f.setAccessible(true);
				tcd.setAccessible(true);
				if(hook.ticksExisted > 100 && f.getInt(hook) > 0 && player.getRNG().nextInt(maxLvl - level + 1) * 10 == 0)
				{
					f.setInt(hook, f.getInt(hook) + 40);
				}
				if(hook.ticksExisted % 2 == 0 && tcd.getInt(hook) > 0 && player.getRNG().nextInt(maxLvl - level + 1) * 100 == 0)
				{
					tcd.setInt(hook, 1);
				}
			} catch(Throwable localThrowable)
			{
			}
		}
		if((hook != null) && (!hook.isDead))
		{
			player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.LUCK).setBaseValue(level * 5.0D);
		} else if((hook == null) || (hook.isDead))
		{
			player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.LUCK).setBaseValue(0.0D);
		}
	}
}