package com.endie.is.api.treasures;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;

public class DropUtil
{
	public static Random RANDOM = new Random();
	
	public static TreasureDropBase chooseDrop(TreasureContext ctx)
	{
		RANDOM = ctx.getRNG();
		return chooseDrop(TreasureRegistry.allDrops(), ctx);
	}
	
	public static TreasureDropBase chooseDrop(List<TreasureDropBase> allDrops, TreasureContext ctx)
	{
		List<TreasureDropBase> preDrops = new ArrayList();
		int lowestLuck = 0;
		for(TreasureDropBase d : allDrops)
			if(d.canDrop(ctx))
				preDrops.add(d.copy());
		float levelIncrease = 129.87013F;
		float weightTotal = 0F;
		ArrayList<Float> weightPoints = new ArrayList();
		weightPoints.add(0F);
		for(TreasureDropBase drop : preDrops)
		{
			weightTotal += drop.getChance() * 100;
			weightPoints.add(weightTotal);
		}
		float randomIndex = RANDOM.nextFloat() * weightTotal;
		TreasureDropBase chosenDrop = getDropByWeight(preDrops, weightPoints, randomIndex);
		return chosenDrop;
	}
	
	private static TreasureDropBase getDropByWeight(List<TreasureDropBase> drops, ArrayList<Float> weightPoints, float randomIndex)
	{
		for(int a = 0; a < drops.size(); a++)
			if((randomIndex >= ((Float) weightPoints.get(a)).floatValue()) && (randomIndex < ((Float) weightPoints.get(a + 1)).floatValue()))
				return drops.get(a);
		return null;
	}
}
