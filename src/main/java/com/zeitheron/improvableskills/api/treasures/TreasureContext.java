package com.zeitheron.improvableskills.api.treasures;

import java.util.Random;

import javax.annotation.Nonnull;

import com.zeitheron.hammercore.utils.WorldLocation;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.api.registry.PlayerSkillBase;

public class TreasureContext
{
	private TreasureContext()
	{
	}
	
	private PlayerSkillBase caller;
	private PlayerSkillData data;
	private WorldLocation loc;
	private Random rand;
	
	public PlayerSkillBase getCaller()
	{
		return caller;
	}
	
	public PlayerSkillData getData()
	{
		return data;
	}
	
	public WorldLocation getLocation()
	{
		return loc;
	}
	
	@Nonnull
	public Random getRNG()
	{
		if(rand == null)
			rand = new Random();
		return rand;
	}
	
	public static class Builder
	{
		private PlayerSkillBase base;
		private PlayerSkillData data;
		private WorldLocation loc;
		private Random rng;
		
		public Builder withCaller(PlayerSkillBase base)
		{
			this.base = base;
			return this;
		}
		
		public Builder withData(PlayerSkillData data)
		{
			this.data = data;
			return this;
		}
		
		public Builder withLocation(WorldLocation loc)
		{
			this.loc = loc;
			return this;
		}
		
		public Builder withRNG(Random rng)
		{
			this.rng = rng;
			return this;
		}
		
		public TreasureContext build()
		{
			if(data == null)
				throw new IllegalStateException("Context must have data about caller!");
			TreasureContext c = new TreasureContext();
			c.caller = base;
			c.data = data;
			c.loc = loc;
			c.rand = rng;
			return c;
		}
	}
}