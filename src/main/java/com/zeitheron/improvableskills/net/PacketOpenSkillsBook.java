package com.zeitheron.improvableskills.net;

import com.zeitheron.hammercore.lib.zlib.utils.Threading;
import com.zeitheron.hammercore.net.HCNet;
import com.zeitheron.hammercore.net.IPacket;
import com.zeitheron.hammercore.net.PacketContext;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.api.registry.PageletBase;
import com.zeitheron.improvableskills.client.gui.base.GuiTabbable;
import com.zeitheron.improvableskills.data.PlayerDataManager;
import com.zeitheron.improvableskills.proxy.SyncSkills;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketOpenSkillsBook implements IPacket
{
	public NBTTagCompound nbt;
	
	static
	{
		IPacket.handle(PacketOpenSkillsBook.class, PacketOpenSkillsBook::new);
	}
	
	public static void sync(EntityPlayerMP mp)
	{
		if(mp != null)
			PlayerDataManager.handleDataSafely(mp, data -> HCNet.INSTANCE.sendTo(new PacketOpenSkillsBook(data), mp));
	}
	
	PacketOpenSkillsBook(PlayerSkillData data)
	{
		nbt = data.serialize();
	}
	
	public PacketOpenSkillsBook()
	{
		nbt = new NBTTagCompound();
	}
	
	@Override
	public void executeOnServer2(PacketContext net)
	{
		sync(net.getSender());
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IPacket executeOnClient(PacketContext net)
	{
		Minecraft mc = Minecraft.getMinecraft();
		SyncSkills.CLIENT_DATA = PlayerSkillData.deserialize(Minecraft.getMinecraft().player, nbt);
		mc.addScheduledTask(() -> mc.displayGuiScreen(GuiTabbable.lastPagelet.createTab(SyncSkills.getData())));
		Threading.createAndStart(() -> GameRegistry.findRegistry(PageletBase.class).getValuesCollection().forEach(PageletBase::reload));
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