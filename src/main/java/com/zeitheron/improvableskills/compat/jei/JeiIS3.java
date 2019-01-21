package com.zeitheron.improvableskills.compat.jei;

import com.zeitheron.improvableskills.client.gui.abil.crafter.ContainerCrafter;
import com.zeitheron.improvableskills.client.gui.abil.crafter.GuiCrafter;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;

@JEIPlugin
public class JeiIS3 implements IModPlugin
{
	@Override
	public void register(IModRegistry registry)
	{
		registry.addRecipeClickArea(GuiCrafter.class, 88, 32, 28, 23, VanillaRecipeCategoryUid.CRAFTING);
		registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerCrafter.class, VanillaRecipeCategoryUid.CRAFTING, 1, 9, 10, 36);
	}
}