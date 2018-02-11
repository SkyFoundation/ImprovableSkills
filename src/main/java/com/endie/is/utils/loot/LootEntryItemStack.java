package com.endie.is.utils.loot;

import java.util.Collection;
import java.util.Random;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.LootFunction;

public class LootEntryItemStack extends LootEntry
{
	protected final ItemStack item;
	protected final LootFunction[] functions;
	
	public LootEntryItemStack(ItemStack itemIn, int weightIn, int qualityIn, LootFunction[] functionsIn, LootCondition[] conditionsIn, String entryName)
	{
		super(weightIn, qualityIn, conditionsIn, entryName);
		this.item = itemIn;
		this.functions = functionsIn;
	}
	
	@Override
	public void addLoot(Collection<ItemStack> stacks, Random rand, LootContext context)
	{
		ItemStack itemstack = this.item.copy();
		
		for(LootFunction lootfunction : this.functions)
		{
			if(LootConditionManager.testAllConditions(lootfunction.getConditions(), rand, context))
			{
				itemstack = lootfunction.apply(itemstack, rand, context);
			}
		}
		
		if(!itemstack.isEmpty())
		{
			if(itemstack.getCount() < this.item.getItem().getItemStackLimit(itemstack))
			{
				stacks.add(itemstack);
			} else
			{
				int i = itemstack.getCount();
				
				while(i > 0)
				{
					ItemStack itemstack1 = itemstack.copy();
					itemstack1.setCount(Math.min(itemstack.getMaxStackSize(), i));
					i -= itemstack1.getCount();
					stacks.add(itemstack1);
				}
			}
		}
	}
	
	@Override
	protected void serialize(JsonObject json, JsonSerializationContext context)
	{
		if(this.functions != null && this.functions.length > 0)
		{
			json.add("functions", context.serialize(this.functions));
		}
		
		ResourceLocation resourcelocation = Item.REGISTRY.getNameForObject(this.item.getItem());
		
		if(resourcelocation == null)
		{
			throw new IllegalArgumentException("Can't serialize unknown item " + this.item);
		} else
		{
			json.addProperty("name", resourcelocation.toString());
		}
		
		json.addProperty("damage", item.getItemDamage());
		json.addProperty("count", item.getCount());
	}
}