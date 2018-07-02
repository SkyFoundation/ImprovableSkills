package com.zeitheron.improvableskills.api;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.zeitheron.hammercore.utils.WorldUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.util.DamageSource;

public class DamageSourceProcessor
{
	/* Gets the 'true source of damage' and casts safely to player. */
	@Nullable
	public static EntityPlayer getAttackerAsPlayer(DamageSource src)
	{
		return WorldUtil.cast(src != null ? src.getTrueSource() : src, EntityPlayer.class);
	}
	
	@Nonnull
	public static DamageType getDamageType(DamageSource src)
	{
		if(src == null)
			return DamageType.UNKNOWN;
		for(DamageType t : DamageType.TYPES)
			if(t.isThisType(src))
				return t;
		return DamageType.UNKNOWN;
	}
	
	public static boolean isMinionEntity(Entity ent)
	{
		return getMinionOwner(ent) != null;
	}
	
	public static boolean isAlchemicalEntity(Entity ent)
	{
		return getAlchemicalOwner(ent) != null;
	}
	
	public static boolean isRangedDamage(DamageSource src)
	{
		return getRangedOwner(src) != null;
	}
	
	public static EntityPlayer getMinionOwner(Entity ent)
	{
		if(ent instanceof IEntityOwnable && ((IEntityOwnable) ent).getOwner() instanceof EntityPlayer)
			return (EntityPlayer) ((IEntityOwnable) ent).getOwner();
		return null;
	}
	
	public static EntityPlayer getAlchemicalOwner(Entity ent)
	{
		if(ent instanceof EntityPotion && ((EntityPotion) ent).getThrower() instanceof EntityPlayer)
			return (EntityPlayer) ((EntityPotion) ent).getThrower();
		return null;
	}
	
	public static EntityPlayer getRangedOwner(DamageSource ds)
	{
		/** Check that this isn't alchemical damage */
		if(getAlchemicalOwner(ds.getImmediateSource()) != null)
			return null;
		
		if(ds.getImmediateSource() instanceof IProjectile && ds.getTrueSource() instanceof EntityPlayer)
			return (EntityPlayer) ds.getTrueSource();
		return null;
	}
	
	public static EntityPlayer getMeleeAttacker(DamageSource ds)
	{
		if(getDamageType(ds) == DamageType.MELEE)
			return WorldUtil.cast(ds.getTrueSource(), EntityPlayer.class);
		return null;
	}
	
	public static class DamageType
	{
		private static final List<DamageType> TYPES = new ArrayList<>();
		private static DamageType[] arTypes;
		
		public static final DamageType MELEE = new DamageType(d -> d.getTrueSource() == d.getImmediateSource() && d.getTrueSource() instanceof EntityPlayer), //
		        RANGED = new DamageType(d -> isRangedDamage(d)), //
		        MINION = new DamageType(d -> isMinionEntity(d.getTrueSource())), //
		        MAGIC = new DamageType(d -> !isAlchemicalEntity(d.getImmediateSource()) && d.isMagicDamage()), //
		        ALCHEMICAL = new DamageType(d -> isAlchemicalEntity(d.getImmediateSource())), //
		        UNKNOWN = new DamageType(d -> false);
		
		private final Predicate<DamageSource> test;
		
		public DamageType(Predicate<DamageSource> test)
		{
			this.test = test;
		}
		
		{
			TYPES.add(this);
			arTypes = TYPES.toArray(new DamageType[TYPES.size()]);
		}
		
		public boolean isThisType(DamageSource src)
		{
			return test.test(src);
		}
		
		/** Don't be silly -- DO NOT EDIT THIS ARRAY */
		public static DamageType[] getTypes()
		{
			return arTypes;
		}
	}
}