package com.zeitheron.improvableskills.init;

import com.zeitheron.hammercore.client.gui.IGuiCallback;
import com.zeitheron.improvableskills.client.gui.abil.anvil.ContainerSkilledAnvil;
import com.zeitheron.improvableskills.client.gui.abil.anvil.GuiSkilledAnvil;
import com.zeitheron.improvableskills.client.gui.abil.crafter.ContainerCrafter;
import com.zeitheron.improvableskills.client.gui.abil.crafter.GuiCrafter;
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
	
	public static final IGuiCallback CRAFTING = new IGuiCallback()
	{
		@Override
		public Object getServerGuiElement(EntityPlayer player, World world, BlockPos pos)
		{
			return new ContainerCrafter(player.inventory);
		}
		
		@Override
		public Object getClientGuiElement(EntityPlayer player, World world, BlockPos pos)
		{
			return new GuiCrafter(player.inventory);
		}
	};
	
	public static final IGuiCallback ANVIL = new IGuiCallback()
	{
		@Override
		public Object getServerGuiElement(EntityPlayer player, World world, BlockPos pos)
		{
			return new ContainerSkilledAnvil(player.inventory, world, pos, player);
		}
		
		@Override
		public Object getClientGuiElement(EntityPlayer player, World world, BlockPos pos)
		{
			return new GuiSkilledAnvil(player.inventory, world);
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