package com.zeitheron.improvableskills.cfg;

import java.util.Arrays;
import java.util.List;

import com.zeitheron.hammercore.cfg.HCModConfigurations;
import com.zeitheron.hammercore.cfg.IConfigReloadListener;
import com.zeitheron.hammercore.cfg.fields.ModConfigPropertyBool;
import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.skills.SkillGrowth;

import net.minecraftforge.common.config.Configuration;

@HCModConfigurations(modid = InfoIS.MOD_ID)
public class ConfigsIS implements IConfigReloadListener
{
	public static Configuration configs;
	
	private List<String> la;
	
	@ModConfigPropertyBool(name = "AddBookToInv", category = "Client-Only", comment = "Should ImprovableSkills add it's Book of Skills into player's inventory?", defaultValue = true)
	public static boolean addBookToInv;
	
	@Override
	public void reloadCustom(Configuration cfgs)
	{
		configs = cfgs;
		
		if(la != null)
			SkillGrowth.blacklist.removeAll(la);
		SkillGrowth.blacklist.addAll(la = Arrays.asList(cfgs.getStringList("Growth-Blacklist", "Misc", new String[] { "minecraft:grass", "minecraft:tallgrass" }, "What blocks should be ignored for growth skill?\nFormat: \"mod:block\"")));
	}
}