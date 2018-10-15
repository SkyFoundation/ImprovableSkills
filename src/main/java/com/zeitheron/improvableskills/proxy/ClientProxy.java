package com.zeitheron.improvableskills.proxy;

import java.util.ArrayList;
import java.util.List;

import com.zeitheron.hammercore.client.utils.texture.gui.theme.GuiTheme;
import com.zeitheron.hammercore.net.HCNet;
import com.zeitheron.hammercore.utils.color.ColorHelper;
import com.zeitheron.improvableskills.ImprovableSkillsMod;
import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.api.registry.PlayerSkillBase;
import com.zeitheron.improvableskills.api.registry.PlayerSkillBase.EnumScrollState;
import com.zeitheron.improvableskills.cfg.ConfigsIS;
import com.zeitheron.improvableskills.client.rendering.OnTopEffects;
import com.zeitheron.improvableskills.init.ItemsIS;
import com.zeitheron.improvableskills.items.ItemSkillScroll;
import com.zeitheron.improvableskills.net.PacketOpenSkillsBook;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
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
		MinecraftForge.EVENT_BUS.register(new OnTopEffects());
		
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, layer) ->
		{
			if(modifyBookCol && hovered)
				return ColorHelper.interpolate(layer == 0 ? ColorHelper.interpolate(GuiTheme.setAlpha(aOrBIfDefTheme(GuiTheme.CURRENT_THEME.bodyColor, 0xFF_2B95D5)), 0xFF_FFFFCC, .4F) : 0xFF_FFFFCC, 0xFF_888888, SyncSkills.getData().hasCraftedSkillsBook() ? 0 : .65F);
			return ColorHelper.interpolate(layer == 0 ? GuiTheme.setAlpha(aOrBIfDefTheme(GuiTheme.CURRENT_THEME.bodyColor, 0xFF_2B95D5)) : 0xFF_FFFFFF, 0xFF_888888, !modifyBookCol || SyncSkills.getData().hasCraftedSkillsBook() ? 0 : .65F);
		}, ItemsIS.SKILLS_BOOK);
		
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, layer) ->
		{
			PlayerSkillBase b = ItemSkillScroll.getSkillFromScroll(stack);
			
			if(layer == 1 && b != null)
				return 255 << 24 | b.getColor();
			
			return 0xFF_FFFFFF;
		}, ItemsIS.SCROLL);
		
		ModelResourceLocation[] scrolls = new ModelResourceLocation[] { new ModelResourceLocation(InfoIS.MOD_ID + ":scroll_normal", "inventory"), new ModelResourceLocation(InfoIS.MOD_ID + ":scroll_special", "inventory") };
		
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(ItemsIS.SCROLL, stack ->
		{
			PlayerSkillBase base = ItemSkillScroll.getSkillFromScroll(stack);
			return base != null && base.getScrollState() == EnumScrollState.SPECIAL ? scrolls[1] : scrolls[0];
		});
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
		
		if(e.getGui() instanceof GuiInventory && ConfigsIS.addBookToInv)
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
		if(e.getGuiContainer() instanceof GuiInventory && openSkills != null && ConfigsIS.addBookToInv)
		{
			GuiInventory inv = (GuiInventory) e.getGuiContainer();
			
			int mx = e.getMouseX();
			int my = e.getMouseY();
			
			modifyBookCol = true;
			
			openSkills.x = inv.guiLeft + (inv.getXSize() - 16) / 2 - 1;
			openSkills.y = inv.guiTop + 24;
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
		if(e.getButton() == openSkills && ConfigsIS.addBookToInv)
		{
			/* Grab skills and open GUI */
			HCNet.INSTANCE.sendToServer(new PacketOpenSkillsBook());
		}
	}
	
	public static int aOrBIfDefTheme(int a, int b)
	{
		if(GuiTheme.CURRENT_THEME.name.equalsIgnoreCase("vanilla"))
			return b;
		return a;
	}
}