package com.zeitheron.improvableskills;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.zeitheron.hammercore.HammerCore;
import com.zeitheron.hammercore.event.PlayerLoadReadyEvent;
import com.zeitheron.hammercore.internal.GuiManager;
import com.zeitheron.hammercore.internal.SimpleRegistration;
import com.zeitheron.hammercore.net.HCNet;
import com.zeitheron.improvableskills.api.RecipesParchmentFragment;
import com.zeitheron.improvableskills.api.loot.RandomBoolean;
import com.zeitheron.improvableskills.api.loot.SkillLoot;
import com.zeitheron.improvableskills.api.registry.PageletBase;
import com.zeitheron.improvableskills.api.registry.PlayerAbilityBase;
import com.zeitheron.improvableskills.api.registry.PlayerSkillBase;
import com.zeitheron.improvableskills.cmd.CommandImprovableSkills;
import com.zeitheron.improvableskills.data.PlayerDataManager;
import com.zeitheron.improvableskills.init.AbilitiesIS;
import com.zeitheron.improvableskills.init.GuiHooksIS;
import com.zeitheron.improvableskills.init.ItemsIS;
import com.zeitheron.improvableskills.init.PageletsIS;
import com.zeitheron.improvableskills.init.SkillsIS;
import com.zeitheron.improvableskills.init.TreasuresIS;
import com.zeitheron.improvableskills.items.ItemAbilityScroll;
import com.zeitheron.improvableskills.items.ItemSkillScroll;
import com.zeitheron.improvableskills.net.PacketSyncSkillData;
import com.zeitheron.improvableskills.proxy.CommonProxy;
import com.zeitheron.improvableskills.utils.loot.LootConditionRandom;
import com.zeitheron.improvableskills.utils.loot.LootConditionSkillScroll;
import com.zeitheron.improvableskills.utils.loot.LootEntryItemStack;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

@Mod(modid = InfoIS.MOD_ID, name = InfoIS.MOD_NAME, version = InfoIS.MOD_VERSION, dependencies = "required-after:hammercore", certificateFingerprint = "4d7b29cd19124e986da685107d16ce4b49bc0a97", updateJSON = "https://pastebin.com/raw/CKrGidbG")
public class ImprovableSkillsMod
{
	@Instance
	public static ImprovableSkillsMod instance;
	
	@SidedProxy(clientSide = "com.zeitheron.improvableskills.proxy.ClientProxy", serverSide = "com.zeitheron.improvableskills.proxy.CommonProxy")
	public static CommonProxy proxy;
	
	public static CreativeTabs TAB = new CreativeTabs(InfoIS.MOD_ID)
	{
		@Override
		public ItemStack createIcon()
		{
			return new ItemStack(ItemsIS.SKILLS_BOOK);
		}
	};
	
	public static final Logger LOG = LogManager.getLogger(InfoIS.MOD_ID);
	
	@EventHandler
	public void certificateViolation(FMLFingerprintViolationEvent e)
	{
		LOG.warn("*****************************");
		LOG.warn("WARNING: Somebody has been tampering with ImprovableSkills jar!");
		LOG.warn("It is highly recommended that you redownload mod from https://minecraft.curseforge.com/projects/247401 !");
		LOG.warn("*****************************");
		HammerCore.invalidCertificates.put(InfoIS.MOD_ID, "https://minecraft.curseforge.com/projects/247401");
	}
	
	@EventHandler
	public void construct(FMLConstructionEvent evt)
	{
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent evt)
	{
		SimpleRegistration.registerFieldItemsFrom(ItemsIS.class, InfoIS.MOD_ID, TAB);
		
		evt.getModMetadata().autogenerated = false;
		evt.getModMetadata().authorList = HammerCore.getHCAuthorsArray();
		
		GuiManager.registerGuiCallback(GuiHooksIS.ENCHANTMENT);
		GuiManager.registerGuiCallback(GuiHooksIS.ENCH_POWER_BOOK_IO);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent e)
	{
		proxy.init();
		TreasuresIS.register();
		
		RecipesParchmentFragment.register(ItemAbilityScroll.of(AbilitiesIS.ENCHANTING), Blocks.ENCHANTING_TABLE, Blocks.BOOKSHELF);
	}
	
