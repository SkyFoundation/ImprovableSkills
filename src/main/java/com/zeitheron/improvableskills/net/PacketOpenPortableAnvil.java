package com.zeitheron.improvableskills.net;

import com.zeitheron.hammercore.internal.GuiManager;
import com.zeitheron.hammercore.net.IPacket;
import com.zeitheron.hammercore.net.PacketContext;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.data.PlayerDataManager;
import com.zeitheron.improvableskills.init.AbilitiesIS;
import com.zeitheron.improvableskills.init.GuiHooksIS;

import net.minecraft.entity.player.EntityPlayerMP;

public class PacketOpenPortableAnvil implements IPacket
{
	static
	{
		IPacket.handle(PacketOpenPortableAnvil.class, PacketOpenPortableAnvil::new);
	}
	
	@Override
	public void executeOnServer2(PacketContext net)
	{
		EntityPlayerMP mp = net.getSender();
		PlayerDataManager.handleDataSafely(mp, dat ->
		{
			if(dat.abilities.contains(AbilitiesIS.ANVIL.getRegistryName().toString()))
				GuiManager.openGuiCallback(GuiHooksIS.ANVIL, mp, mp.world, mp.getPosition());
		});
	}
}