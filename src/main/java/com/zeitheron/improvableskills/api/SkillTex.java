package com.zeitheron.improvableskills.api;

import com.zeitheron.hammercore.client.utils.UV;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SkillTex
{
	public final PlayerSkillBase skill;
	public final ResourceLocation texNorm, texHov;
	
	public SkillTex(PlayerSkillBase skill)
	{
		this.skill = skill;
		ResourceLocation res = skill.getRegistryName();
		this.texNorm = new ResourceLocation(res.getResourceDomain(), "textures/skills/" + res.getResourcePath() + "_normal.png");
		this.texHov = new ResourceLocation(res.getResourceDomain(), "textures/skills/" + res.getResourcePath() + "_hovered.png");
	}
	
	@SideOnly(Side.CLIENT)
	public UV toUV(boolean hovered)
	{
		return new UV(hovered ? texHov : texNorm, 0, 0, 256, 256);
	}
}