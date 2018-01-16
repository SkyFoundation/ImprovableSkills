package com.endie.is.api;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.endie.is.data.PlayerDataManager;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public class PlayerSkillData
{
	public static final Logger LOG = LogManager.getLogger("ImprovableSkills-IO");
	
	public BigInteger storageXp = BigInteger.ZERO;
	
	public final EntityPlayer player;
	public NBTTagCompound persistedData = new NBTTagCompound();
	private Map<String, Short> stats = new HashMap<>();
	
	public PlayerSkillData(EntityPlayer player)
	{
		this.player = player;
	}
	
	public short getSkillLevel(PlayerSkillBase stat)
	{
		return stats.getOrDefault(stat.getRegistryName().toString(), (short) 0);
	}
	
	public void handleTick()
	{
		List<PlayerSkillBase> skills = GameRegistry.findRegistry(PlayerSkillBase.class).getValues();
		for(int i = 0; i < skills.size(); ++i)
			skills.get(i).tick(this);
	}
	
	public void setSkillLevel(PlayerSkillBase stat, Number lvl)
	{
		setSkillLevel(stat, lvl, true);
	}
	
	private void setSkillLevel(PlayerSkillBase stat, Number lvl, boolean save)
	{
		stats.put(stat.getRegistryName().toString(), lvl.shortValue());
		if(save)
			PlayerDataManager.save(player);
	}
	
	public static PlayerSkillData deserialize(EntityPlayer player, NBTTagCompound nbt)
	{
		PlayerSkillData data = new PlayerSkillData(player);
		
		IForgeRegistry<PlayerSkillBase> reg = GameRegistry.findRegistry(PlayerSkillBase.class);
		NBTTagList lvls = nbt.getTagList("Levels", NBT.TAG_COMPOUND);
		for(int i = 0; i < lvls.tagCount(); ++i)
		{
			NBTTagCompound tag = lvls.getCompoundTagAt(i);
			String sstat = tag.getString("Id");
			
			PlayerSkillBase stat = reg.getValue(new ResourceLocation(sstat));
			
			if(stat == null)
			{
				LOG.warn("[LOAD] Skill '" + sstat + "' wasn't found. Maybe you removed the addon? Skipping unregistered skill.");
				continue;
			}
			
			data.setSkillLevel(stat, tag.getShort("Lvl"), false);
		}
		
		data.persistedData = nbt.getCompoundTag("Persisted");
		if(data.persistedData.hasKey("BankXP", NBT.TAG_STRING))
			try
			{
				data.storageXp = new BigInteger(data.persistedData.getString("BankXP"), 36);
			} catch(Throwable err)
			{
				data.storageXp = BigInteger.ZERO;
			}
		
		return data;
	}
	
	public NBTTagCompound serialize()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		
		persistedData.setString("BankXP", storageXp.toString(36));
		
		IForgeRegistry<PlayerSkillBase> reg = GameRegistry.findRegistry(PlayerSkillBase.class);
		nbt.setTag("Persisted", persistedData);
		NBTTagList list = new NBTTagList();
		for(String sstat : stats.keySet())
		{
			PlayerSkillBase stat = reg.getValue(new ResourceLocation(sstat));
			
			if(stat == null)
			{
				LOG.warn("[SAVE] Skill '" + sstat + "' wasn't found. Maybe you removed the addon? Skipping unregistered skill.");
				continue;
			}
			
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("Id", stat.getRegistryName().toString());
			tag.setShort("Lvl", getSkillLevel(stat));
			list.appendTag(tag);
		}
		nbt.setTag("Levels", list);
		
		return nbt;
	}
}