package com.endie.is.api.treasures.drops;

import java.util.List;

import com.endie.is.api.treasures.TreasureContext;
import com.endie.is.api.treasures.TreasureDropBase;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class TreasureSandDropItem extends TreasureDropBase
{
	public NonNullList<Stackable> items = NonNullList.create();
	public int minLvl;
	
	public TreasureSandDropItem()
	{
	}
	
	public TreasureSandDropItem(int lvl, Stackable... items)
	{
		this.minLvl = lvl;
		
		for(Stackable s : items)
			this.items.add(s);
	}
	
	public TreasureSandDropItem(int lvl, Object... items)
	{
		this.minLvl = lvl;
		
		for(int i = 0; i < items.length; ++i)
		{
			Object o = items[i];
			
			if(o == null)
				throw new NullPointerException("Item at index " + i + " is null.");
			
			if(o instanceof Item)
				this.items.add(Stackable.of(new ItemStack((Item) o)));
			else if(o instanceof Block)
				this.items.add(Stackable.of(new ItemStack((Block) o)));
			else if(o instanceof ItemStack)
				this.items.add(Stackable.of(((ItemStack) o).copy()));
			else if(o instanceof Stackable)
				this.items.add((Stackable) o);
			else
				throw new IllegalArgumentException("Item at index " + i + " is not supported!");
		}
	}
	
	@Override
	public void drop(TreasureContext ctx, List<ItemStack> drops)
	{
		for(Stackable s : items)
			if(s != null)
				drops.add(s.transform(ctx.getRNG()));
	}
	
	@Override
	public TreasureDropBase copy()
	{
		TreasureSandDropItem l = (TreasureSandDropItem) super.copy();
		l.items = NonNullList.create();
		l.minLvl = minLvl;
		l.items.addAll(items);
		return this;
	}
	
	@Override
	public boolean canDrop(TreasureContext ctx)
	{
		return ctx.getCaller() != null && ctx.getCaller().getRegistryName().toString().equals("improvableskills:treasure_sands") && ctx.getData().getSkillLevel(ctx.getCaller()) >= minLvl;
	}
}