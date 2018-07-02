package com.zeitheron.improvableskills.client.rendering;

import com.zeitheron.improvableskills.client.rendering.ote.OTEBook;
import com.zeitheron.improvableskills.client.rendering.ote.OTEItemStack;
import com.zeitheron.improvableskills.items.ItemSkillScroll;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec2f;

public class ItemToBookHandler
{
	public static void toBook(EnumHand hand, int time)
	{
		Minecraft mc = Minecraft.getMinecraft();
		ScaledResolution sr = new ScaledResolution(mc);
		
		Vec2f v = getPosOfHandSlot(hand, sr);
		toBook(Minecraft.getMinecraft().player.getHeldItem(hand), v.x, v.y, time);
	}
	
	public static void toBook(EnumHand hand, ItemStack stack, int time)
	{
		Minecraft mc = Minecraft.getMinecraft();
		ScaledResolution sr = new ScaledResolution(mc);
		
		Vec2f v = getPosOfHandSlot(hand, sr);
		toBook(stack, v.x, v.y, time);
	}
	
	public static void toBook(ItemStack stack, double x, double y, int time)
	{
		Minecraft mc = Minecraft.getMinecraft();
		ScaledResolution sr = new ScaledResolution(mc);
		
		OTEBook.show(time + 10);
		OnTopEffects.effects.add(new OTEItemStack(x, y, sr.getScaledWidth() - 20, sr.getScaledHeight() - 12, time, stack));
	}
	
	public static Vec2f getPosOfHandSlot(EnumHand hand, ScaledResolution sr)
	{
		Minecraft mc = Minecraft.getMinecraft();
		int w = sr.getScaledWidth();
		int h = sr.getScaledHeight();
		int sl = hand == EnumHand.OFF_HAND ? -2 : mc.player.inventory.currentItem;
		float slots = 4.5F;
		float slot = 18;
		return new Vec2f(w / 2 - slots * slot + sl * slot + (sl == -2 ? 4 : 0), h - 10);
	}
}