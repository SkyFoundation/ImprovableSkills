package com.endie.is;

import java.util.List;

import com.endie.is.api.DamageSourceProcessor;
import com.endie.is.api.DamageSourceProcessor.DamageType;
import com.endie.is.api.PlayerSkillBase;
import com.endie.is.api.PlayerSkillData;
import com.endie.is.api.iDigSpeedAffectorSkill;
import com.endie.is.data.PlayerDataManager;
import com.endie.is.init.SkillsIS;
import com.endie.is.items.ItemSkillsBook;
import com.endie.is.net.PacketSyncSkillData;
import com.endie.is.proxy.SyncSkills;
import com.endie.lib.tuple.OneTuple;
import com.endie.lib.tuple.OneTuple.Atomic;
import com.pengu.hammercore.annotations.MCFBus;
import com.pengu.hammercore.common.utils.WorldUtil;
import com.pengu.hammercore.net.HCNetwork;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@MCFBus
public class SkillEvents
{
	@SubscribeEvent
	public void playerTick(PlayerTickEvent e)
	{
		if(e.phase == Phase.START)
		{
			PlayerSkillData data = PlayerDataManager.getDataFor(e.player);
			if(data == null)
				return;
			data.handleTick();
		}
	}
	
	@SubscribeEvent
	public void crafting(ItemCraftedEvent e)
	{
		/** Check if we craft skills book */
		if(e.player instanceof EntityPlayerMP && !e.player.world.isRemote && !e.crafting.isEmpty() && e.crafting.getItem() instanceof ItemSkillsBook)
		{
			PlayerSkillData data = PlayerDataManager.getDataFor(e.player);
			if(data == null)
				return;
			data.hasCraftedSkillBook = true;
			HCNetwork.manager.sendTo(new PacketSyncSkillData(data), (EntityPlayerMP) e.player);
		}
	}
	
	@SubscribeEvent
	public void breakSpeed(PlayerEvent.BreakSpeed e)
	{
		if(e.getEntityLiving() instanceof EntityPlayer)
		{
			EntityPlayer p = (EntityPlayer) e.getEntityLiving();
			PlayerSkillData data = p.world.isRemote ? SyncSkills.CLIENT_DATA : PlayerDataManager.getDataFor(p);
			if(data == null || data.player == null)
				return;
			
			ItemStack item = p.getHeldItemMainhand();
			
			OneTuple.Atomic<Float> tot = new Atomic<>(1F);
			GameRegistry.findRegistry(PlayerSkillBase.class).getValues().stream().filter(s -> s instanceof iDigSpeedAffectorSkill).forEach(s ->
			{
				iDigSpeedAffectorSkill d = (iDigSpeedAffectorSkill) s;
				tot.set(tot.get() + d.getDigMultiplier(item, e.getPos(), data));
			});
			e.setNewSpeed(e.getOriginalSpeed() * tot.get());
		}
	}
	
	@SubscribeEvent
	public void fall(LivingFallEvent e)
	{
		if(e.getEntityLiving() instanceof EntityPlayer)
		{
			EntityPlayer p = (EntityPlayer) e.getEntityLiving();
			PlayerSkillData data = p.world.isRemote ? SyncSkills.CLIENT_DATA : PlayerDataManager.getDataFor(p);
			if(data == null)
				return;
			int softLandingStatLevel = data.getSkillLevel(SkillsIS.SOFT_LANDING);
			
			float reduce = Math.min(0.5F, Math.max(0.25F, softLandingStatLevel / (float) SkillsIS.SOFT_LANDING.maxLvl));
			reduce = 1.0F - reduce;
			if(softLandingStatLevel > 0)
			{
				e.setDistance(e.getDistance() * reduce);
				p.fallDistance *= reduce;
				e.setDamageMultiplier(e.getDamageMultiplier() * reduce);
				return;
			}
		}
	}
	
	@SubscribeEvent
	public void jump(LivingJumpEvent e)
	{
		if(e.getEntityLiving() instanceof EntityPlayer)
		{
			EntityPlayer p = (EntityPlayer) e.getEntityLiving();
			PlayerSkillData data = p.world.isRemote ? SyncSkills.CLIENT_DATA : PlayerDataManager.getDataFor(p);
			if(data == null)
				return;
			int leaper = data.getSkillLevel(SkillsIS.LEAPER);
			
			if(leaper > 0)
				p.motionY *= 1 + (float) leaper / SkillsIS.LEAPER.maxLvl * 0.75F;
		}
	}
	
