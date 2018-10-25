package com.zeitheron.improvableskills.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.zeitheron.hammercore.client.utils.ItemColorHelper;
import com.zeitheron.hammercore.utils.ConsumableItem;
import com.zeitheron.hammercore.utils.color.Rainbow;
import com.zeitheron.hammercore.utils.inventory.InventoryDummy;
import com.zeitheron.improvableskills.ImprovableSkillsMod;
import com.zeitheron.improvableskills.api.RecipesParchmentFragment;
import com.zeitheron.improvableskills.api.RecipesParchmentFragment.RecipeParchmentFragment;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;

public class ItemParchmentFragment extends Item
{
	public ItemParchmentFragment()
	{
		setTranslationKey("parchment_fragment");
	}
	
	@Override
	public boolean onEntityItemUpdate(EntityItem e)
	{
		float f1 = MathHelper.sin(((float) e.age) / 10.0F + e.hoverStart) * 0.1F + 0.1F;
		
		NBTTagCompound nbt = e.getEntityData();
		
		boolean fx = false, ffx = false;
		int add = 0;
		RecipeParchmentFragment recipe = null;
		
		List<EntityItem> itemsNearby = e.world.getEntitiesWithinAABB(EntityItem.class, e.getEntityBoundingBox().grow(1, .1, 1));
		
		itemsNearby.remove(e); // Exclude self
		
		rs: for(RecipeParchmentFragment r : RecipesParchmentFragment.RECIPES)
		{
			IntList counts = new IntArrayList();
			NonNullList<ItemStack> copy = NonNullList.create();
			for(EntityItem ei : itemsNearby)
			{
				copy.add(ei.getItem().copy());
				counts.add(ei.getItem().getCount());
			}
			
			InventoryDummy id = new InventoryDummy(copy.toArray(new ItemStack[copy.size()]));
			
			for(ConsumableItem ci : r.itemsIn)
				if(!ci.consume(id))
					continue rs;
				
			for(int i = 0; i < copy.size(); ++i)
			{
				boolean changed = copy.get(i).getCount() != counts.getInt(i);
				if(changed)
				{
					EntityItem item = itemsNearby.get(i);
					
					double d = Math.max(.8, (1 - item.getDistanceSq(e)) * 15) * 15;
					double mx = (e.posX - item.posX) / d;
					double my = (e.posY - item.posY) / d;
					double mz = (e.posZ - item.posZ) / d;
					
					item.addVelocity(mx, my, mz);
					
					item.setNoGravity(true);
				}
			}
			
			fx = true;
			recipe = r;
			
			nbt.setInteger("IS3ParchCraft", nbt.getInteger("IS3ParchCraft") + 1);
			int v = nbt.getInteger("IS3ParchCraft");
			int mv = r.itemsIn.size() * 40;
			
			int time = v * 5 / mv;
			
			float prog = v / (float) (mv + 40);
			
			if(v % Math.max(1, 5 - time) == 0)
				e.world.playSound(null, e.getPosition(), SoundEvents.UI_TOAST_IN, SoundCategory.AMBIENT, 2F, .25F + 1.75F * prog);
			
			nbt.setFloat("IS3ParchDegree", nbt.getFloat("IS3ParchDegree") + (prog + .25F) * 4F);
			nbt.setFloat("IS3ParchThrowback", prog);
			
			add = Math.round((v / (mv + 40F)) * 10);
			
			if(v > mv)
			{
				if(v > mv + 40)
				{
					if(!e.world.isRemote)
					{
						NonNullList<ItemStack> origin = NonNullList.create();
						for(EntityItem ei : itemsNearby)
						{
							origin.add(ei.getItem());
							ei.setNoGravity(false);
						}
						
						id = new InventoryDummy(0);
						id.inventory = origin;
						
						for(ConsumableItem ci : r.itemsIn)
							if(!ci.consume(id))
								continue rs;
							
						EntityItem res = new EntityItem(e.world, e.posX, e.posY, e.posZ, r.output.copy());
						
						res.motionX = e.motionX;
						res.motionY = e.motionY;
						res.motionZ = e.motionZ;
						res.hoverStart = e.hoverStart;
						
						e.world.playSound(null, e.getPosition(), SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.AMBIENT, 1F, 1.6F + itemRand.nextFloat() * .2F);
						
						e.world.spawnEntity(res);
					}
					
					e.getItem().shrink(1);
					nbt.removeTag("IS3ParchCraft");
				}
				
				ffx = true;
			}
			
			break rs;
		}
		
		if(recipe == null && nbt.hasKey("IS3ParchCraft"))
			nbt.removeTag("IS3ParchCraft");
		
		if(fx && recipe != null && e.ticksExisted % 2 == 0 && e.onGround)
		{
			int num = recipe.itemsIn.size() + 3 + add;
			float deg = 360F / num;
			
			float coff = nbt.getFloat("IS3ParchDegree") % 360F;
			float throwb = .75F + nbt.getFloat("IS3ParchThrowback");
			
			for(int i = 0; i < num; ++i)
			{
				double sin = Math.sin(Math.toRadians(coff));
				double cos = Math.cos(Math.toRadians(coff));
				
				ImprovableSkillsMod.proxy.sparkle(e.world, e.posX + (itemRand.nextFloat() - itemRand.nextFloat()) * .05F, e.posY + (itemRand.nextFloat() - itemRand.nextFloat()) * .1F + e.height * 1.5, e.posZ + (itemRand.nextFloat() - itemRand.nextFloat()) * .05F, sin * .05 * throwb, f1 * .1, cos * .05 * throwb, 0x31425E, 90);
				
				coff += deg;
			}
		}
		
		return false;
	}
}