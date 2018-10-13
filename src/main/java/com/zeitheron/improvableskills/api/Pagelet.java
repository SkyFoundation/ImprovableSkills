package com.zeitheron.improvableskills.api;

import com.zeitheron.improvableskills.client.gui.base.GuiTabbable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryEntry;

public abstract class Pagelet extends IForgeRegistryEntry.Impl<Pagelet>
{
	protected ItemStack icon;
	public ITextComponent title;
	
	@SideOnly(Side.CLIENT)
	public abstract GuiTabbable createTab();
	
	private Object defaultInstance;
	
	public GuiTabbable getTab()
	{
		if(defaultInstance == null)
			defaultInstance = createTab();
		return createTab();
	}
	
	public Pagelet setIcon(ItemStack icon)
	{
		this.icon = icon;
		return this;
	}
	
	public Pagelet setTitle(ITextComponent title)
	{
		this.title = title;
		return this;
	}
	
	public ItemStack getIcon()
	{
		if(this.icon == null || this.icon.isEmpty())
			return ItemStack.EMPTY;
		return this.icon;
	}
	
	public ITextComponent getTitle()
	{
		return title;
	}
}