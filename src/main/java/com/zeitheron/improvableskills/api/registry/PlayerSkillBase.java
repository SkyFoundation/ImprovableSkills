package com.zeitheron.improvableskills.api.registry;

import com.zeitheron.hammercore.utils.XPUtil;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.api.SkillTex;
import com.zeitheron.improvableskills.api.loot.SkillLoot;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class PlayerSkillBase extends IForgeRegistryEntry.Impl<PlayerSkillBase>
{
	private SkillLoot loot;
	public SkillTex<PlayerSkillBase> tex = new SkillTex(this);
	public double xpValue = 1;
	public int maxLvl;
	protected boolean hasScroll, genScroll;
	
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
	
	public String getLocalizedName()
	{
		return I18n.format(getUnlocalizedName() + ".name");
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
		return clvl < maxLvl && (XPUtil.getXPTotal(data.player) >= getXPToUpgrade(data, (short) (clvl + 1)) || data.player.capabilities.isCreativeMode);
	}
	
	public void onUpgrade(short oldLvl, short newLvl, PlayerSkillData data)
	{
		if(oldLvl > newLvl)
			XPUtil.setPlayersExpTo(data.player, XPUtil.getXPTotal(data.player) + getXPToDowngrade(data, newLvl));
		else
			XPUtil.setPlayersExpTo(data.player, XPUtil.getXPTotal(data.player) - getXPToUpgrade(data, newLvl));
	}
	
	public boolean isDowngradable(PlayerSkillData data)
	{
		return true;
	}
	
	public int getXPToDowngrade(PlayerSkillData data, short to)
	{
		return getXPToUpgrade(data, (short) to);
	}
	
	public void onDowngrade(PlayerSkillData data, short from)
	{
		
	}
	
	public EnumScrollState getScrollState()
	{
		return hasScroll ? maxLvl == 1 ? EnumScrollState.SPECIAL : EnumScrollState.NORMAL : EnumScrollState.NONE;
	}
	
	public SkillLoot getLoot()
	{
		return hasScroll && genScroll ? loot == null ? (loot = new SkillLoot(this)) : loot : null;
	}
	
	public boolean isVisible(PlayerSkillData data)
	{
		return !hasScroll || data.stat_scrolls.contains(getRegistryName().toString());
	}
	
	public int getColor()
	{
		return getRegistryName().toString().hashCode();
	}
	
	public static enum EnumScrollState
	{
		NONE, NORMAL, SPECIAL;
		
		public boolean hasScroll()
		{
			return ordinal() > 0;
		}
	}
}