package com.zeitheron.improvableskills.client.gui.abil.ench;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.glu.Project;

import com.google.common.collect.Lists;
import com.zeitheron.hammercore.client.utils.ItemColorHelper;
import com.zeitheron.hammercore.utils.color.ColorHelper;
import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.client.gui.base.GuiTabbable;
import com.zeitheron.improvableskills.client.rendering.OnTopEffects;
import com.zeitheron.improvableskills.client.rendering.ote.OTESparkle;
import com.zeitheron.improvableskills.client.rendering.ote.OTETooltip;
import com.zeitheron.improvableskills.init.ItemsIS;
import com.zeitheron.improvableskills.proxy.SyncSkills;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnchantmentNameParts;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class GuiPortableEnchantment extends GuiContainer
{
	/** The ResourceLocation containing the Enchantment GUI texture location */
	private static final ResourceLocation ENCHANTMENT_TABLE_GUI_TEXTURE = new ResourceLocation("textures/gui/container/enchanting_table.png");
	/**
	 * The ResourceLocation containing the texture for the Book rendered above
	 * the enchantment table
	 */
	private static final ResourceLocation ENCHANTMENT_TABLE_BOOK_TEXTURE1 = new ResourceLocation(InfoIS.MOD_ID, "textures/gui/enchanting_table_book_1.png");
	private static final ResourceLocation ENCHANTMENT_TABLE_BOOK_TEXTURE2 = new ResourceLocation(InfoIS.MOD_ID, "textures/gui/enchanting_table_book_2.png");
	/**
	 * The ModelBook instance used for rendering the book on the Enchantment
	 * table
	 */
	private static final ModelBook MODEL_BOOK = new ModelBook();
	/** The player inventory currently bound to this GuiEnchantment instance. */
	private final InventoryPlayer playerInventory;
	/** A Random instance for use with the enchantment gui */
	private final Random random = new Random();
	/**
	 * The same reference as {@link GuiContainer#field_147002_h}, downcasted to
	 * {@link ContainerEnchantment}.
	 */
	private final ContainerEnchantment container;
	public int ticks;
	public float flip;
	public float oFlip;
	public float flipT;
	public float flipA;
	public float open;
	public float oOpen;
	private ItemStack last = ItemStack.EMPTY;
	
	public static int getThemeColor()
	{
		return Minecraft.getMinecraft().getItemColors().colorMultiplier(ItemsIS.SKILLS_BOOK.getDefaultInstance(), 0);
	}
	
	public GuiPortableEnchantment(InventoryPlayer inventory, World worldIn)
	{
		super(new ContainerPortableEnchantment(inventory, worldIn));
		this.playerInventory = inventory;
		this.container = (ContainerEnchantment) this.inventorySlots;
	}
	
	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of
	 * the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		mouseX -= guiLeft;
		mouseY -= guiTop;
		
		String ln = I18n.format("container.enchant");
		this.fontRenderer.drawString(ln, 12, 4, 4210752);
		
		ln = TextFormatting.UNDERLINE + I18n.format("text.improvableskills:enchpower", (int) SyncSkills.getData().enchantPower);
		
		boolean mouseOverChant = mouseX >= 60 + (108 - fontRenderer.getStringWidth(ln)) / 2 && mouseY > 3 && mouseX < 60 + (108 - fontRenderer.getStringWidth(ln)) / 2 + fontRenderer.getStringWidth(ln) && mouseY < 3 + fontRenderer.FONT_HEIGHT;
		
		if(mouseOverChant)
			ln = TextFormatting.BLUE.toString() + ln;
		
		this.fontRenderer.drawString(ln, 60 + (108 - fontRenderer.getStringWidth(ln)) / 2, 3, 4210752);
		
		this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
	}
	
	/**
	 * Called from the main game loop to update the screen.
	 */
	@Override
	public void updateScreen()
	{
		super.updateScreen();
		this.tickBook();
	}
	
	/**
	 * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
	 */
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		super.mouseClicked(mouseX, mouseY, mouseButton);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		
		if(mouseButton == 0)
		{
			mouseX -= guiLeft;
			mouseY -= guiTop;
			
			String ln = I18n.format("text.improvableskills:enchpower", (int) SyncSkills.getData().enchantPower);
			boolean mouseOverChant = mouseX >= 60 + (108 - fontRenderer.getStringWidth(ln)) / 2 && mouseY > 3 && mouseX < 60 + (108 - fontRenderer.getStringWidth(ln)) / 2 + fontRenderer.getStringWidth(ln) && mouseY < 3 + fontRenderer.FONT_HEIGHT;
			if(mouseOverChant)
			{
				mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, .7F + random.nextFloat() * .1F));
				this.mc.playerController.sendEnchantPacket(this.container.windowId, 122);
				return;
			}
			
			mouseX += guiLeft;
			mouseY += guiTop;
		}
		
		for(int k = 0; k < 3; ++k)
		{
			int l = mouseX - (i + 60);
			int i1 = mouseY - (j + 14 + 19 * k);
			
			if(l >= 0 && i1 >= 0 && l < 108 && i1 < 19 && this.container.enchantItem(this.mc.player, k))
			{
				int col = getThemeColor();
				
				// Send color first
				this.mc.playerController.sendEnchantPacket(this.container.windowId, 121);
				this.mc.playerController.sendEnchantPacket(this.container.windowId, Math.round(ColorHelper.getRed(col) * 255F));
				this.mc.playerController.sendEnchantPacket(this.container.windowId, Math.round(ColorHelper.getGreen(col) * 255F));
				this.mc.playerController.sendEnchantPacket(this.container.windowId, Math.round(ColorHelper.getBlue(col) * 255F));
				
				// Then perform actual enchanment
				this.mc.playerController.sendEnchantPacket(this.container.windowId, k);
				
				for(int m = 0; m < 10; ++m)
				{
					float x1 = i + 23 + random.nextFloat() * 22;
					float y1 = j + 23 + random.nextFloat() * 12;
					
					float x2 = i + container.inventorySlots.get(0).xPos + random.nextFloat() * 16;
					float y2 = j + container.inventorySlots.get(0).yPos + random.nextFloat() * 16;
					
					OnTopEffects.effects.add(new OTESparkle(x1, y1, x2, y2, 40 - random.nextInt(30), col));
				}
			}
		}
	}
	
	/**
	 * Draws the background layer of this container (behind the items).
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(ENCHANTMENT_TABLE_GUI_TEXTURE);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
		GlStateManager.pushMatrix();
		GlStateManager.matrixMode(5889);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		ScaledResolution scaledresolution = new ScaledResolution(this.mc);
		GlStateManager.viewport((scaledresolution.getScaledWidth() - 320) / 2 * scaledresolution.getScaleFactor(), (scaledresolution.getScaledHeight() - 240) / 2 * scaledresolution.getScaleFactor(), 320 * scaledresolution.getScaleFactor(), 240 * scaledresolution.getScaleFactor());
		GlStateManager.translate(-0.34F, 0.23F, 0.0F);
		Project.gluPerspective(90.0F, 1.3333334F, 9.0F, 80.0F);
		float f = 1.0F;
		GlStateManager.matrixMode(5888);
		GlStateManager.loadIdentity();
		RenderHelper.enableStandardItemLighting();
		GlStateManager.translate(0.0F, 3.3F, -16.0F);
		GlStateManager.scale(1.0F, 1.0F, 1.0F);
		float f1 = 5.0F;
		GlStateManager.scale(5.0F, 5.0F, 5.0F);
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotate(20.0F, 1.0F, 0.0F, 0.0F);
		float f2 = this.oOpen + (this.open - this.oOpen) * partialTicks;
		
		// if(f2 > .9 && random.nextInt(2) == 0)
		// {
		//
		// }
		
		GlStateManager.translate((1.0F - f2) * 0.2F, (1.0F - f2) * 0.1F, (1.0F - f2) * 0.25F);
		GlStateManager.rotate(-(1.0F - f2) * 90.0F - 90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
		float f3 = this.oFlip + (this.flip - this.oFlip) * partialTicks + 0.25F;
		float f4 = this.oFlip + (this.flip - this.oFlip) * partialTicks + 0.75F;
		f3 = (f3 - (float) MathHelper.fastFloor((double) f3)) * 1.6F - 0.3F;
		f4 = (f4 - (float) MathHelper.fastFloor((double) f4)) * 1.6F - 0.3F;
		
		if(f3 < 0.0F)
		{
			f3 = 0.0F;
		}
		
		if(f4 < 0.0F)
		{
			f4 = 0.0F;
		}
		
		if(f3 > 1.0F)
		{
			f3 = 1.0F;
		}
		
		if(f4 > 1.0F)
		{
			f4 = 1.0F;
		}
		
		GlStateManager.enableRescaleNormal();
		
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();
		
		ColorHelper.gl(255 << 24 | getThemeColor());
		mc.getTextureManager().bindTexture(ENCHANTMENT_TABLE_BOOK_TEXTURE1);
		MODEL_BOOK.render(null, 0.0F, f3, f4, f2, 0.0F, 0.0625F);
		
		ColorHelper.gl(0xFFFFFFFF);
		mc.getTextureManager().bindTexture(ENCHANTMENT_TABLE_BOOK_TEXTURE2);
		MODEL_BOOK.render(null, 0.0F, f3, f4, f2, 0.0F, 0.0625F);
		
		GlStateManager.disableRescaleNormal();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.matrixMode(5889);
		GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(5888);
		GlStateManager.popMatrix();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		EnchantmentNameParts.getInstance().reseedRandomGenerator((long) this.container.xpSeed);
		int k = this.container.getLapisAmount();
		
		for(int l = 0; l < 3; ++l)
		{
			int i1 = i + 60;
			int j1 = i1 + 20;
			this.zLevel = 0.0F;
			this.mc.getTextureManager().bindTexture(ENCHANTMENT_TABLE_GUI_TEXTURE);
			int k1 = this.container.enchantLevels[l];
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			
			if(k1 == 0)
			{
				this.drawTexturedModalRect(i1, j + 14 + 19 * l, 0, 185, 108, 19);
			} else
			{
				String s = "" + k1;
				int l1 = 86 - this.fontRenderer.getStringWidth(s);
				String s1 = EnchantmentNameParts.getInstance().generateNewRandomName(this.fontRenderer, l1);
				FontRenderer fontrenderer = this.mc.standardGalacticFontRenderer;
				int i2 = 6839882;
				
				if(((k < l + 1 || this.mc.player.experienceLevel < k1) && !this.mc.player.capabilities.isCreativeMode) || this.container.enchantClue[l] == -1)
				{
					this.drawTexturedModalRect(i1, j + 14 + 19 * l, 0, 185, 108, 19);
					this.drawTexturedModalRect(i1 + 1, j + 15 + 19 * l, 16 * l, 239, 16, 16);
					fontrenderer.drawSplitString(s1, j1, j + 16 + 19 * l, l1, (i2 & 16711422) >> 1);
					i2 = 4226832;
				} else
				{
					int j2 = mouseX - (i + 60);
					int k2 = mouseY - (j + 14 + 19 * l);
					
					if(j2 >= 0 && k2 >= 0 && j2 < 108 && k2 < 19)
					{
						this.drawTexturedModalRect(i1, j + 14 + 19 * l, 0, 204, 108, 19);
						i2 = 16777088;
					} else
					{
						this.drawTexturedModalRect(i1, j + 14 + 19 * l, 0, 166, 108, 19);
						
						if(random.nextInt(120) == 0)
						{
							String ln = I18n.format("text.improvableskills:enchpower", (int) SyncSkills.getData().enchantPower);
							
							float w = random.nextFloat();
							float x2 = i1 + w * 108;
							float y2 = j + 14 + 19 * l + random.nextFloat() * 19;
							
							float x1 = guiLeft + 60 + (108 - this.fontRenderer.getStringWidth(ln)) / 2 + w * this.fontRenderer.getStringWidth(ln);
							float y1 = guiTop + 3 + this.fontRenderer.FONT_HEIGHT;
							
							OnTopEffects.effects.add(new OTESparkle(x1, y1, x2, y2, 50 + random.nextInt(30), ItemColorHelper.DEFAULT_GLINT_COLOR));
						}
					}
					
					this.drawTexturedModalRect(i1 + 1, j + 15 + 19 * l, 16 * l, 223, 16, 16);
					fontrenderer.drawSplitString(s1, j1, j + 16 + 19 * l, l1, i2);
					i2 = 8453920;
				}
				
				fontrenderer = this.mc.fontRenderer;
				fontrenderer.drawStringWithShadow(s, (float) (j1 + 86 - fontrenderer.getStringWidth(s)), (float) (j + 16 + 19 * l + 7), i2);
			}
		}
	}
	
	/**
	 * Draws the screen and all the components in it.
	 */
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		partialTicks = this.mc.getTickLength();
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
		boolean flag = this.mc.player.capabilities.isCreativeMode;
		int i = this.container.getLapisAmount();
		
		for(int j = 0; j < 3; ++j)
		{
			int k = this.container.enchantLevels[j];
			Enchantment enchantment = Enchantment.getEnchantmentByID(this.container.enchantClue[j]);
			int l = this.container.worldClue[j];
			int i1 = j + 1;
			
			if(this.isPointInRegion(60, 14 + 19 * j, 108, 17, mouseX, mouseY) && k > 0)
			{
				List<String> list = Lists.<String> newArrayList();
				list.add("" + TextFormatting.WHITE + TextFormatting.ITALIC + I18n.format("container.enchant.clue", enchantment == null ? "" : enchantment.getTranslatedName(l)));
				
				if(enchantment == null)
					java.util.Collections.addAll(list, "", TextFormatting.RED + I18n.format("forge.container.enchant.limitedEnchantability"));
				else if(!flag)
				{
					list.add("");
					
					if(this.mc.player.experienceLevel < k)
					{
						list.add(TextFormatting.RED + I18n.format("container.enchant.level.requirement", this.container.enchantLevels[j]));
					} else
					{
						String s;
						
						if(i1 == 1)
						{
							s = I18n.format("container.enchant.lapis.one");
						} else
						{
							s = I18n.format("container.enchant.lapis.many", i1);
						}
						
						TextFormatting textformatting = i >= i1 ? TextFormatting.GRAY : TextFormatting.RED;
						list.add(textformatting + "" + s);
						
						if(i1 == 1)
						{
							s = I18n.format("container.enchant.level.one");
						} else
						{
							s = I18n.format("container.enchant.level.many", i1);
						}
						
						list.add(TextFormatting.GRAY + "" + s);
					}
				}
				
				OTETooltip.showTooltip(list);
				break;
			}
		}
	}
	
	public void tickBook()
	{
		ItemStack itemstack = this.inventorySlots.getSlot(0).getStack();
		
		if(!ItemStack.areItemStacksEqual(itemstack, this.last))
		{
			this.last = itemstack;
			
			while(true)
			{
				this.flipT += (float) (this.random.nextInt(4) - this.random.nextInt(4));
				
				if(this.flip > this.flipT + 1.0F || this.flip < this.flipT - 1.0F)
				{
					break;
				}
			}
		}
		
		++this.ticks;
		this.oFlip = this.flip;
		this.oOpen = this.open;
		boolean flag = false;
		
		for(int i = 0; i < 3; ++i)
		{
			if(this.container.enchantLevels[i] != 0)
			{
				flag = true;
			}
		}
		
		if(flag)
		{
			this.open += 0.2F;
		} else
		{
			this.open -= 0.2F;
		}
		
		this.open = MathHelper.clamp(this.open, 0.0F, 1.0F);
		float f1 = (this.flipT - this.flip) * 0.4F;
		float f = 0.2F;
		f1 = MathHelper.clamp(f1, -0.2F, 0.2F);
		this.flipA += (f1 - this.flipA) * 0.9F;
		this.flip += this.flipA;
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException
	{
		super.keyTyped(typedChar, keyCode);
		if(mc.currentScreen == null)
		{
			int xx = Mouse.getX();
			int yy = Mouse.getY();
			mc.displayGuiScreen(GuiTabbable.lastPagelet.createTab(SyncSkills.getData()));
			Mouse.setCursorPosition(xx, yy);
		}
	}
}