package com.zeitheron.improvableskills.custom.skills;

import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.api.IDigSpeedAffectorSkill;
import com.zeitheron.improvableskills.api.registry.PlayerSkillBase;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class SkillMining extends PlayerSkillBase implements IDigSpeedAffectorSkill
{
	public SkillMining()
	{
		super(25);
		setRegistryName(InfoIS.MOD_ID, "mining");
	}
	
	@Override
	public int getXPToUpgrade(PlayerSkillData data, short targetLvl)
	{
		return (int) Math.pow(targetLvl, 1.5) * 3;
	}
	
	@Override
	public float getDigMultiplier(ItemStack stack, BlockPos pos, PlayerSkillData data)
	{
		if(pos == null)
			return 0F;
		Block b = data.player.world.getBlockState(pos).getBlock();
		String obj = b.getHarvestTool(data.player.world.getBlockState(pos));
		if(stack.isEmpty() || obj == null)
			return 0.0F;
		boolean isQ = false;
		String[] strs = stack.getItem().getToolClasses(stack).toArray(new String[0]);
		for(String s : strs)
			if(s.equalsIgnoreCase("pickaxe"))
			{
				isQ = true;
				break;
			}
		return obj.equalsIgnoreCase("pickaxe") && isQ ? data.getSkillLevel(this) / 8F : 0F;
	}
}