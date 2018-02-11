package com.endie.is.api.treasures;

import java.util.ArrayList;
import java.util.Iterator;

public class TreasureRegistry
{
	private static final ArrayList<TreasureDropBase> allDrops = new ArrayList<>();
	
	public static <T extends TreasureDropBase> T registerDrop(T drop)
	{
		if(drop == null)
			return null;
		allDrops.add(drop);
		return drop;
	}
	
	public static ArrayList<TreasureDropBase> allDrops()
	{
		return allDrops;
	}
	
	public static TreasureDropBase[] allDropsArray()
	{
		return (TreasureDropBase[]) allDrops.toArray(new TreasureDropBase[allDrops.size()]);
	}
}