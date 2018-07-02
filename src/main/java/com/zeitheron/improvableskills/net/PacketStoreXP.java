package com.zeitheron.improvableskills.net;

import java.math.BigInteger;

import com.zeitheron.hammercore.net.IPacket;
import com.zeitheron.hammercore.net.PacketContext;
import com.zeitheron.hammercore.utils.XPUtil;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.data.PlayerDataManager;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

public class PacketStoreXP implements IPacket
{
	public int xp;
	
	static
	{
		IPacket.handle(PacketStoreXP.class, PacketStoreXP::new);
	}
	
	public PacketStoreXP(int xp)
	{
		this.xp = xp;
	}
	
	public PacketStoreXP()
	{
	}
	
	@Override
	public IPacket executeOnServer(PacketContext net)
	{
		EntityPlayerMP player = net.getSender();
		
		PlayerDataManager.saveQuitting(player);
		PlayerDataManager.loadLogging(player);
		
		PlayerSkillData data = PlayerDataManager.getDataFor(player);
		
		int cxp = XPUtil.getXPTotal(player);
		int xp = Math.min(this.xp, cxp);
		
		XPUtil.setPlayersExpTo(player, cxp - xp);
		
		data.storageXp = data.storageXp.add(new BigInteger(xp + ""));
		
		return new PacketSyncSkillData(data);
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