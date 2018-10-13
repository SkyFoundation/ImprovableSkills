package com.zeitheron.improvableskills.api;

import com.zeitheron.hammercore.client.utils.UV;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SkillTex
{
	public final PlayerSkillBase skill;
	public ResourceLocation texNorm, texHov;
	
	public SkillTex(PlayerSkillBase skill)
	{
		this.skill = skill;
	}
	
	@SideOnly(Side.CLIENT)
	public UV toUV(boolean hovered)
	{
		if(texHov == null || texNorm == null)
		{
			ResourceLocation res = skill.getRegistryName();
			this.texNorm = new ResourceLocation(res.getNamespace(), "textures/skills/" + res.getPath() + "_normal.png");
			this.texHov = new ResourceLocation(res.getNamespace(), "textures/skills/" + res.getPath() + "_hovered.png");
		}
		
		return new UV(hovered ? texHov : texNorm, 0, 0, 256, 256);
	}
}