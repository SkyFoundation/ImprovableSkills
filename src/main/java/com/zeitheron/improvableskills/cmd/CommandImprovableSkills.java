package com.zeitheron.improvableskills.cmd;

import com.zeitheron.improvableskills.InfoIS;
import com.zeitheron.improvableskills.api.PlayerSkillData;
import com.zeitheron.improvableskills.data.PlayerDataManager;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.server.command.CommandTreeBase;

public class CommandImprovableSkills extends CommandTreeBase
{
	{
		addSubcommand(new CommandTreeBase()
		{
			{
				addSubcommand(new CommandBase()
				{
					@Override
					public String getUsage(ICommandSender sender)
					{
						return "Reset skills";
					}
					
					@Override
					public String getName()
					{
						return "reset";
					}
					
					@Override
					public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
					{
						if(args.length == 0)
						{
							EntityPlayerMP mp = getCommandSenderAsPlayer(sender);
							
							PlayerDataManager.handleDataSafely(mp, data ->
							{
								data.stats.clear();
								data.stat_scrolls.clear();
								data.sync();
								
								sender.sendMessage(new TextComponentTranslation("chat." + InfoIS.MOD_ID + ":skill_reset_self"));
							});
						} else
						{
							EntityPlayerMP mp = getPlayer(server, sender, args[0]);
							
							PlayerDataManager.handleDataSafely(mp, data ->
							{
								data.stats.clear();
								data.stat_scrolls.clear();
								data.sync();
								
								sender.sendMessage(new TextComponentTranslation("chat." + InfoIS.MOD_ID + ":skill_reset_tother", mp.getName()));
								mp.sendMessage(new TextComponentTranslation("chat." + InfoIS.MOD_ID + ":skill_reset_fother", sender.getName()));
							});
						}
					}
				});
			}
			
			@Override
			public String getUsage(ICommandSender sender)
			{
				return null;
			}
			
			@Override
			public String getName()
			{
				return "skill";
			}
		});
		
		addSubcommand(new CommandTreeBase()
		{
			{
				addSubcommand(new CommandBase()
				{
					@Override
					public String getUsage(ICommandSender sender)
					{
						return "Reset skills";
					}
					
					@Override
					public String getName()
					{
						return "reset";
					}
					
					@Override
					public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
					{
						if(args.length == 0)
						{
							EntityPlayerMP mp = getCommandSenderAsPlayer(sender);
							
							PlayerDataManager.handleDataSafely(mp, data->
							{
								data.abilities.clear();
								data.enchantPower = 0F;
								data.sync();
								
								sender.sendMessage(new TextComponentTranslation("chat." + InfoIS.MOD_ID + ":ability_reset_self"));
							});
						} else
						{
							EntityPlayerMP mp = getPlayer(server, sender, args[0]);
							
							PlayerDataManager.handleDataSafely(mp, data->
							{
								data.abilities.clear();
								data.enchantPower = 0F;
								data.sync();
								
								sender.sendMessage(new TextComponentTranslation("chat." + InfoIS.MOD_ID + ":ability_reset_tother", mp.getName()));
								mp.sendMessage(new TextComponentTranslation("chat." + InfoIS.MOD_ID + ":ability_reset_fother", sender.getName()));
							});
						}
					}
				});
			}
			
			@Override
			public String getUsage(ICommandSender sender)
			{
				return null;
			}
			
			@Override
			public String getName()
			{
				return "ability";
			}
		});
	}
	
	@Override
	public String getName()
	{
		return "is3";
	}
	
	@Override
	public String getUsage(ICommandSender sender)
	{
		return "";
	}
}