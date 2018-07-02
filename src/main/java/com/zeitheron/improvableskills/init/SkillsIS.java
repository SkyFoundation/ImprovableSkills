package com.zeitheron.improvableskills.init;

import com.zeitheron.improvableskills.ImprovableSkillsMod;
import com.zeitheron.improvableskills.api.PlayerSkillBase;
import com.zeitheron.improvableskills.cfg.ConfigsIS;
import com.zeitheron.improvableskills.skills.SkillAcceleratedFurnace;
import com.zeitheron.improvableskills.skills.SkillAlchemist;
import com.zeitheron.improvableskills.skills.SkillAtkDmgMelee;
import com.zeitheron.improvableskills.skills.SkillAtkDmgRanged;
import com.zeitheron.improvableskills.skills.SkillAttackSpeed;
import com.zeitheron.improvableskills.skills.SkillCutting;
import com.zeitheron.improvableskills.skills.SkillDigging;
import com.zeitheron.improvableskills.skills.SkillEnchanter;
import com.zeitheron.improvableskills.skills.SkillEnderManipulator;
import com.zeitheron.improvableskills.skills.SkillGenericProtection;
import com.zeitheron.improvableskills.skills.SkillGrowth;
import com.zeitheron.improvableskills.skills.SkillHealth;
import com.zeitheron.improvableskills.skills.SkillLadderKing;
import com.zeitheron.improvableskills.skills.SkillLeaper;
import com.zeitheron.improvableskills.skills.SkillLuckOfTheSea;
import com.zeitheron.improvableskills.skills.SkillMining;
import com.zeitheron.improvableskills.skills.SkillObsidianSkin;
import com.zeitheron.improvableskills.skills.SkillPVP;
import com.zeitheron.improvableskills.skills.SkillSoftLanding;
import com.zeitheron.improvableskills.skills.SkillTreasureSands;
import com.zeitheron.improvableskills.skills.SkillXPPlus;

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
	public static final SkillHealth HEALTH = new SkillHealth();
	public static final SkillGrowth GROWTH = new SkillGrowth();
	public static final SkillAlchemist ALCHEMIST = new SkillAlchemist();
	public static final SkillGenericProtection GENERIC_PROTECTION = new SkillGenericProtection();
	public static final SkillTreasureSands TREASURE_OF_SANDS = new SkillTreasureSands();
	public static final SkillAtkDmgMelee DAMAGE_MELEE = new SkillAtkDmgMelee();
	public static final SkillAtkDmgRanged DAMAGE_RANGED = new SkillAtkDmgRanged();
	public static final SkillPVP PVP = new SkillPVP();
	public static final SkillEnchanter ENCHANTER = new SkillEnchanter();
	public static final SkillEnderManipulator ENDER_MANIPULATOR = new SkillEnderManipulator();
	public static final SkillXPPlus XP_PLUS = new SkillXPPlus();
	
	public static IForgeRegistry<PlayerSkillBase> registry;
	
	public static void register(IForgeRegistry<PlayerSkillBase> reg)
	{
		registry = reg;
		
		if(ConfigsIS.configs.getBoolean("XP Storage", "Misc", true, "Should XP Bank be active in the book? Disabling this only hides the skill from the player."))
			reg.register(XP_STORAGE);
		
		register(ACCELERATED_FURNACE);
		register(LEAPER);
		register(LADDER_KING);
		register(SOFT_LANDING);
		register(ATTACK_SPEED);
		register(MINING);
		register(DIGGING);
		register(CUTTING);
		register(OBSIDIAN_SKIN);
		register(LUCK_OF_THE_SEA);
		register(HEALTH);
		register(GROWTH);
		register(ALCHEMIST);
		register(GENERIC_PROTECTION);
		register(TREASURE_OF_SANDS);
		register(DAMAGE_MELEE);
		register(DAMAGE_RANGED);
		register(PVP);
		register(ENCHANTER);
		register(ENDER_MANIPULATOR);
		register(XP_PLUS);
		
		if(ConfigsIS.configs.hasChanged())
			ConfigsIS.configs.save();
	}
	
	public static void register(PlayerSkillBase skill)
	{
		boolean add = ConfigsIS.configs.getBoolean(skill.getRegistryName().toString(), "Skills", true, "Should Skill \"" + skill.getUnlocalizedName() + "\" be added to the game?");
		ImprovableSkillsMod.LOG.info("Checking state for " + skill.getRegistryName().toString() + ": " + add);
		if(add)
		{
			ImprovableSkillsMod.LOG.info("  -Registering");
			registry.register(skill);
		}
	}
}