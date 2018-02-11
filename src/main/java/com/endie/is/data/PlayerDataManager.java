package com.endie.is.data;

import static com.endie.is.InfoIS.NBT_DATA_TAG;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.endie.is.api.PlayerSkillData;
import com.endie.is.net.PacketSyncSkillData;
import com.endie.is.proxy.SyncSkills;
import com.mojang.authlib.GameProfile;
import com.pengu.hammercore.net.HCNetwork;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class PlayerDataManager
{
	private static final Map<String, PlayerSkillData> DATAS = new HashMap<>();
	
	private static ThreadLocal<EntityPlayer> LPLAYER = ThreadLocal.withInitial(() -> null);
	
	public static PlayerSkillData getDataFor(EntityPlayer player)
	{
		if(player == null)
			return null;
		if(player.world.isRemote)
			return SyncSkills.CLIENT_DATA;
		LPLAYER.set(player);
		return getDataFor(player.getGameProfile());
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
		
		EntityPlayer ep = LPLAYER.get();
		LPLAYER.set(null);
		
		if(DATAS.containsKey(u))
			return DATAS.get(u);
		
		if(LPLAYER.get() != null)
		{
			DATAS.put(u, new PlayerSkillData(ep));
			return DATAS.get(u);
		}
		
		return null;
	}
	
	public static void saveQuitting(EntityPlayer player)
	{
		save(player);
		DATAS.remove(player.getGameProfile().getId().toString());
	}
	
	public static boolean save(EntityPlayer player)
	{
		if(player == null)
			return false;
		PlayerSkillData data = getDataFor(player);
		if(data == null)
			return false;
		player.getEntityData().setTag(NBT_DATA_TAG, data.serialize());
		return true;
	}
	
	public static void loadLogging(EntityPlayer player)
	{
		DATAS.put(player.getGameProfile().getId().toString(), PlayerSkillData.deserialize(player, player.getEntityData().getCompoundTag(NBT_DATA_TAG)));
		if(player instanceof EntityPlayerMP)
			HCNetwork.manager.sendTo(new PacketSyncSkillData(getDataFor(player)), (EntityPlayerMP) player);
	}
}