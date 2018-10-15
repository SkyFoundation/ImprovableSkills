package com.zeitheron.improvableskills.api;

import com.zeitheron.hammercore.client.utils.UV;
import com.zeitheron.improvableskills.api.registry.PlayerAbilityBase;
import com.zeitheron.improvableskills.api.registry.PlayerSkillBase;
import com.zeitheron.improvableskills.custom.abilities.PlayerAbilityEnchanting;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class SkillTex<V extends IForgeRegistryEntry<V>>
{
	public final V skill;
	public ResourceLocation texNorm, texHov;
	
	public SkillTex(V skill)
	{
		this.skill = skill;
	}
	
	@SideOnly(Side.CLIENT)
	public UV toUV(boolean hovered)
	{
		if(texHov == null || texNorm == null)
		{
			ResourceLocation res = skill.getRegistryName();
			
			String sub = "skills";
			if(skill instanceof PlayerAbilityBase)
				sub = "abilities";
			
			this.texNorm = new ResourceLocation(res.getNamespace(), "textures/" + sub + "/" + res.getPath() + "_normal.png");
			this.texHov = new ResourceLocation(res.getNamespace(), "textures/" + sub + "/" + res.getPath() + "_hovered.png");
		}
		
		return new UV(hovered ? texHov : texNorm, 0, 0, 256, 256);
	}
}