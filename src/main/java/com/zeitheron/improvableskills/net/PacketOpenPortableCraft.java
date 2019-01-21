package com.zeitheron.improvableskills.net;

import com.zeitheron.hammercore.internal.GuiManager;
import com.zeitheron.hammercore.net.IPacket;
import com.zeitheron.hammercore.net.PacketContext;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.data.PlayerDataManager;
import com.zeitheron.improvableskills.init.AbilitiesIS;
import com.zeitheron.improvableskills.init.GuiHooksIS;

import net.minecraft.entity.player.EntityPlayerMP;

public class PacketOpenPortableCraft implements IPacket
{
	static
	{
		IPacket.handle(PacketOpenPortableCraft.class, PacketOpenPortableCraft::new);
	}
	
	@Override
	public IPacket executeOnServer(PacketContext net)
	{
		EntityPlayerMP mp = net.getSender();
		PlayerSkillData dat = PlayerDataManager.getDataFor(mp);
		if(dat != null && dat.abilities.contains(AbilitiesIS.CRAFTER.getRegistryName().toString()))
			GuiManager.openGuiCallback(GuiHooksIS.CRAFTING, mp, mp.world, mp.getPosition());
		return null;
	}
}