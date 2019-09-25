package com.zeitheron.improvableskills.net;

import com.zeitheron.hammercore.net.IPacket;
import com.zeitheron.hammercore.net.PacketContext;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.api.registry.PlayerSkillBase;
import com.zeitheron.improvableskills.data.PlayerDataManager;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketSetExperience;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class PacketLvlUpSkill implements IPacket
{
	public ResourceLocation skill;
	
	static
	{
		IPacket.handle(PacketLvlUpSkill.class, PacketLvlUpSkill::new);
	}
	
	public PacketLvlUpSkill(PlayerSkillBase skill)
	{
		this.skill = skill.getRegistryName();
	}
	
	public PacketLvlUpSkill()
	{
	}
	
	@Override
	public void executeOnServer2(PacketContext net)
	{
		EntityPlayerMP player = net.getSender();
		
		PlayerDataManager.handleDataSafely(player, data ->
		{
			PlayerSkillBase skill = GameRegistry.findRegistry(PlayerSkillBase.class).getValue(this.skill);
			short lvl = data.getSkillLevel(skill);
			if(skill != null && skill.canUpgrade(data) && lvl < Short.MAX_VALUE - 1)
			{
				data.setSkillLevel(skill, lvl + 1);
				skill.onUpgrade(lvl, (short) (lvl + 1), data);
				player.connection.sendPacket(new SPacketSetExperience(player.experience, player.experienceTotal, player.experienceLevel));
				PacketSyncSkillData.sync(player);
			}
		});
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