package com.zeitheron.improvableskills.net;

import com.zeitheron.hammercore.net.IPacket;
import com.zeitheron.hammercore.net.PacketContext;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.client.gui.GuiSkillsBook;
import com.zeitheron.improvableskills.data.PlayerDataManager;
import com.zeitheron.improvableskills.proxy.SyncSkills;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketOpenSkillsBook implements IPacket
{
	public NBTTagCompound nbt;
	
	static
	{
		IPacket.handle(PacketOpenSkillsBook.class, PacketOpenSkillsBook::new);
	}
	
	public PacketOpenSkillsBook(PlayerSkillData data)
	{
		nbt = data.serialize();
	}
	
	public PacketOpenSkillsBook()
	{
		nbt = new NBTTagCompound();
	}
	
	@Override
	public IPacket executeOnServer(PacketContext net)
	{
		return new PacketOpenSkillsBook(PlayerDataManager.getDataFor(net.getSender()));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IPacket executeOnClient(PacketContext net)
	{
		Minecraft mc = Minecraft.getMinecraft();
		SyncSkills.CLIENT_DATA = PlayerSkillData.deserialize(Minecraft.getMinecraft().player, nbt);
		mc.addScheduledTask(() -> mc.displayGuiScreen(new GuiSkillsBook(SyncSkills.getData())));
		return null;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		if(this.nbt != null)
			nbt.setTag("Data", this.nbt);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		this.nbt = nbt.getCompoundTag("Data");
	}
}