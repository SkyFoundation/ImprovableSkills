package com.endie.is.net;

import java.math.BigInteger;

import com.endie.is.api.PlayerSkillBase;
import com.endie.is.api.PlayerSkillData;
import com.endie.is.data.PlayerDataManager;
import com.pengu.hammercore.common.utils.XPUtil;
import com.pengu.hammercore.net.HCNetwork;
import com.pengu.hammercore.net.packetAPI.iPacket;
import com.pengu.hammercore.net.packetAPI.iPacketListener;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class PacketDrawXP implements iPacket, iPacketListener<PacketDrawXP, iPacket>
{
	public int xp;
	
	public PacketDrawXP(int xp)
	{
		this.xp = xp;
	}
	
	public PacketDrawXP()
	{
	}
	
	@Override
	public iPacket onArrived(PacketDrawXP packet, MessageContext context)
	{
		if(context.side == Side.SERVER)
		{
			EntityPlayerMP player = context.getServerHandler().player;
			
			PlayerDataManager.saveQuitting(player);
			PlayerDataManager.loadLogging(player);
			
			PlayerSkillData data = PlayerDataManager.getDataFor(player);
			
			int cxp = XPUtil.getXPTotal(player);
			BigInteger bi = data.storageXp.min(new BigInteger(packet.xp + ""));
			int xp = Math.max(bi.intValue(), 0);
			
			XPUtil.setPlayersExpTo(player, cxp + xp);
			
			data.storageXp = data.storageXp.subtract(new BigInteger(xp + ""));
			
			HCNetwork.manager.sendTo(new PacketSyncSkillData(data), player);
		}
		
		return null;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("xp", xp);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		xp = nbt.getInteger("xp");
	}
}