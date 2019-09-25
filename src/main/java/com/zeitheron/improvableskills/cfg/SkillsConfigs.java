package com.zeitheron.improvableskills.cfg;

import java.io.File;

import com.zeitheron.hammercore.cfg.file1132.Configuration;
import com.zeitheron.hammercore.cfg.file1132.io.ConfigEntryCategory;
import com.zeitheron.improvableskills.ImprovableSkillsMod;

public class SkillsConfigs
{
	private static File configFile;
	
	public static void setConfigFile(File configFile)
	{
		if(SkillsConfigs.configFile == null)
		{
			SkillsConfigs.configFile = configFile;
			reloadSkillConfigs();
		}
	}
	
	public static void reloadSkillConfigs()
	{
		final Configuration config = new Configuration(configFile);
		
		ConfigEntryCategory costs = config.getCategory("Costs").setDescription("Configure how expensive each skill is");
		
		ImprovableSkillsMod.getSkills().forEach(skill -> skill.xpCalculator.load(costs.getCategory(skill.getRegistryName().toString().replaceAll("[:]", "/"))));
		
		config.save();
	}
}