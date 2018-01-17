package com.endie.is.init;

import com.endie.is.api.PlayerSkillBase;
import com.endie.is.skills.SkillAcceleratedFurnace;
import com.endie.is.skills.SkillAttackSpeed;
import com.endie.is.skills.SkillCutting;
import com.endie.is.skills.SkillDigging;
import com.endie.is.skills.SkillLadderKing;
import com.endie.is.skills.SkillLeaper;
import com.endie.is.skills.SkillLuckOfTheSea;
import com.endie.is.skills.SkillMining;
import com.endie.is.skills.SkillObsidianSkin;
import com.endie.is.skills.SkillSoftLanding;

import net.minecraftforge.registries.IForgeRegistry;

public class SkillsIS
{
	public static final PlayerSkillBase XP_STORAGE = new PlayerSkillBase(0).setRegistryName("xp_bank");
	public static final SkillAcceleratedFurnace ACCELERATED_FURNACE = new SkillAcceleratedFurnace();
	public static final SkillLeaper LEAPER = new SkillLeaper();
	public static final SkillLadderKing LADDER_KING = new SkillLadderKing();
	public static final SkillSoftLanding SOFT_LANDING = new SkillSoftLanding();
	public static final SkillAttackSpeed ATTACK_SPEED = new SkillAttackSpeed();
	public static final SkillMining MINING = new SkillMining();
	public static final SkillDigging DIGGING = new SkillDigging();
	public static final SkillCutting CUTTING = new SkillCutting();
	public static final SkillObsidianSkin OBSIDIAN_SKIN = new SkillObsidianSkin();
	public static final SkillLuckOfTheSea LUCK_OF_THE_SEA = new SkillLuckOfTheSea();
	
	public static void register(IForgeRegistry<PlayerSkillBase> reg)
	{
		reg.register(XP_STORAGE);
		reg.register(ACCELERATED_FURNACE);
		reg.register(LEAPER);
		reg.register(LADDER_KING);
		reg.register(SOFT_LANDING);
		reg.register(ATTACK_SPEED);
		reg.register(MINING);
		reg.register(DIGGING);
		reg.register(CUTTING);
		reg.register(OBSIDIAN_SKIN);
		reg.register(LUCK_OF_THE_SEA);
	}
}