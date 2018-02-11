package com.endie.is.net;

import com.endie.is.api.PlayerSkillBase;
import com.endie.is.client.rendering.ItemToBookHandler;
import com.endie.is.client.rendering.OnTopEffects;
import com.endie.is.client.rendering.ote.OTEBook;
import com.endie.is.client.rendering.ote.OTEItemStack;
import com.endie.is.items.ItemSkillScroll;
import com.endie.is.proxy.SyncSkills;
import com.pengu.hammercore.net.packetAPI.iPacket;
import com.pengu.hammercore.net.packetAPI.iPacketListener;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketScrollUnlockedSkill implements iPacket, iPacketListener<PacketScrollUnlockedSkill, iPacket>
{
	private ResourceLocation skill;
	private int slot;
	
	public PacketScrollUnlockedSkill(ResourceLocation skill, int slot)
	{
		this.skill = skill;
		this.slot = slot;
	}
	
	public PacketScrollUnlockedSkill()
	{
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setString("s", skill.toString());
		nbt.setInteger("i", slot);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		skill = new ResourceLocation(nbt.getString("s"));
		slot = nbt.getInteger("i");
	}
	
	@Override
	public iPacket onArrived(PacketScrollUnlockedSkill packet, MessageContext context)
	{
		if(context.side == Side.CLIENT)
			packet.client();
		return null;
	}
	
	@SideOnly(Side.CLIENT)
	private void client()
	{
		PlayerSkillBase sk = GameRegistry.findRegistry(PlayerSkillBase.class).getValue(skill);
		Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("chat.improvableskills.page_unlocked", sk.getLocalizedName(SyncSkills.getData())));
		ItemToBookHandler.toBook(slot == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, ItemSkillScroll.of(sk), 100);
	}
}