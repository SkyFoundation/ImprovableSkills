package com.zeitheron.improvableskills.net;

import com.zeitheron.hammercore.net.HCNet;
import com.zeitheron.hammercore.net.IPacket;
import com.zeitheron.hammercore.net.PacketContext;
import com.zeitheron.hammercore.utils.WorldUtil;
import com.zeitheron.hammercore.utils.XPUtil;
import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.api.IGuiSkillDataConsumer;
import com.zeitheron.improvableskills.data.PlayerDataManager;
import com.zeitheron.improvableskills.proxy.SyncSkills;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketSyncSkillData implements IPacket
{
	public NBTTagCompound nbt;
	
	static
	{
		IPacket.handle(PacketSyncSkillData.class, PacketSyncSkillData::new);
	}
	
	public static void sync(EntityPlayerMP mp)
	{
		try
		{
			if(mp != null)
				PlayerDataManager.handleDataSafely(mp, data -> HCNet.INSTANCE.sendTo(new PacketSyncSkillData(data), mp));
		} catch(NullPointerException npe)
		{
			// networking issues, pretty unsure how to prevent.
		}
	}
	
	private PacketSyncSkillData(PlayerSkillData data)
	{
		nbt = data.serialize();
		nbt.setInteger("PlayerLocalXP", XPUtil.getXPTotal(data.player));
	}
	
	public PacketSyncSkillData()
	{
		nbt = new NBTTagCompound();
	}
	
	@Override
	public void executeOnServer2(PacketContext net)
	{
		sync(net.getSender());
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void executeOnClient2(PacketContext net)
	{
		IGuiSkillDataConsumer c = WorldUtil.cast(Minecraft.getMinecraft().currentScreen, IGuiSkillDataConsumer.class);
		SyncSkills.CLIENT_DATA = PlayerSkillData.deserialize(Minecraft.getMinecraft().player, nbt);
		if(c != null)
			c.applySkillData(SyncSkills.CLIENT_DATA);
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		
		// Prevent console pollution
		if(player == null)
			return;
		
		XPUtil.setPlayersExpTo(player, nbt.getInteger("PlayerLocalXP"));
		// This is not REQUIRED but preffered for mods that may use this tag
		player.getEntityData().setTag(InfoIS.NBT_DATA_TAG, nbt);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setTag("Data", this.nbt);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		this.nbt = nbt.getCompoundTag("Data");
	}
}