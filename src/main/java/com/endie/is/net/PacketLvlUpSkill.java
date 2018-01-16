package com.endie.is.net;

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

public class PacketLvlUpSkill implements iPacket, iPacketListener<PacketLvlUpSkill, iPacket>
{
	public ResourceLocation skill;
	
	public PacketLvlUpSkill(PlayerSkillBase skill)
	{
		this.skill = skill.getRegistryName();
	}
	
	public PacketLvlUpSkill()
	{
	}
	
	@Override
	public iPacket onArrived(PacketLvlUpSkill packet, MessageContext context)
	{
		if(context.side == Side.SERVER)
		{
			EntityPlayerMP player = context.getServerHandler().player;
			
			PlayerDataManager.saveQuitting(player);
			PlayerDataManager.loadLogging(player);
			
			PlayerSkillData data = PlayerDataManager.getDataFor(player);
			PlayerSkillBase skill = GameRegistry.findRegistry(PlayerSkillBase.class).getValue(packet.skill);
			short lvl = data.getSkillLevel(skill);
			if(skill != null && skill.canUpgrade(data) && lvl < Short.MAX_VALUE - 1)
			{
				data.setSkillLevel(skill, lvl + 1);
				skill.onUpgrade(lvl, (short) (lvl + 1), data);
				
				HCNetwork.manager.sendTo(new PacketSyncSkillData(data), player);
			}
		}
		
		return null;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setString("Skill", skill.toString());
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		skill = new ResourceLocation(nbt.getString("Skill"));
	}
}