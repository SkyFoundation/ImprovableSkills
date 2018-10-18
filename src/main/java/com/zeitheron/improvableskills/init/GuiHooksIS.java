package com.zeitheron.improvableskills.init;

import com.zeitheron.hammercore.client.gui.IGuiCallback;
import com.zeitheron.improvableskills.client.gui.abil.ench.ContainerEnchPowBook;
import com.zeitheron.improvableskills.client.gui.abil.ench.ContainerPortableEnchantment;
import com.zeitheron.improvableskills.client.gui.abil.ench.GuiEnchPowBook;
import com.zeitheron.improvableskills.client.gui.abil.ench.GuiPortableEnchantment;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GuiHooksIS
{
	public static final IGuiCallback ENCHANTMENT = new IGuiCallback()
	{
		@Override
		public Object getServerGuiElement(EntityPlayer player, World world, BlockPos pos)
		{
			return new ContainerPortableEnchantment(player.inventory, world);
		}
		
		@Override
		public Object getClientGuiElement(EntityPlayer player, World world, BlockPos pos)
		{
			return new GuiPortableEnchantment(player.inventory, world);
		}
	};
	
	public static final IGuiCallback ENCH_POWER_BOOK_IO = new IGuiCallback()
	{
		@Override
		public Object getServerGuiElement(EntityPlayer player, World world, BlockPos pos)
		{
			return new ContainerEnchPowBook(player, world);
		}
		
		@Override
		public Object getClientGuiElement(EntityPlayer player, World world, BlockPos pos)
		{
			return new GuiEnchPowBook(player, world);
		}
	};
}