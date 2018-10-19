package com.zeitheron.improvableskills.client.gui.base;

import com.zeitheron.hammercore.client.utils.texture.def.IBindableImage;
import com.zeitheron.hammercore.client.utils.texture.gui.theme.GuiTheme;
import com.zeitheron.hammercore.utils.color.ColorHelper;
import com.zeitheron.improvableskills.InfoIS;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class GuiCustomButton extends GuiButton
{
	static IBindableImage ZEITH_AVATAR;
	protected static final ResourceLocation CBUTTON_TEXTURES = new ResourceLocation(InfoIS.MOD_ID, "textures/gui/icons.png");
	
	public SoundEvent customClickSound;
	
	public GuiCustomButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText)
	{
		super(buttonId, x, y, widthIn, heightIn, buttonText);
	}
	
	public GuiCustomButton setCustomClickSound(SoundEvent customClickSound)
	{
		this.customClickSound = customClickSound;
		return this;
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
	{
		if(this.visible)
		{
			FontRenderer fontrenderer = mc.fontRenderer;
			mc.getTextureManager().bindTexture(CBUTTON_TEXTURES);
			this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
			int i = this.getHoverState(this.hovered);
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			ColorHelper.glColor1i(GuiTheme.current().bodyLayerLU);
			this.drawTexturedModalRect(this.x, this.y, 0, i * 20, this.width / 2, this.height);
			this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, i * 20, this.width / 2, this.height);
			this.mouseDragged(mc, mouseX, mouseY);
			int j = 14737632;
			
			if(packedFGColour != 0)
			{
				j = packedFGColour;
			} else if(!this.enabled)
			{
				j = 10526880;
			} else if(this.hovered)
			{
				j = 16777120;
			}
			
			this.drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, j);
		}
	}
	
	@Override
	public void playPressSound(SoundHandler soundHandlerIn)
	{
		if(customClickSound == null)
			super.playPressSound(soundHandlerIn);
		else
			soundHandlerIn.playSound(PositionedSoundRecord.getMasterRecord(customClickSound, 1.0F));
	}
}