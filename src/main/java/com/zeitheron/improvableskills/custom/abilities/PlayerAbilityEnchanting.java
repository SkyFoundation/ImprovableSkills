package com.zeitheron.improvableskills.custom.abilities;

import com.zeitheron.hammercore.client.utils.UV;
import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.SkillTex;
import com.zeitheron.improvableskills.api.registry.PlayerAbilityBase;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PlayerAbilityEnchanting extends PlayerAbilityBase
{
	public PlayerAbilityEnchanting()
	{
		setRegistryName(InfoIS.MOD_ID, "enchanting");
		
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
				
				return new UV(hovered ? texHov : texNorm, 0, 0, 256, 256)
				{
					@Override
					public void render(double x, double y)
					{
						super.render(x, y);
						
						GlStateManager.pushMatrix();
						GlStateManager.translate(x + 16, y + 4, 0);
						GlStateManager.scale(width / 18, height / 18, 1);
						Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(table, 0, 0);
						GlStateManager.popMatrix();
					}
				};
			}
		};
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void onClickClient(EntityPlayer player)
	{
		
	}
}