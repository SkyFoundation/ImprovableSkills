package com.zeitheron.improvableskills.custom.skills;

import java.util.ArrayList;
import java.util.List;

import com.zeitheron.hammercore.utils.ListUtils;
import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.api.registry.PlayerSkillBase;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.common.Loader;

public class SkillGrowth extends PlayerSkillBase
{
	public static final List<String> blacklist = new ArrayList<>();
	
	public SkillGrowth()
	{
		super(20);
		setRegistryName(InfoIS.MOD_ID, "growth");
		hasScroll = true;
		genScroll = true;
		
		getLoot().chance.n = 4;
		getLoot().setLootTable(LootTableList.CHESTS_JUNGLE_TEMPLE);
	}
	
	@Override
	public int getXPToUpgrade(PlayerSkillData data, short targetLvl)
	{
		return (int) Math.pow(targetLvl, 3);
	}
	
	@Override
	public void tick(PlayerSkillData data)
	{
		short lvl = data.getSkillLevel(this);
		if(lvl > 0 && data.player.ticksExisted % ((maxLvl - lvl) * 3 + 80) == 0)
			growAround(data.player, 2 + lvl / 4, lvl / 2 + 2);
	}
	
	public static void growAround(Entity ent, int rad, int max)
	{
		World world = ent.world;
		List<BlockPos> positions = new ArrayList<>();
		
		for(int x = -rad; x <= rad; ++x)
			for(int z = -rad; z <= rad; ++z)
				for(int y = -rad / 2; y <= rad / 2; ++y)
				{
					BlockPos pos = ent.getPosition().add(x, y, z);
					IBlockState state = world.getBlockState(pos);
					Block b = state.getBlock();
					if(b instanceof IGrowable)
					{
						if(blacklist.contains(b.getRegistryName().toString().toLowerCase()))
							continue;
						IGrowable gr = (IGrowable) b;
						if(gr.canGrow(world, pos, state, world.isRemote) && gr.canUseBonemeal(world, world.rand, pos, state))
							positions.add(pos);
					}
				}
			
		positions = ListUtils.randomizeList(positions, ent.world.rand);
		
		int co = Math.min(ent.world.rand.nextInt(max), positions.size());
		for(int i = 0; i < co; ++i)
		{
			BlockPos pos = positions.remove(0);
			try
			{
				if(ItemDye.applyBonemeal(ItemStack.EMPTY, world, pos))
					world.playEvent(2005, pos, 0);
			} catch(Throwable e)
			{
				if(e instanceof IllegalArgumentException && e.getMessage().equalsIgnoreCase("invalid hand null") && Loader.isModLoaded("thebetweenlands"))
					try
					{
						if(world instanceof net.minecraft.world.WorldServer)
							if(ItemDye.applyBonemeal(ItemStack.EMPTY, world, pos, net.minecraftforge.common.util.FakePlayerFactory.getMinecraft((net.minecraft.world.WorldServer) world), EnumHand.MAIN_HAND))
								world.playEvent(2005, pos, 0);
					} catch(Throwable err)
					{
						err.printStackTrace();
					}
				else
					e.printStackTrace();
			}
		}
	}
}