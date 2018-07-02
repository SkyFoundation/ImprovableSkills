package com.zeitheron.improvableskills.api.treasures.drops;

import java.util.Random;

import net.minecraft.item.ItemStack;

@FunctionalInterface
public interface Stackable
{
	ItemStack transform(Random rand);
	
	public static Stackable of(ItemStack instance)
	{
		return r -> instance.copy();
	}
	
	public static Stackable of(ItemStack instance, int min, int max)
	{
		int mult = instance.getCount();
		return r ->
		{
			ItemStack ns = instance.copy();
			ns.setCount(mult * min + r.nextInt(max - min + 1));
			return ns;
		};
	}
}