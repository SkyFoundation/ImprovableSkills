package com.zeitheron.improvableskills.api.treasures.drops;

import java.util.List;

import com.zeitheron.hammercore.utils.WorldLocation;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.api.treasures.TreasureContext;
import com.zeitheron.improvableskills.api.treasures.TreasureDropBase;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTableList;

public class TreasureSandDropLootTableItem extends TreasureDropBase
{
	public ResourceLocation dropTable = LootTableList.CHESTS_DESERT_PYRAMID;
	public int minLvl;
	
	public TreasureSandDropLootTableItem()
	{
	}
	
	public TreasureSandDropLootTableItem(ResourceLocation table, int minLvl)
	{
		this.dropTable = table;
		this.minLvl = minLvl;
	}
	
	@Override
	public void drop(TreasureContext ctx, List<ItemStack> drops)
	{
		PlayerSkillData data = ctx.getData();
		WorldLocation loc = ctx.getLocation();
		
		LootContext.Builder builder = new LootContext.Builder((WorldServer) loc.getWorld()) //
		        .withPlayer(data.player) //
		        .withLuck(data.player.getLuck());
		
		List<ItemStack> gen = loc.getWorld() //
		        .getLootTableManager() //
		        .getLootTableFromLocation(dropTable) //
		        .generateLootForPools(data.player.getRNG(), builder.build());
		
		if(!gen.isEmpty())
		{
			drops.add(gen.get(data.player.getRNG().nextInt(gen.size())).copy());
			return;
		}
	}
	
	@Override
	public TreasureDropBase copy()
	{
		TreasureSandDropLootTableItem l = (TreasureSandDropLootTableItem) super.copy();
		l.dropTable = dropTable;
		l.minLvl = minLvl;
		return l;
	}
	
	@Override
	public boolean canDrop(TreasureContext ctx)
	{
		return ctx.getCaller() != null && ctx.getCaller().getRegistryName().toString().equals("improvableskills:treasure_sands") && ctx.getData().getSkillLevel(ctx.getCaller()) >= minLvl;
	}
}