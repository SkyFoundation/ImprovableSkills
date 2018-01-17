package com.endie.is.proxy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.endie.is.ImprovableSkillsMod;
import com.endie.is.InfoIS;
import com.endie.is.api.PlayerSkillData;
import com.endie.is.init.ItemsIS;
import com.endie.is.net.PacketOpenSkillsBook;
import com.pengu.hammercore.client.texture.gui.theme.GuiTheme;
import com.pengu.hammercore.color.InterpolationUtil;
import com.pengu.hammercore.net.HCNetwork;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy
{
	public boolean modifyBookCol, hovered;
	
	@Override
	public void init()
	{
		MinecraftForge.EVENT_BUS.register(this);
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, layer) ->
		{
			if(modifyBookCol && hovered)
				return InterpolationUtil.interpolate(layer == 0 ? InterpolationUtil.interpolate(GuiTheme.setAlpha(aOrBIfDefTheme(GuiTheme.CURRENT_THEME.bodyColor, 0xFF_2B95D5)), 0xFF_FFFFCC, .4F) : 0xFF_FFFFCC, 0xFF_888888, SyncSkills.getData().hasCraftedSkillsBook() ? 0 : .65F);
			return InterpolationUtil.interpolate(layer == 0 ? GuiTheme.setAlpha(aOrBIfDefTheme(GuiTheme.CURRENT_THEME.bodyColor, 0xFF_2B95D5)) : 0xFF_FFFFFF, 0xFF_888888, !modifyBookCol || SyncSkills.getData().hasCraftedSkillsBook() ? 0 : .65F);
		}, ItemsIS.SKILLS_BOOK);
	}
	
	private GuiButton openSkills;
	
	@SubscribeEvent
	public void addInvButtons(GuiScreenEvent.InitGuiEvent e)
	{
		Minecraft mc = Minecraft.getMinecraft();
		
		if(mc.player == null && SyncSkills.CLIENT_DATA != null)
		{
			ImprovableSkillsMod.LOG.info("Reset client skill data.");
			SyncSkills.CLIENT_DATA = null;
		}
		
		if(e.getGui() instanceof GuiInventory)
		{
			GuiInventory inv = (GuiInventory) e.getGui();
			
			List<Integer> ids = new ArrayList<>();
			e.getButtonList().forEach(b -> ids.add(b.id));
			int i = 0;
			while(ids.contains(i))
				++i;
			
			PlayerSkillData data = SyncSkills.getData();
			
			e.getButtonList().add(openSkills = new GuiButton(i, inv.guiLeft + (inv.getXSize() - 16) / 2 - 1, inv.guiTop + 24, 16, 16, ItemsIS.SKILLS_BOOK.getItemStackDisplayName(ItemStack.EMPTY))
			{
				@Override
				public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
				{
					return this.enabled && (hovered = (mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height));
				}
			});
			
			openSkills.visible = false;
			openSkills.enabled = data.hasCraftedSkillBook;
		}
	}
	
	@SubscribeEvent
	public void drawInv(GuiContainerEvent.DrawForeground e)
	{
		if(e.getGuiContainer() instanceof GuiInventory && openSkills != null)
		{
			GuiInventory inv = (GuiInventory) e.getGuiContainer();
			
			int mx = e.getMouseX();
			int my = e.getMouseY();
			
			modifyBookCol = true;
			
			openSkills.mousePressed(inv.mc, mx, my);
			hovered = mx >= openSkills.x && my >= openSkills.y && mx < openSkills.x + openSkills.width && my < openSkills.y + openSkills.height;
			
			ItemStack book = new ItemStack(ItemsIS.SKILLS_BOOK);
			
			inv.mc.getRenderItem().renderItemIntoGUI(book, openSkills.x - inv.guiLeft, openSkills.y - inv.guiTop);
			
			modifyBookCol = false;
			
			PlayerSkillData data = SyncSkills.getData();
			
			openSkills.enabled = data.hasCraftedSkillBook;
			
			String name = ItemsIS.SKILLS_BOOK.getItemStackDisplayName(book);
			
			if(hovered)
			{
				List<String> arr = new ArrayList<>();
				
				arr.add(name);
				if(!openSkills.enabled)
					arr.add(I18n.format("gui." + InfoIS.MOD_ID + ".locked"));
				
				arr.add(TextFormatting.BLUE + TextFormatting.ITALIC.toString() + '@' + InfoIS.MOD_NAME);
				
				int maxWid = 0;
				for(String el : arr)
					maxWid = Math.max(inv.mc.fontRenderer.getStringWidth(el), maxWid);
				
				maxWid += 20;
				
				inv.drawHoveringText(arr, (inv.getXSize() - maxWid) / 2, 56);
			}
		}
	}
	
	@SubscribeEvent
	public void openSkillBook(GuiScreenEvent.ActionPerformedEvent e)
	{
		if(e.getButton() == openSkills)
		{
			/* Grab skills and open GUI */
			HCNetwork.manager.sendToServer(new PacketOpenSkillsBook());
		}
	}
	
	public static int aOrBIfDefTheme(int a, int b)
	{
		if(GuiTheme.CURRENT_THEME.name.equalsIgnoreCase("vanilla"))
			return b;
		return a;
	}
}