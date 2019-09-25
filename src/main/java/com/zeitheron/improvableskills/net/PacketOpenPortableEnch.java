package com.zeitheron.improvableskills.net;

import java.util.HashMap;
import java.util.Map;

import com.zeitheron.hammercore.internal.GuiManager;
import com.zeitheron.hammercore.net.IPacket;
import com.zeitheron.hammercore.net.PacketContext;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.data.PlayerDataManager;
import com.zeitheron.improvableskills.init.AbilitiesIS;
import com.zeitheron.improvableskills.init.GuiHooksIS;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

public class PacketOpenPortableEnch implements IPacket
{
	public static final Map<String, Integer> COLORS = new HashMap<>();
	
	static
	{
		IPacket.handle(PacketOpenPortableEnch.class, PacketOpenPortableEnch::new);
	}
	
	int color;
	
	public PacketOpenPortableEnch(int color)
	{
		this.color = color;
	}
	
	public PacketOpenPortableEnch()
	{
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("C", color);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		color = nbt.getInteger("C");
	}
	
	@Override
	public void executeOnServer2(PacketContext net)
	{
		EntityPlayerMP mp = net.getSender();
		PlayerDataManager.handleDataSafely(mp, dat ->
		{
			if(dat.abilities.contains(AbilitiesIS.ENCHANTING.getRegistryName().toString()))
				GuiManager.openGuiCallback(GuiHooksIS.ENCHANTMENT, mp, mp.world, mp.getPosition());
			COLORS.put(mp.getGameProfile().getName(), color);
		});
	}
}