	@SubscribeEvent
	public void blockBroken(BlockEvent.BreakEvent e)
	{
		EntityPlayer p = e.getPlayer();
		float xp = e.getExpToDrop();
		PlayerSkillData data = p.world.isRemote ? SyncSkills.CLIENT_DATA : PlayerDataManager.getDataFor(p);
		if(data == null || xp <= 0)
			return;
		float xpp = data.getSkillLevel(SkillsIS.XP_PLUS) / (float) SkillsIS.XP_PLUS.maxLvl;
		e.setExpToDrop(MathHelper.floor(xp + p.world.rand.nextFloat() * xp * xpp));
	}
	
	@SubscribeEvent
	public void killEntity(LivingExperienceDropEvent e)
	{
		EntityLivingBase die = e.getEntityLiving();
		float xp = e.getDroppedExperience();
		/* Prevent XP dupe */
		if(die instanceof EntityPlayer || xp <= 0)
			return;
		EntityPlayer p = e.getAttackingPlayer();
		PlayerSkillData data = p.world.isRemote ? SyncSkills.CLIENT_DATA : PlayerDataManager.getDataFor(p);
		if(data == null)
			return;
		float xpp = data.getSkillLevel(SkillsIS.XP_PLUS) / (float) SkillsIS.XP_PLUS.maxLvl;
		e.setDroppedExperience(MathHelper.floor(xp + p.world.rand.nextFloat() * xp * xpp));
	}
	
	@SubscribeEvent
	public void babyEntitySpawn(BabyEntitySpawnEvent e)
	{
		EntityPlayer p = e.getCausedByPlayer();
		if(e.getChild() instanceof EntityVillager || p == null || p instanceof FakePlayer)
			return;
		PlayerSkillData data = p.world.isRemote ? SyncSkills.CLIENT_DATA : PlayerDataManager.getDataFor(p);
		if(data == null)
			return;
		int xpp = data.getSkillLevel(SkillsIS.XP_PLUS);
		if(xpp > 0)
		{
			int x = p.world.rand.nextInt(xpp + 1);
			if(x == 0)
				return;
			EntityLiving c = e.getParentA();
			c.world.spawnEntity(new EntityXPOrb(c.world, c.posX, c.posY, c.posZ, x));
		}
	}
	
	@SubscribeEvent
	public void itemFished(ItemFishedEvent e)
	{
		EntityPlayer p = e.getEntityPlayer();
		NonNullList<ItemStack> drops = e.getDrops();
		
		if(p == null || p instanceof FakePlayer)
			return;
		PlayerSkillData data = p.world.isRemote ? SyncSkills.CLIENT_DATA : PlayerDataManager.getDataFor(p);
		if(data == null)
			return;
		int xpp = data.getSkillLevel(SkillsIS.XP_PLUS);
		if(xpp > 0)
			for(ItemStack drop : drops)
			{
				int x = p.world.rand.nextInt(xpp + 1);
				if(x == 0)
					continue;
				p.world.spawnEntity(new EntityXPOrb(p.world, p.posX, p.posY, p.posZ, x));
			}
	}
	
	@SubscribeEvent
	public void dropsEvent(BlockEvent.HarvestDropsEvent e)
	{
		EntityPlayer p = e.getHarvester();
		BlockPos pos = e.getPos();
		World w = e.getWorld();
		List<ItemStack> drops = e.getDrops();
		SkillsIS.TREASURE_OF_SANDS.handleDropAdd(e, PlayerDataManager.getDataFor(p), drops);
	}
	
