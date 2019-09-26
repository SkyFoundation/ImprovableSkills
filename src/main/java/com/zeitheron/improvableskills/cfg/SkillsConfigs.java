package com.zeitheron.improvableskills.cfg;

import java.io.File;

import com.zeitheron.hammercore.cfg.file1132.Configuration;
import com.zeitheron.hammercore.cfg.file1132.io.ConfigEntryCategory;
import com.zeitheron.improvableskills.ImprovableSkillsMod;

public class SkillsConfigs
{
	public static final int CUR_VERSION = 1;
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
		
		boolean has$ = config.categories.containsKey("$");
		ConfigEntryCategory $ = config.getCategory("$");
		int version = $.getIntEntry("cfgversion", CUR_VERSION, 0, Integer.MAX_VALUE).getValue();
		if(!has$ || version != CUR_VERSION)
		{
			config.categories.clear();
			File old = new File(configFile.getAbsolutePath() + ".old");
			if(old.isFile())
				old.delete();
			config.config.renameTo(old);
			$ = config.getCategory("$");
			version = $.getIntEntry("cfgversion", CUR_VERSION, 0, Integer.MAX_VALUE).getValue();
		}
		
		ConfigEntryCategory costs = config.getCategory("Costs").setDescription("Configure how expensive each skill is");
		
		ImprovableSkillsMod.getSkills().forEach(skill -> skill.xpCalculator.load(costs.getCategory(skill.getRegistryName().toString().replaceAll("[:]", "/"))));
		
		config.save();
	}
}