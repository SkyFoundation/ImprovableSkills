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
	public void executeOnServer2(PacketContext net)
	{
		EntityPlayerMP player = net.getSender();
		
		PlayerDataManager.handleDataSafely(player, data ->
		{
			int cxp = XPUtil.getXPTotal(player);
			int xp = Math.min(this.xp, cxp);
			XPUtil.setPlayersExpTo(player, cxp - xp);
			player.connection.sendPacket(new SPacketSetExperience(player.experience, player.experienceTotal, player.experienceLevel));
			data.storageXp = data.storageXp.add(new BigInteger(xp + ""));
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