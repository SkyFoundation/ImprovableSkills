package com.zeitheron.improvableskills.client.gui.abil.ench;

import java.io.IOException;
import java.util.Random;

import org.lwjgl.input.Keyboard;

import com.zeitheron.hammercore.client.utils.ItemColorHelper;
import com.zeitheron.hammercore.client.utils.TexturePixelGetter;
import com.zeitheron.hammercore.client.utils.texture.gui.DynGuiTex;
import com.zeitheron.hammercore.client.utils.texture.gui.GuiTexBakery;
import com.zeitheron.hammercore.utils.WorldUtil;
import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.client.rendering.OnTopEffects;
import com.zeitheron.improvableskills.client.rendering.ote.OTESparkle;
import com.zeitheron.improvableskills.data.PlayerDataManager;
import com.zeitheron.improvableskills.proxy.SyncSkills;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class GuiEnchPowBook extends GuiContainer
{
	protected static final ResourceLocation OVERLAY = new ResourceLocation(InfoIS.MOD_ID, "textures/gui/book_slot_overlay.png");
	
	public GuiEnchPowBook(EntityPlayer player, World world)
	{
		super(new ContainerEnchPowBook(player, world));
	}
	
	DynGuiTex tex;
	
	@Override
	public void initGui()
	{
		super.initGui();
		
		GuiTexBakery b = new GuiTexBakery();
		b.body(0, 0, xSize, ySize);
		for(Slot slot : inventorySlots.inventorySlots)
			b.slot(slot.xPos - 1, slot.yPos - 1);
		tex = b.bake();
		
		addButton(new GuiButton(0, guiLeft + xSize / 2 - 16, guiTop + ySize / 2 - 52 - 12, 60, 20, "--> +"));
		addButton(new GuiButton(1, guiLeft + xSize / 2 - 16, guiTop + ySize / 2 - 52 + 12, 60, 20, "Back"));
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		buttonList.get(0).displayString = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? "<-- *" : "--> *";
		
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
		
		String ln = I18n.format("container.enchant.level.many", (int) SyncSkills.getData().enchantPower);
		
		this.fontRenderer.drawString(ln, guiLeft + (xSize - fontRenderer.getStringWidth(ln)) / 2, guiTop + 3, 4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		tex.render(guiLeft, guiTop);
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		int id = button.id;
		if(id == 0 && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
			id = 11;
		mc.playerController.sendEnchantPacket(inventorySlots.windowId, id);
		
		ContainerEnchPowBook thus = WorldUtil.cast(inventorySlots, ContainerEnchPowBook.class);
		if(thus != null)
		{
			Slot sl = thus.inventorySlots.get(thus.inventorySlots.size() - 1);
			
			if(id == 11)
			{
				ItemStack item = thus.inventory.getStackInSlot(0);
				
				PlayerSkillData data = SyncSkills.getData();
				
				if(item.isEmpty() || (item.getItem() == Items.BOOK && item.getCount() < 64) && data != null && data.enchantPower > 0F)
				{
					Random r = new Random();
					int col = ItemColorHelper.DEFAULT_GLINT_COLOR;
					
					String ln = I18n.format("container.enchant.level.many", (int) SyncSkills.getData().enchantPower);
					
					double tx = guiLeft + (xSize - fontRenderer.getStringWidth(ln)) / 2;
					double ty = guiTop + 3;
					
					for(int i = 0; i < 2; ++i)
						OnTopEffects.effects.add(new OTESparkle(tx + r.nextFloat() * fontRenderer.getStringWidth(ln), ty + r.nextFloat() * fontRenderer.FONT_HEIGHT, guiLeft + sl.xPos + r.nextInt(16), guiTop + sl.yPos + r.nextInt(16), 30, col));
				}
			}
			
			if(id == 0)
			{
				ItemStack item = thus.inventory.getStackInSlot(0);
				
				PlayerSkillData data = SyncSkills.getData();
				
				if(!item.isEmpty() && item.getItem() == Items.BOOK && data != null && data.enchantPower < 15F)
				{
					Random r = new Random();
					int col = ItemColorHelper.DEFAULT_GLINT_COLOR;
					
					String ln = I18n.format("container.enchant.level.many", (int) SyncSkills.getData().enchantPower);
					
					double tx = guiLeft + (xSize - fontRenderer.getStringWidth(ln)) / 2;
					double ty = guiTop + 3;
					
					for(int i = 0; i < 2; ++i)
						OnTopEffects.effects.add(new OTESparkle(guiLeft + sl.xPos + r.nextInt(16), guiTop + sl.yPos + r.nextInt(16), tx + r.nextFloat() * fontRenderer.getStringWidth(ln), ty + r.nextFloat() * fontRenderer.FONT_HEIGHT, 30, col));
				}
			}
		}
	}
}