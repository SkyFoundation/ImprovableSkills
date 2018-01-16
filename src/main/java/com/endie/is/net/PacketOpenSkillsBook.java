package com.endie.is.net;

import com.endie.is.api.PlayerSkillData;
import com.endie.is.client.gui.GuiSkillBook;
import com.endie.is.proxy.SyncStats;
import com.pengu.hammercore.net.packetAPI.iPacket;
import com.pengu.hammercore.net.packetAPI.iPacketListener;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketOpenSkillsBook implements iPacket, iPacketListener<PacketOpenSkillsBook, iPacket>
{
	public NBTTagCompound nbt;
	
	public PacketOpenSkillsBook(PlayerSkillData data)
	{
		nbt = data.serialize();
	}
	
	public PacketOpenSkillsBook()
	{
	}
	
	@Override
	public iPacket onArrived(PacketOpenSkillsBook packet, MessageContext context)
	{
		if(context.side == Side.CLIENT)
			packet.client();
		return null;
	}
	
	@SideOnly(Side.CLIENT)
	public void client()
	{
		Minecraft mc = Minecraft.getMinecraft();
		SyncStats.CLIENT_DATA = PlayerSkillData.deserialize(Minecraft.getMinecraft().player, nbt);
		mc.addScheduledTask(() -> mc.displayGuiScreen(new GuiSkillBook(SyncStats.CLIENT_DATA)));
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