package com.zeitheron.improvableskills.api.treasures;

import java.util.List;

import net.minecraft.item.ItemStack;

public abstract class TreasureDropBase implements DropCondition
{
	private float chance = 1.0F;
	
	public float getChance()
	{
		return chance;
	}
	
	public void setChance(float newChance)
	{
		chance = newChance;
	}
	
	public void drop(TreasureContext ctx, List<ItemStack> drops)
	{
	}
	
	public TreasureDropBase copy()
	{
		try
		{
			TreasureDropBase l = (TreasureDropBase) getClass().newInstance();
			l.chance = chance;
			return l;
		} catch(Throwable localThrowable)
		{
		}
		return null;
	}
}