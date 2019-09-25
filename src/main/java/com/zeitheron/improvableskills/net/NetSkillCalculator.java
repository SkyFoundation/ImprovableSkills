package com.zeitheron.improvableskills.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.zeitheron.hammercore.net.transport.ITransportAcceptor;
import com.zeitheron.hammercore.net.transport.TransportSessionBuilder;
import com.zeitheron.improvableskills.ImprovableSkillsMod;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

public class NetSkillCalculator implements ITransportAcceptor
{
	@Override
	public void read(InputStream readable, int length)
	{
		try
		{
			NBTTagCompound nbt = CompressedStreamTools.readCompressed(readable);
			
			ImprovableSkillsMod.getSkills().forEach(skill -> skill.xpCalculator.readClientNBT(nbt.getCompoundTag("SkillCost" + skill.getRegistryName().toString())));
			
			ImprovableSkillsMod.LOG.info("Received server settings.");
		} catch(Throwable err)
		{
			err.printStackTrace();
		}
	}
	
	public static TransportSessionBuilder pack()
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		NBTTagCompound nbt = new NBTTagCompound();
		
		ImprovableSkillsMod.getSkills().forEach(skill ->
		{
			NBTTagCompound tag = new NBTTagCompound();
			skill.xpCalculator.writeServerNBT(tag);
			nbt.setTag("SkillCost" + skill.getRegistryName().toString(), tag);
		});
		
		try
		{
			CompressedStreamTools.writeCompressed(nbt, baos);
		} catch(IOException e)
		{
			e.printStackTrace();
		}
		return new TransportSessionBuilder().setAcceptor(NetSkillCalculator.class).addData(baos.toByteArray());
	}
}