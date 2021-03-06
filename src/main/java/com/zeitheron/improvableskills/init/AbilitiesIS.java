package com.zeitheron.improvableskills.init;

import com.zeitheron.improvableskills.api.registry.PlayerAbilityBase;
import com.zeitheron.improvableskills.custom.abilities.PlayerAbilityAnvil;
import com.zeitheron.improvableskills.custom.abilities.PlayerAbilityCrafter;
import com.zeitheron.improvableskills.custom.abilities.PlayerAbilityEnchanting;

import net.minecraftforge.registries.IForgeRegistry;

public class AbilitiesIS
{
	public static final PlayerAbilityEnchanting ENCHANTING = new PlayerAbilityEnchanting();
	public static final PlayerAbilityCrafter CRAFTER = new PlayerAbilityCrafter();
	public static final PlayerAbilityAnvil ANVIL = new PlayerAbilityAnvil();
	
	public static IForgeRegistry<PlayerAbilityBase> registry;
	
	public static void register(IForgeRegistry<PlayerAbilityBase> reg)
	{
		registry = reg;
		
		reg.register(ENCHANTING);
		reg.register(CRAFTER);
		reg.register(ANVIL);
	}
}