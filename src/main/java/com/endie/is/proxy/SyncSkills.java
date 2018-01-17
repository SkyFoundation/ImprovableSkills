package com.endie.is.proxy;

import com.endie.is.api.PlayerSkillData;
import com.endie.is.net.PacketSyncSkillData;
import com.pengu.hammercore.net.HCNetwork;

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
			HCNetwork.manager.sendToServer(new PacketSyncSkillData());
			return CLIENT_DATA = new PlayerSkillData(Minecraft.getMinecraft().player);
		}
		return CLIENT_DATA;
	}
}