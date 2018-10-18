package com.zeitheron.improvableskills.api.registry;

import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.api.SkillTex;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class PlayerAbilityBase extends IForgeRegistryEntry.Impl<PlayerAbilityBase>
{
	public SkillTex<PlayerAbilityBase> tex = new SkillTex<PlayerAbilityBase>(this);
	
	@SideOnly(Side.CLIENT)
	public void onClickClient(EntityPlayer player, int mouseButton)
	{
	}
	
	public String getUnlocalizedName()
	{
		return "ability." + getRegistryName().toString();
	}
	
	public String getUnlocalizedName(PlayerSkillData data)
	{
		return getUnlocalizedName();
	}
	
	public String getLocalizedName(PlayerSkillData data)
	{
		return I18n.format(getUnlocalizedName(data) + ".name");
	}
	
	public String getLocalizedName()
	{
		return I18n.format(getUnlocalizedName() + ".name");
	}
	
	public String getUnlocalizedDesc(PlayerSkillData data)
	{
		return "ability." + getRegistryName().toString();
	}
	
	public String getLocalizedDesc(PlayerSkillData data)
	{
		return I18n.format(getUnlocalizedDesc(data) + ".desc");
	}
	
	public int getColor()
	{
		return getRegistryName().toString().hashCode();
	}
	
	public boolean isVisible(PlayerSkillData data)
	{
		return data.abilities.contains(getRegistryName().toString());
	}
}