package com.zeitheron.improvableskills.init;

import static com.zeitheron.improvableskills.api.treasures.TreasureRegistry.registerDrop;

import java.util.Random;

import com.zeitheron.improvableskills.api.treasures.drops.Stackable;
import com.zeitheron.improvableskills.api.treasures.drops.TreasureSandDropItem;
import com.zeitheron.improvableskills.api.treasures.drops.TreasureSandDropLootTableItem;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootTableList;

public class TreasuresIS
{
	public static void register()
	{
		registerSandTreasures();
	}
	
	private static void registerSandTreasures()
	{
		registerDrop(new TreasureSandDropItem(1, Stackable.of(new ItemStack(Items.IRON_NUGGET), 1, 3))).setChance(.7F);
		registerDrop(new TreasureSandDropItem(1, new ItemStack(Items.ROTTEN_FLESH))).setChance(.8F);
		registerDrop(new TreasureSandDropItem(1, r -> new ItemStack(select(r, Items.STONE_SHOVEL, Items.STONE_PICKAXE), 1, 125 - r.nextInt(32)))).setChance(.2F);
		registerDrop(new TreasureSandDropItem(1, Stackable.of(new ItemStack(Items.BONE), 1, 3))).setChance(.65F);
		registerDrop(new TreasureSandDropItem(1, Items.COAL)).setChance(.72F);
		
		registerDrop(new TreasureSandDropItem(2, r -> new ItemStack(select(r, Items.IRON_SHOVEL, Items.IRON_PICKAXE, Items.IRON_SWORD), 1, 250 - r.nextInt(64)))).setChance(.25F);
		registerDrop(new TreasureSandDropItem(2, Stackable.of(new ItemStack(Items.GOLD_NUGGET), 1, 3))).setChance(.6F);
		registerDrop(new TreasureSandDropItem(2, r -> new ItemStack(r.nextBoolean() ? Items.CHAINMAIL_BOOTS : Items.CHAINMAIL_HELMET, 1, 160 - r.nextInt(69)))).setChance(.1F);
		
		registerDrop(new TreasureSandDropItem(3, new ItemStack(Items.GOLDEN_APPLE, 1, 0))).setChance(.15F);
		registerDrop(new TreasureSandDropItem(3, Stackable.of(new ItemStack(Items.DIAMOND), 1, 2))).setChance(.52F);
		registerDrop(new TreasureSandDropItem(3, Stackable.of(new ItemStack(Items.DYE, 1, 4), 3, 7))).setChance(.3F);
		registerDrop(new TreasureSandDropItem(3, new ItemStack(Items.GOLDEN_APPLE, 1, 1))).setChance(.001F);
		registerDrop(new TreasureSandDropLootTableItem(LootTableList.CHESTS_DESERT_PYRAMID, 3)).setChance(.45F);
	}
	
	public static final <T> T select(Random rand, T... vars)
	{
		return vars[rand.nextInt(vars.length)];
	}
}