	@SubscribeEvent
	public void attackHook(LivingHurtEvent e)
	{
		DamageSource ds = e.getSource();
		
		ic: if(e.getEntityLiving() instanceof EntityPlayer && ds == DamageSource.FALL)
		{
			EntityPlayer p = (EntityPlayer) e.getEntityLiving();
			PlayerSkillData data = p.world.isRemote ? SyncSkills.CLIENT_DATA : PlayerDataManager.getDataFor(p);
			if(data == null)
				break ic;
			if(data.getSkillLevel(SkillsIS.SOFT_LANDING) >= SkillsIS.SOFT_LANDING.maxLvl && e.getAmount() >= p.getHealth())
				e.setAmount(p.getHealth() - 1F);
		}
		
		ic: if(ds != null && ds.isFireDamage() && e.getEntity() instanceof EntityPlayer)
		{
			EntityPlayer p = (EntityPlayer) e.getEntity();
			PlayerSkillData data = p.world.isRemote ? SyncSkills.CLIENT_DATA : PlayerDataManager.getDataFor(p);
			if(data == null)
				break ic;
			int obsSkin = data.getSkillLevel(SkillsIS.OBSIDIAN_SKIN);
			e.setAmount(e.getAmount() * (1F - obsSkin / (float) SkillsIS.OBSIDIAN_SKIN.maxLvl + .2F));
		}
		
		ic: if(DamageSourceProcessor.getDamageType(ds) == DamageType.MELEE)
		{
			EntityPlayer p = DamageSourceProcessor.getMeleeAttacker(ds);
			if(p == null)
				break ic;
			PlayerSkillData data = p.world.isRemote ? SyncSkills.CLIENT_DATA : PlayerDataManager.getDataFor(p);
			if(data == null)
				break ic;
			int melee = data.getSkillLevel(SkillsIS.DAMAGE_MELEE);
			float pp = (float) melee / SkillsIS.DAMAGE_MELEE.maxLvl;
			e.setAmount(e.getAmount() + (e.getAmount() * pp / 2F) + pp * 7F);
		}
		
		ic: if(DamageSourceProcessor.getDamageType(ds) == DamageType.RANGED)
		{
			EntityPlayer p = DamageSourceProcessor.getRangedOwner(ds);
			if(p == null)
				break ic;
			PlayerSkillData data = p.world.isRemote ? SyncSkills.CLIENT_DATA : PlayerDataManager.getDataFor(p);
			if(data == null)
				break ic;
			int melee = data.getSkillLevel(SkillsIS.DAMAGE_RANGED);
			float pp = (float) melee / SkillsIS.DAMAGE_RANGED.maxLvl;
			e.setAmount(e.getAmount() + (e.getAmount() * pp) + melee / 2F);
		}
		
		ic: if(DamageSourceProcessor.getDamageType(ds) == DamageType.ALCHEMICAL)
		{
			EntityPlayer src = DamageSourceProcessor.getAlchemicalOwner(ds.getImmediateSource());
			Entity hurt = e.getEntity();
			if(src == null)
				break ic;
			PlayerSkillData dat = hurt.world.isRemote ? SyncSkills.CLIENT_DATA : PlayerDataManager.getDataFor(src);
			if(dat == null)
				break ic;
			int lvl = dat.getSkillLevel(SkillsIS.ENDER_MANIPULATOR);
			//ALCHEMICAL DAMAGE !!
		}
		
		if(ds != null && e.getEntityLiving() instanceof EntityPlayer)
		{
			EntityPlayer p = (EntityPlayer) e.getEntityLiving();
			PlayerSkillData data = p.world.isRemote ? SyncSkills.CLIENT_DATA : PlayerDataManager.getDataFor(p);
			if(data == null)
				return;
			int melee = data.getSkillLevel(SkillsIS.PVP);
			float pp = 1 - (float) melee / SkillsIS.PVP.maxLvl;
			e.setAmount(e.getAmount() * Math.min(1, .75F + pp / 4F));
		}
	}
	
	@SubscribeEvent
	public void enchLvl(EnchantmentLevelSetEvent e)
	{
		List<EntityPlayerMP> players = e.getWorld().getEntitiesWithinAABB(EntityPlayerMP.class, new AxisAlignedBB(e.getPos()).grow(9));
		for(EntityPlayerMP p : players)
		{
			ContainerEnchantment enc = (ContainerEnchantment) p.openContainer;
			
			/* Check that the item is absolutely equal. Allows to see who is
			 * actually calling the event. Little hack ;) */
			if(e.getItem() == enc.tableInventory.getStackInSlot(0))
			{
				int enchanter = PlayerDataManager.getDataFor(p).getSkillLevel(SkillsIS.ENCHANTER);
				
				if(enchanter > 0 && e.getLevel() != 0)
					e.setLevel(Math.max(1, e.getLevel() - enchanter / 4));
				
				return;
			}
		}
	}
	
	@SubscribeEvent
	public void enderPort(EnderTeleportEvent e)
	{
		EntityPlayerMP p = WorldUtil.cast(e.getEntityLiving(), EntityPlayerMP.class);
		
		if(p != null)
		{
			int lvl = PlayerDataManager.getDataFor(p).getSkillLevel(SkillsIS.ENDER_MANIPULATOR);
			
			if(lvl > 0)
			{
				float prog = lvl / (float) (SkillsIS.ENDER_MANIPULATOR.maxLvl - 1);
				
				if(prog > 1)
				{
					e.setAttackDamage(e.getAttackDamage() / 10F);
					p.heal(1);
				} else
					e.setAttackDamage(e.getAttackDamage() * (1F - prog * .8F));
			}
		}
	}
}