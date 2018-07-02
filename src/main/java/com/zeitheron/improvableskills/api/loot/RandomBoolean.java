package com.zeitheron.improvableskills.api.loot;

import java.util.Random;

public class RandomBoolean
{
	public Random rand;
	public int n;
	
	public RandomBoolean(Random rand)
	{
		this.rand = rand;
	}
	
	public RandomBoolean()
	{
		this(new Random());
	}
	
	public boolean get()
	{
		return rand.nextInt(n) == 0;
	}
	
	public boolean get(Random rand)
	{
		return rand.nextInt(n) == 0;
	}
}