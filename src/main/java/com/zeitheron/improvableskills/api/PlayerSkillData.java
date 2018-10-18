package com.zeitheron.improvableskills.api;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.zeitheron.hammercore.HammerCore;
import com.zeitheron.hammercore.net.HCNet;
import com.zeitheron.improvableskills.ImprovableSkillsMod;
import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.registry.PlayerSkillBase;
import com.zeitheron.improvableskills.data.PlayerDataManager;
import com.zeitheron.improvableskills.net.PacketSyncSkillData;
import com.zeitheron.improvableskills.proxy.SyncSkills;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public class PlayerSkillData
{
	public static final Logger LOG = LogManager.getLogger("ImprovableSkills-IO");
	
	public BigInteger storageXp = BigInteger.ZERO;
	
	public final EntityPlayer player;
	public NBTTagCompound persistedData = new NBTTagCompound();
	/** The array of scrolls that have been used */
	public List<String> stat_scrolls = new ArrayList<>();
	public List<String> abilities = new ArrayList<>();
	public boolean hasCraftedSkillBook = false;
	private boolean hcsbPrev = false;
	public Map<String, Short> stats = new HashMap<>();
	
	public float enchantPower = 0;
	
	public EntityPlayer getPlayer()
	{
		if(this == SyncSkills.CLIENT_DATA)
			return HammerCore.renderProxy.getClientPlayer();
		return player;
	}
	
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
		long start = System.currentTimeMillis();
		
		if(player == null)
			return;
		
		// stat_scrolls.clear();
		
		Map<String, Long> updates = new HashMap<>();
		
		List<PlayerSkillBase> skills = GameRegistry.findRegistry(PlayerSkillBase.class).getValues();
		for(int i = 0; i < skills.size(); ++i)
		{
			long start0 = System.currentTimeMillis();
			skills.get(i).tick(this);
			updates.put(skills.get(i).getRegistryName().toString(), System.currentTimeMillis() - start0);
		}
		
		if(!player.world.isRemote && hcsbPrev != hasCraftedSkillBook && !hcsbPrev)
		{
			player.sendMessage(new TextComponentTranslation("chat." + InfoIS.MOD_ID + ".guide"));
			hcsbPrev = true;
			sync();
		}
		
		hcsbPrev = hasCraftedSkillBook;
		
		long end = System.currentTimeMillis();
		
		if(end - start > 100L)
			ImprovableSkillsMod.LOG.warn("Skill tick took too long! (" + (end - start) + "ms, expected <100 ms!). Time map: " + updates);
	}
	
	public void sync()
	{
		if(player instanceof EntityPlayerMP && !player.world.isRemote)
		{
			HCNet.INSTANCE.sendTo(new PacketSyncSkillData(this), (EntityPlayerMP) player);
			save();
		}
	}
	
	public void save()
	{
		if(player != null)
			PlayerDataManager.save(player);
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
	
	public boolean hasCraftedSkillsBook()
	{
		return hasCraftedSkillBook;
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
		
		NBTTagList list = nbt.getTagList("Scrolls", NBT.TAG_STRING);
		for(int i = 0; i < list.tagCount(); ++i)
			data.stat_scrolls.add(list.getStringTagAt(i));
		
		list = nbt.getTagList("Abilities", NBT.TAG_STRING);
		for(int i = 0; i < list.tagCount(); ++i)
			data.abilities.add(list.getStringTagAt(i));
		
		data.enchantPower = nbt.getFloat("EnchantPower");
		
		data.persistedData = nbt.getCompoundTag("Persisted");
		if(data.persistedData.hasKey("BankXP", NBT.TAG_STRING))
			try
			{
				data.storageXp = new BigInteger(data.persistedData.getString("BankXP"), 36);
			} catch(Throwable err)
			{
				data.storageXp = BigInteger.ZERO;
			}
		
		data.hasCraftedSkillBook = data.persistedData.getBoolean("SkillBookCrafted");
		data.hcsbPrev = data.persistedData.getBoolean("PrevSkillBookCrafted");
		
		return data;
	}
	
	public NBTTagCompound serialize()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		
		persistedData.setString("BankXP", storageXp.toString(36));
		persistedData.setBoolean("SkillBookCrafted", hasCraftedSkillBook);
		persistedData.setBoolean("PrevSkillBookCrafted", hcsbPrev);
		
		IForgeRegistry<PlayerSkillBase> reg = GameRegistry.findRegistry(PlayerSkillBase.class);
		nbt.setTag("Persisted", persistedData);
		nbt.setFloat("EnchantPower", enchantPower);
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
		
		list = new NBTTagList();
		for(String scroll : stat_scrolls)
			list.appendTag(new NBTTagString(scroll));
		nbt.setTag("Scrolls", list);
		
		list = new NBTTagList();
		for(String scroll : abilities)
			list.appendTag(new NBTTagString(scroll));
		nbt.setTag("Abilities", list);
		
		return nbt;
	}
}