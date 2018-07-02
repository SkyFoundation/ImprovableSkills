package com.zeitheron.improvableskills.proxy;

import com.zeitheron.hammercore.net.HCNet;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.net.PacketSyncSkillData;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SyncSkills
{
	public static PlayerSkillData CLIENT_DATA;
	
	@SideOnly(Side.CLIENT)
	public static PlayerSkillData getData()
	{
		if(CLIENT_DATA == null || CLIENT_DATA.player != Minecraft.getMinecraft().player)
		{
			HCNet.INSTANCE.sendToServer(new PacketSyncSkillData());
			return CLIENT_DATA = new PlayerSkillData(Minecraft.getMinecraft().player);
		}
		return CLIENT_DATA;
	}
}