package com.zeitheron.improvableskills.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import com.mojang.authlib.GameProfile;
import com.zeitheron.improvableskills.ImprovableSkillsMod;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.proxy.SyncSkills;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

@EventBusSubscriber
public class PlayerDataManager
{
	public static final Map<String, PlayerSkillData> DATAS = new HashMap<>();
	
	private static ThreadLocal<EntityPlayer> LPLAYER = ThreadLocal.withInitial(() -> null);
	
	public static void handleDataSafely(EntityPlayer player, Consumer<PlayerSkillData> acceptor)
	{
		PlayerSkillData psd = getDataFor(player);
		if(psd != null)
			acceptor.accept(psd);
	}
	
	public static <T> T handleDataSafely(EntityPlayer player, Function<PlayerSkillData, T> acceptor, T defaultValue)
	{
		PlayerSkillData psd = getDataFor(player);
		if(psd != null)
			return acceptor.apply(psd);
		return defaultValue;
	}
	
	public static PlayerSkillData getDataFor(EntityPlayer player)
	{
		if(player == null || player instanceof FakePlayer)
			return null;
		if(player.world.isRemote)
			return SyncSkills.getData();
		LPLAYER.set(player);
		PlayerSkillData psd = getDataFor(player.getGameProfile());
		
		// Update player reference -- keep it up-to-date
		if(psd != null && psd.getPlayer() != player)
			DATAS.put(player.getGameProfile().getId().toString(), psd = PlayerSkillData.deserialize(player, psd.serialize()));
		
		return psd;
	}
	
	public static PlayerSkillData getDataFor(GameProfile player)
	{
		if(player == null)
			return null;
		return getDataFor(player.getId());
	}
	
	public static PlayerSkillData getDataFor(UUID player)
	{
		if(player == null)
			return null;
		String u = player.toString();
		if(DATAS.containsKey(u))
			return DATAS.get(u);
		EntityPlayer epl = LPLAYER.get();
		if(epl instanceof EntityPlayerMP)
		{
			EntityPlayerMP mp = (EntityPlayerMP) epl;
			
		}
		return null;
	}
	
	static final List<String> logoff = new ArrayList<String>();
	
	@SubscribeEvent
	public static void playerLoggedOut(PlayerLoggedOutEvent e)
	{
		logoff.add(e.player.getGameProfile().getId().toString());
	}
	
	@SubscribeEvent
	public static void loadPlayerFromFile(PlayerEvent.LoadFromFile e)
	{
		NBTTagCompound nbttagcompound = null;
		
		try
		{
			File file1 = e.getPlayerFile(".is3.dat");
			
			if(file1.exists() && file1.isFile())
			{
				nbttagcompound = CompressedStreamTools.readCompressed(new FileInputStream(file1));
			}
		} catch(Exception var4)
		{
			ImprovableSkillsMod.LOG.warn("Failed to load player data for {}", e.getEntityPlayer().getName());
		}
		
		if(nbttagcompound != null)
			DATAS.put(e.getPlayerUUID(), PlayerSkillData.deserialize(e.getEntityPlayer(), nbttagcompound));
		else
			DATAS.put(e.getPlayerUUID(), new PlayerSkillData(e.getEntityPlayer()));
	}
	
	@SubscribeEvent
	public static void savePlayerToFile(PlayerEvent.SaveToFile e)
	{
		PlayerSkillData data = getDataFor(e.getEntityPlayer());
		if(data == null)
			return;
		try
		{
			NBTTagCompound nbttagcompound = data.serialize();
			File file1 = e.getPlayerFile(".is3.dat.tmp");
			File file2 = e.getPlayerFile(".is3.dat");
			CompressedStreamTools.writeCompressed(nbttagcompound, new FileOutputStream(file1));
			
			if(file2.exists())
			{
				file2.delete();
			}
			
			file1.renameTo(file2);
		} catch(Exception var5)
		{
			ImprovableSkillsMod.LOG.warn("Failed to save player data for {}", e.getEntityPlayer().getName());
		}
	}
}