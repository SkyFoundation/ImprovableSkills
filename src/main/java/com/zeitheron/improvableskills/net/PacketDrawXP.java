package com.zeitheron.improvableskills.net;

import java.math.BigInteger;

import com.zeitheron.hammercore.net.IPacket;
import com.zeitheron.hammercore.net.PacketContext;
import com.zeitheron.hammercore.utils.XPUtil;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.data.PlayerDataManager;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketSetExperience;

public class PacketDrawXP implements IPacket
{
	public int xp;
	
	static
	{
		IPacket.handle(PacketDrawXP.class, PacketDrawXP::new);
	}
	
	public PacketDrawXP(int xp)
	{
		this.xp = xp;
	}
	
	public PacketDrawXP()
	{
	}
	
	@Override
	public void executeOnServer2(PacketContext net)
	{
		EntityPlayerMP player = net.getSender();
		
		PlayerDataManager.handleDataSafely(player, data ->
		{
			int cxp = XPUtil.getXPTotal(player);
			BigInteger bi = data.storageXp.min(new BigInteger(this.xp + ""));
			int xp = Math.max(bi.intValue(), 0);
			XPUtil.setPlayersExpTo(player, cxp + xp);
			player.connection.sendPacket(new SPacketSetExperience(player.experience, player.experienceTotal, player.experienceLevel));
			data.storageXp = data.storageXp.subtract(new BigInteger(xp + ""));
			PacketSyncSkillData.sync(player);
		});
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