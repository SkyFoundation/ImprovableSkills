package com.zeitheron.improvableskills.init;

import com.zeitheron.improvableskills.api.registry.PageletBase;
import com.zeitheron.improvableskills.cfg.ConfigsIS;
import com.zeitheron.improvableskills.custom.pagelets.PageletAbilities;
import com.zeitheron.improvableskills.custom.pagelets.PageletDiscord;
import com.zeitheron.improvableskills.custom.pagelets.PageletNews;
import com.zeitheron.improvableskills.custom.pagelets.PageletSkills;
import com.zeitheron.improvableskills.custom.pagelets.PageletUpdate;
import com.zeitheron.improvableskills.custom.pagelets.PageletXPStorage;

import net.minecraftforge.registries.IForgeRegistry;

public class PageletsIS
{
	public static final PageletSkills SKILLS = new PageletSkills();
	public static final PageletAbilities ABILITIES = new PageletAbilities();
	public static final PageletUpdate UPDATE = new PageletUpdate();
	public static final PageletNews NEWS = new PageletNews();
	public static final PageletDiscord DISCORD = new PageletDiscord();
	public static final PageletXPStorage XP_STORAGE = new PageletXPStorage();
	
	public static IForgeRegistry<PageletBase> registry;
	
	public static void register(IForgeRegistry<PageletBase> reg)
	{
		registry = reg;
		
		reg.register(SKILLS);
		reg.register(ABILITIES);
		reg.register(UPDATE);
		reg.register(NEWS);
		reg.register(DISCORD);
		
		if(ConfigsIS.configs.getBoolean("XP Storage", "Misc", true, "Should XP Bank be active in the book? Disabling this only hides the skill from the player."))
			reg.register(XP_STORAGE);
	}
}