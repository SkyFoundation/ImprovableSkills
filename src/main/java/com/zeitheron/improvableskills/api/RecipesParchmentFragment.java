package com.zeitheron.improvableskills.api;

import java.util.ArrayList;
import java.util.List;

import com.zeitheron.hammercore.utils.ConsumableItem;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.oredict.OreIngredient;

public class RecipesParchmentFragment
{
	public static final List<RecipeParchmentFragment> RECIPES = new ArrayList<>();
	
	public static RecipeParchmentFragment register(ItemStack output, Object... recipe)
	{
		RecipeParchmentFragment r = new RecipeParchmentFragment(output, recipe);
		RECIPES.add(r);
		return r;
	}
	
	public static class RecipeParchmentFragment
	{
		public final List<ConsumableItem> itemsIn;
		public final ItemStack output;
		
		public RecipeParchmentFragment(ItemStack output, Object... ingredients)
		{
			this.output = output;
			
			List<ConsumableItem> items = new ArrayList<>();
			for(Object i : ingredients)
			{
				if(i instanceof Ingredient)
					items.add(new ConsumableItem(1, (Ingredient) i));
				if(i instanceof String)
					items.add(new ConsumableItem(1, new OreIngredient((String) i)));
				if(i instanceof ItemStack)
					items.add(new ConsumableItem(((ItemStack) i).getCount(), Ingredient.fromStacks((ItemStack) i)));
				if(i instanceof ItemStack[])
					items.add(new ConsumableItem(1, Ingredient.fromStacks((ItemStack[]) i)));
				if(i instanceof Block && Item.getItemFromBlock((Block) i) != null)
					items.add(new ConsumableItem(1, Ingredient.fromItems(Item.getItemFromBlock((Block) i))));
				if(i instanceof Item)
					items.add(new ConsumableItem(1, Ingredient.fromItems((Item) i)));
				if(i instanceof Item[])
					items.add(new ConsumableItem(1, Ingredient.fromItems((Item[]) i)));
			}
			itemsIn = items;
		}
	}
}