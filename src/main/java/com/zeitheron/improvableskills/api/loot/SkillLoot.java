package com.zeitheron.improvableskills.api.loot;

import java.util.function.Predicate;

import com.zeitheron.improvableskills.ImprovableSkillsMod;
import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.registry.PlayerSkillBase;
import com.zeitheron.improvableskills.init.SkillsIS;
import com.zeitheron.improvableskills.items.ItemSkillScroll;
import com.zeitheron.improvableskills.utils.loot.LootConditionSkillScroll;
import com.zeitheron.improvableskills.utils.loot.LootEntryItemStack;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.event.LootTableLoadEvent;

public class SkillLoot
{
	public final PlayerSkillBase skill;
	public Predicate<ResourceLocation> lootTableChecker = r -> false;
	public RandomBoolean chance = new RandomBoolean();
	
	public SkillLoot(PlayerSkillBase skill)
	{
		this.skill = skill;
	}
	
	public void setLootTable(ResourceLocation rl)
	{
		lootTableChecker = r -> r.equals(rl);
	}
	
	public void addLootTable(ResourceLocation rl)
	{
		lootTableChecker = lootTableChecker.or(r -> rl.equals(r));
	}
	
	public void apply(LootTableLoadEvent table)
	{
		if(lootTableChecker != null && lootTableChecker.test(table.getName()))
		{
			ImprovableSkillsMod.LOG.info("Injecting scroll for skill '" + skill.getRegistryName().toString() + "' into LootTable '" + table.getName() + "'!");
			LootEntry entry = new LootEntryItemStack(ItemSkillScroll.of(skill), 2, 60, new LootFunction[0], new LootCondition[0], InfoIS.MOD_ID + ":" + skill.getRegistryName().toString() + "_scroll");
			LootPool pool1 = new LootPool(new LootEntry[] { entry }, new LootCondition[] { new LootConditionSkillScroll(skill, chance) }, new RandomValueRange(1), new RandomValueRange(0, 1), skill.getRegistryName().toString() + "_skill_scroll");
			try
			{
				table.getTable().addPool(pool1);
			} catch(Throwable err)
			{
				ImprovableSkillsMod.LOG.error("Failed to inject scroll for skill '" + skill.getRegistryName().toString() + "' into LootTable '" + table.getName() + "'!!!");
				err.printStackTrace();
			}
		}
	}
}