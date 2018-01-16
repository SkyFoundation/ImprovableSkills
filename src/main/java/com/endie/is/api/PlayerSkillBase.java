package com.endie.is.api;

import com.pengu.hammercore.common.utils.XPUtil;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class PlayerSkillBase extends IForgeRegistryEntry.Impl<PlayerSkillBase>
{
	public final SkillTex tex = new SkillTex(this);
	public double xpValue = 1;
	public int maxLvl;
	
	public PlayerSkillBase(int maxLvl)
	{
		this.maxLvl = maxLvl;
	}
	
	public void tick(PlayerSkillData data)
	{
		
	}
	
	public String getUnlocalizedName()
	{
		return "skill." + getRegistryName().toString();
	}
	
	public String getUnlocalizedName(PlayerSkillData data)
	{
		return getUnlocalizedName();
	}
	
	public String getLocalizedName(PlayerSkillData data)
	{
		return I18n.format(getUnlocalizedName(data) + ".name");
	}
	
	public String getUnlocalizedDesc(PlayerSkillData data)
	{
		return "skill." + getRegistryName().toString();
	}
	
	public String getLocalizedDesc(PlayerSkillData data)
	{
		return I18n.format(getUnlocalizedDesc(data) + ".desc");
	}
	
	public int getXPToUpgrade(PlayerSkillData data, short targetLvl)
	{
		return (int) Math.pow(targetLvl + 1, xpValue);
	}
	
	public boolean canUpgrade(PlayerSkillData data)
	{
		short clvl = data.getSkillLevel(this);
		return clvl < maxLvl && XPUtil.getXPTotal(data.player) >= getXPToUpgrade(data, (short) (clvl + 1));
	}
	
	public void onUpgrade(short oldLvl, short newLvl, PlayerSkillData data)
	{
		int xp = getXPToUpgrade(data, newLvl);
		if(oldLvl > newLvl)
			xp = -xp;
		XPUtil.setPlayersExpTo(data.player, XPUtil.getXPTotal(data.player) - xp);
	}
}