package com.endie.is.skills;

import com.endie.is.InfoIS;
import com.endie.is.api.PlayerSkillBase;
import com.endie.is.api.PlayerSkillData;
import com.endie.is.api.iDigSpeedAffectorSkill;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class SkillCutting extends PlayerSkillBase implements iDigSpeedAffectorSkill
{
	public SkillCutting()
	{
		super(25);
		setRegistryName(InfoIS.MOD_ID, "cutting");
	}
	
	@Override
	public int getXPToUpgrade(PlayerSkillData data, short targetLvl)
	{
		return (int) Math.pow(targetLvl, 1.5);
	}
	
	@Override
	public float getDigMultiplier(ItemStack stack, BlockPos pos, PlayerSkillData data)
	{
		Block b = data.player.world.getBlockState(pos).getBlock();
		String obj = b.getHarvestTool(data.player.world.getBlockState(pos));
		if(stack.isEmpty() || obj == null)
			return 0.0F;
		boolean isQ = false;
		String[] strs = stack.getItem().getToolClasses(stack).toArray(new String[0]);
		for(String s : strs)
			if(s.equalsIgnoreCase("axe"))
			{
				isQ = true;
				break;
			}
		return obj.equalsIgnoreCase("axe") && isQ ? data.getSkillLevel(this) / 8F : 0F;
	}
}