package com.zeitheron.improvableskills.custom.abilities;

import com.zeitheron.hammercore.client.utils.RenderUtil;
import com.zeitheron.hammercore.client.utils.UV;
import com.zeitheron.hammercore.client.utils.UtilsFX;
import com.zeitheron.hammercore.net.HCNet;
import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.SkillTex;
import com.zeitheron.improvableskills.api.registry.PlayerAbilityBase;
import com.zeitheron.improvableskills.net.PacketOpenPortableAnvil;
import com.zeitheron.improvableskills.net.PacketOpenPortableCraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PlayerAbilityAnvil extends PlayerAbilityBase
{
	public PlayerAbilityAnvil()
	{
		setRegistryName(InfoIS.MOD_ID, "anvil");
		
		tex = new SkillTex<PlayerAbilityBase>(this)
		{
			ItemStack table = new ItemStack(Blocks.ENCHANTING_TABLE);
			
			@Override
			@SideOnly(Side.CLIENT)
			public UV toUV(boolean hovered)
			{
				if(texHov == null || texNorm == null)
				{
					ResourceLocation res = skill.getRegistryName();
					this.texNorm = new ResourceLocation(res.getNamespace(), "textures/abilities/" + res.getPath() + "_normal.png");
					this.texHov = new ResourceLocation(res.getNamespace(), "textures/abilities/" + res.getPath() + "_hovered.png");
				}
				
				return hovered ? UVMagma.instance : new UV(texNorm, 0, 0, 256, 256);
			}
		};
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void onClickClient(EntityPlayer player, int mouseButton)
	{
		HCNet.INSTANCE.sendToServer(new PacketOpenPortableAnvil());
	}
	
	@SideOnly(Side.CLIENT)
	static class UVMagma extends UV
	{
		public static final UVMagma instance = new UVMagma();
		
		public UVMagma()
		{
			super(new ResourceLocation("improvableskills", "textures/abilities/anvil_hovered.png"), 0, 0, 256, 256);
		}
		
		@Override
		public void render(double x, double y)
		{
			UtilsFX.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/lava_flow");
			RenderUtil.drawTexturedModalRect(x + 20, y + 20, sprite, width - 40, height - 40);
			
			super.render(x, y);
		}
	}
}