	@EventHandler
	public void starting(FMLServerStartingEvent e)
	{
		e.registerServerCommand(new CommandImprovableSkills());
	}
	
	@SubscribeEvent
	public void addRecipes(RegistryEvent.NewRegistry e)
	{
		new RegistryBuilder<PlayerAbilityBase>().setName(new ResourceLocation(InfoIS.MOD_ID, "abilities")).setType(PlayerAbilityBase.class).create();
		new RegistryBuilder<PlayerSkillBase>().setName(new ResourceLocation(InfoIS.MOD_ID, "stats")).setType(PlayerSkillBase.class).create();
		new RegistryBuilder<PageletBase>().setName(new ResourceLocation(InfoIS.MOD_ID, "pagelets")).setType(PageletBase.class).create();
	}
	
	@SubscribeEvent
	public void addRecipes(RegistryEvent.Register<IRecipe> e)
	{
		IForgeRegistry<IRecipe> reg = e.getRegistry();
		
		LOG.info("RegistryEvent.Register<IRecipe>");
		reg.register(SimpleRegistration.parseShapedRecipe(new ItemStack(ItemsIS.SKILLS_BOOK), "lbl", "pgp", "lbl", 'l', "leather", 'b', Items.BOOK, 'p', "paper", 'g', "ingotGold").setRegistryName(InfoIS.MOD_ID, "skills_book"));
	}
	
	@SubscribeEvent
	public void addStats(RegistryEvent.Register<PlayerSkillBase> e)
	{
		LOG.info("RegistryEvent.Register<PlayerSkillBase>");
		SkillsIS.register(e.getRegistry());
	}
	
	@SubscribeEvent
	public void addPagelet(RegistryEvent.Register<PageletBase> e)
	{
		PageletsIS.register(e.getRegistry());
	}
	
	@SubscribeEvent
	public void addAbilities(RegistryEvent.Register<PlayerAbilityBase> e)
	{
		LOG.info("RegistryEvent.Register<PlayerAbilityBase>");
		AbilitiesIS.register(e.getRegistry());
	}
	
	@SubscribeEvent
	public void playerJoin(PlayerLoadReadyEvent e)
	{
		if(!e.getEntityPlayer().world.isRemote && e.getEntityPlayer() instanceof EntityPlayerMP)
		{
			PlayerDataManager.loadLogging(e.getEntityPlayer());
			HCNet.INSTANCE.sendTo(new PacketSyncSkillData(PlayerDataManager.getDataFor(e.getEntityPlayer())), (EntityPlayerMP) e.getEntityPlayer());
		}
	}
	
	@SubscribeEvent
	public void playerLeft(PlayerLoggedOutEvent e)
	{
		PlayerDataManager.saveQuitting(e.player);
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void lootLoad(LootTableLoadEvent e)
	{
		GameRegistry.findRegistry(PlayerSkillBase.class) //
		        .getValuesCollection() //
		        .stream() //
		        .filter(s -> s.getLoot() != null) //
		        .forEach(s -> s.getLoot().apply(e));
		
		if(e.getName().toString().toLowerCase().contains("chests/"))
		{
			RandomBoolean bool = new RandomBoolean();
			bool.n = 5;
			
			ImprovableSkillsMod.LOG.info("Injecting parchment into LootTable '" + e.getName() + "'!");
			LootEntry entry = new LootEntryItemStack(new ItemStack(ItemsIS.PARCHMENT_FRAGMENT), 2, 60, new LootFunction[0], new LootCondition[0], ItemsIS.PARCHMENT_FRAGMENT.getRegistryName().toString());
			LootPool pool1 = new LootPool(new LootEntry[] { entry }, new LootCondition[] { new LootConditionRandom(bool) }, new RandomValueRange(1), new RandomValueRange(0, 1), ItemsIS.PARCHMENT_FRAGMENT.getRegistryName().getPath());
			try
			{
				e.getTable().addPool(pool1);
			} catch(Throwable err)
			{
				ImprovableSkillsMod.LOG.error("Failed to inject parchment into LootTable '" + e.getName() + "'!!!");
				err.printStackTrace();
			}
		}
	}
}