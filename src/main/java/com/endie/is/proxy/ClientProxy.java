package com.endie.is.proxy;

import com.endie.is.init.ItemsIS;
import com.pengu.hammercore.client.texture.gui.theme.GuiTheme;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class ClientProxy extends CommonProxy
{
	@Override
	public void init()
	{
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, layer) -> layer == 0 ? GuiTheme.setAlpha(aOrBIfDefTheme(GuiTheme.CURRENT_THEME.bodyColor, 0xFF2B95D5)) : 0xFFFFFFFF, ItemsIS.SKILLS_BOOK);
	}
	
	public static int aOrBIfDefTheme(int a, int b)
	{
		if(GuiTheme.CURRENT_THEME.name.equalsIgnoreCase("vanilla"))
			return b;
		return a;
	}
}