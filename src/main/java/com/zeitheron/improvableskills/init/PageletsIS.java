package com.zeitheron.improvableskills.init;

import com.zeitheron.improvableskills.api.registry.PageletBase;
import com.zeitheron.improvableskills.custom.pagelets.PageletAbilities;
import com.zeitheron.improvableskills.custom.pagelets.PageletSkills;
import com.zeitheron.improvableskills.custom.pagelets.PageletUpdate;

import net.minecraftforge.registries.IForgeRegistry;

public class PageletsIS
{
	public static final PageletSkills SKILLS = new PageletSkills();
	public static final PageletAbilities ABILITIES = new PageletAbilities();
	public static final PageletUpdate UPDATE = new PageletUpdate();
	
	public static IForgeRegistry<PageletBase> registry;
	
	public static void register(IForgeRegistry<PageletBase> reg)
	{
		registry = reg;
		
		reg.register(SKILLS);
		reg.register(ABILITIES);
		reg.register(UPDATE);
	}
}