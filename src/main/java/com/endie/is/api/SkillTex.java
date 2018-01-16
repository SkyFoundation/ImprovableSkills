package com.endie.is.api;

import com.pengu.hammercore.client.UV;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SkillTex
{
	public final PlayerSkillBase skill;
	
	public SkillTex(PlayerSkillBase skill)
	{
		this.skill = skill;
	}
	
	@SideOnly(Side.CLIENT)
	public UV toUV(boolean hovered)
	{
		ResourceLocation res = skill.getRegistryName();
		return new UV(new ResourceLocation(res.getResourceDomain(), "textures/skills/" + res.getResourcePath() + (hovered ? "_hovered" : "_normal") + ".png"), 0, 0, 256, 256);
	}
}