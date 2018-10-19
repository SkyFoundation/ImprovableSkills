package com.zeitheron.improvableskills.init;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.zeitheron.improvableskills.InfoIS;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class SoundsIS
{
	public static final SoundEvent PAGE_TURNS = new SoundEvent(new ResourceLocation(InfoIS.MOD_ID, "page_turns"));
	public static final SoundEvent TREASURE_FOUND = new SoundEvent(new ResourceLocation(InfoIS.MOD_ID, "treasure_found"));
	public static final SoundEvent CONNECT = new SoundEvent(new ResourceLocation(InfoIS.MOD_ID, "connect"));
	
	public static void register(IForgeRegistry<SoundEvent> r)
	{
		for(Field f : SoundsIS.class.getDeclaredFields())
			if(SoundEvent.class.isAssignableFrom(f.getType()) && Modifier.isStatic(f.getModifiers()))
			{
				f.setAccessible(true);
				try
				{
					SoundEvent se = (SoundEvent) f.get(null);
					se.setRegistryName(se.getSoundName());
					r.register(se);
				} catch(ReflectiveOperationException e)
				{
					e.printStackTrace();
				}
			}
	}
}