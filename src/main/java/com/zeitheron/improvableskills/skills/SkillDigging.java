package com.zeitheron.improvableskills.skills;

import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.PlayerSkillBase;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.api.iDigSpeedAffectorSkill;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class SkillDigging extends PlayerSkillBase implements iDigSpeedAffectorSkill
{
	public SkillDigging()
	{
		super(25);
		setRegistryName(InfoIS.MOD_ID, "digging");
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
			if(s.equalsIgnoreCase("spade") || s.equalsIgnoreCase("shovel"))
			{
				isQ = true;
				break;
			}
		return (obj.equalsIgnoreCase("spade") || obj.equalsIgnoreCase("shovel")) && isQ ? data.getSkillLevel(this) / 28F : 0F;
	}
}