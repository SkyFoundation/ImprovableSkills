package com.endie.is;

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
import com.pengu.hammercore.net.HCNetwork;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
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
			PlayerDataManager.getDataFor(e.player).handleTick();
	}
	
	@SubscribeEvent
	public void crafting(ItemCraftedEvent e)
	{
		/** Check if we craft skills book */
		if(e.player instanceof EntityPlayerMP && !e.player.world.isRemote && !e.crafting.isEmpty() && e.crafting.getItem() instanceof ItemSkillsBook)
		{
			PlayerSkillData data = PlayerDataManager.getDataFor(e.player);
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
			int leaper = data.getSkillLevel(SkillsIS.LEAPER);
			
			if(leaper > 0)
				p.motionY *= 1 + (float) leaper / SkillsIS.LEAPER.maxLvl * 0.75F;
		}
	}
	
	@SubscribeEvent
	public void blockBroken(BlockEvent.HarvestDropsEvent e)
	{
		
	}
	
	@SubscribeEvent
	public void attackHook(LivingHurtEvent e)
	{
		DamageSource ds = e.getSource();
		
		if(e.getEntityLiving() instanceof EntityPlayer && ds == DamageSource.FALL)
		{
			EntityPlayer p = (EntityPlayer) e.getEntityLiving();
			PlayerSkillData data = p.world.isRemote ? SyncSkills.CLIENT_DATA : PlayerDataManager.getDataFor(p);
			if(data.getSkillLevel(SkillsIS.SOFT_LANDING) >= SkillsIS.SOFT_LANDING.maxLvl && e.getAmount() >= p.getHealth())
				e.setAmount(p.getHealth() - 1F);
		}
		
		if(ds != null && ds.isFireDamage() && e.getEntity() instanceof EntityPlayer)
		{
			EntityPlayer p = (EntityPlayer) e.getEntity();
			PlayerSkillData data = p.world.isRemote ? SyncSkills.CLIENT_DATA : PlayerDataManager.getDataFor(p);
			int obsSkin = data.getSkillLevel(SkillsIS.OBSIDIAN_SKIN);
			e.setAmount(e.getAmount() * (1F - obsSkin / (float) SkillsIS.OBSIDIAN_SKIN.maxLvl + .2F));
		}
	}
}