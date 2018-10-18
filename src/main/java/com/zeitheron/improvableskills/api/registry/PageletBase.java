package com.zeitheron.improvableskills.api.registry;

import java.util.List;

import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.client.gui.base.GuiTabbable;
import com.zeitheron.improvableskills.proxy.SyncSkills;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryEntry;

public abstract class PageletBase extends IForgeRegistryEntry.Impl<PageletBase>
{
	protected Object icon;
	
	public ITextComponent title;
	
	public boolean isRight()
	{
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	public GuiTabbable createTab(PlayerSkillData data)
	{
		return null;
	}
	
	private Object defaultInstance;
	
	public void reload()
	{
		
	}
	
	@SideOnly(Side.CLIENT)
	public Class<? extends GuiTabbable> getTabType()
	{
		if(defaultInstance == null)
			defaultInstance = createTab(SyncSkills.getData());
		return defaultInstance.getClass().asSubclass(GuiTabbable.class);
	}
	
	@SideOnly(Side.CLIENT)
	public GuiTabbable getTab()
	{
		return createTab(SyncSkills.getData());
	}
	
	/**
	 * Determines whether this pagelet should perform click even or open another
	 * tab
	 */
	@SideOnly(Side.CLIENT)
	public boolean hasTab()
	{
		return true;
	}
	
	/**
	 * Called if {@link #hasTab()} returns false. Otherwise creates new GUI
	 */
	@SideOnly(Side.CLIENT)
	public void onClick()
	{
		
	}
	
	@SideOnly(Side.CLIENT)
	public PageletBase setIcon(Object icon)
	{
		this.icon = icon;
		return this;
	}
	
	public PageletBase setTitle(ITextComponent title)
	{
		this.title = title;
		return this;
	}
	
	@SideOnly(Side.CLIENT)
	public Object getIcon()
	{
		if(this.icon == null)
			return ItemStack.EMPTY;
		return this.icon;
	}
	
	public void addTitle(List<String> text)
	{
		if(getTitle() != null)
			text.add(getTitle().getFormattedText());
		else
			text.add("Unnamed!");
	}
	
	public ITextComponent getTitle()
	{
		return title;
	}
	
	@SideOnly(Side.CLIENT)
	public boolean isVisible()
	{
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	public boolean doesPop()
	{
		return false;
	}
}