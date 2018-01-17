package com.endie.is.net;

import com.endie.is.InfoIS;
import com.endie.is.api.PlayerSkillData;
import com.endie.is.api.iGuiSkillDataConsumer;
import com.endie.is.data.PlayerDataManager;
import com.endie.is.proxy.SyncSkills;
import com.pengu.hammercore.common.utils.WorldUtil;
import com.pengu.hammercore.common.utils.XPUtil;
import com.pengu.hammercore.net.packetAPI.iPacket;
import com.pengu.hammercore.net.packetAPI.iPacketListener;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketSyncSkillData implements iPacket, iPacketListener<PacketSyncSkillData, iPacket>
{
	public NBTTagCompound nbt;
	
	public PacketSyncSkillData(PlayerSkillData data)
	{
		nbt = data.serialize();
		nbt.setInteger("PlayerLocalXP", XPUtil.getXPTotal(data.player));
	}
	
	public PacketSyncSkillData()
	{
		nbt = new NBTTagCompound();
	}
	
	@Override
	public iPacket onArrived(PacketSyncSkillData packet, MessageContext context)
	{
		if(context.side == Side.SERVER)
			return new PacketSyncSkillData(PlayerDataManager.getDataFor(context.getServerHandler().player));
		if(context.side == Side.CLIENT)
			packet.client();
		return null;
	}
	
	@SideOnly(Side.CLIENT)
	public void client()
	{
		iGuiSkillDataConsumer c = WorldUtil.cast(Minecraft.getMinecraft().currentScreen, iGuiSkillDataConsumer.class);
		SyncSkills.CLIENT_DATA = PlayerSkillData.deserialize(Minecraft.getMinecraft().player, nbt);
		SyncSkills.CLIENT_DATA.player.getEntityData().setTag(InfoIS.NBT_DATA_TAG, nbt);
		if(c != null)
			c.applySkillData(SyncSkills.CLIENT_DATA);
		XPUtil.setPlayersExpTo(Minecraft.getMinecraft().player, nbt.getInteger("PlayerLocalXP"));
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