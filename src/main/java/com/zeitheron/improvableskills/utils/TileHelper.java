package com.zeitheron.improvableskills.utils;

import java.util.ArrayList;
import java.util.List;

import com.zeitheron.hammercore.utils.WorldLocation;
import com.zeitheron.hammercore.utils.WorldUtil;

public class TileHelper
{
	public static <T> List<T> collectTiles(WorldLocation center, int rad, Class<T> type)
	{
		List<T> al = new ArrayList<>();
		for(int x = -rad; x <= rad; ++x)
			for(int y = -rad; y <= rad; ++y)
				for(int z = -rad; z <= rad; ++z)
				{
					T t = WorldUtil.cast(center.offset(x, y, z).getTile(), type);
					if(t != null)
						al.add(t);
				}
		return al;
	}
}