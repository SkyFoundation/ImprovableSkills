package com.zeitheron.improvableskills.api.registry;

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
	public abstract GuiTabbable createTab(PlayerSkillData data);
	
	private Object defaultInstance;
	
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
	
	public ITextComponent getTitle()
	{
		return title;
	}
	
	@SideOnly(Side.CLIENT)
	public boolean isVisible()
	{
		return true;
	}
}