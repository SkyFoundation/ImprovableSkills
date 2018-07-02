package com.zeitheron.improvableskills.net;

import com.zeitheron.hammercore.net.IPacket;
import com.zeitheron.hammercore.net.PacketContext;
import com.zeitheron.improvableskills.api.PlayerSkillBase;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.data.PlayerDataManager;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class PacketLvlDownSkill implements IPacket
{
	public ResourceLocation skill;
	
	static
	{
		IPacket.handle(PacketLvlDownSkill.class, PacketLvlDownSkill::new);
	}
	
	public PacketLvlDownSkill(PlayerSkillBase skill)
	{
		this.skill = skill.getRegistryName();
	}
	
	public PacketLvlDownSkill()
	{
	}
	
	@Override
	public IPacket executeOnServer(PacketContext net)
	{
		EntityPlayerMP player = net.getSender();
		
		PlayerDataManager.saveQuitting(player);
		PlayerDataManager.loadLogging(player);
		
		PlayerSkillData data = PlayerDataManager.getDataFor(player);
		PlayerSkillBase skill = GameRegistry.findRegistry(PlayerSkillBase.class).getValue(this.skill);
		short lvl = data.getSkillLevel(skill);
		
		if(skill != null && lvl > 0 && skill.isDowngradable(data))
		{
			data.setSkillLevel(skill, lvl - 1);
			skill.onUpgrade(lvl, (short) (lvl - 1), data);
			skill.onDowngrade(data, lvl);
			
			return new PacketSyncSkillData(data);
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