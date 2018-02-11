package com.endie.is.cfg;

import java.util.Arrays;
import java.util.List;

import com.endie.is.InfoIS;
import com.endie.is.skills.SkillGrowth;
import com.pengu.hammercore.cfg.HCModConfigurations;
import com.pengu.hammercore.cfg.iConfigReloadListener;
import com.pengu.hammercore.cfg.fields.ModConfigPropertyBool;

import net.minecraftforge.common.config.Configuration;

@HCModConfigurations(modid = InfoIS.MOD_ID)
public class ConfigsIS implements iConfigReloadListener
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