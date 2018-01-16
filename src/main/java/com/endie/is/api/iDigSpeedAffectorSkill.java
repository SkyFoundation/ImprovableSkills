package com.endie.is.api;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public interface iDigSpeedAffectorSkill
{
	float getDigMultiplier(ItemStack stack, BlockPos pos, PlayerSkillData data);
}