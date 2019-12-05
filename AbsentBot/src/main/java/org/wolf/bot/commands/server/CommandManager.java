package org.wolf.bot.commands.server;

import java.util.concurrent.ConcurrentHashMap;

import org.wolf.bot.commands.AbsentCommand;
import org.wolf.bot.commands.PrepareAbsentResponseCommand;
import org.wolf.bot.commands.ReactCommand;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;

public class CommandManager {
	public static final String ABSENT = "ABSENT";
	public static final String PREPARE_ABSENT_REACT = "PREPARE_ABSENT_REACT";
	
	public static final String REACT_COMMAND = "REACT_COMMAND";
	
	private ConcurrentHashMap<String, ServerCommand> textCommands;
	private ConcurrentHashMap<String, ReactionCommand> reactCommands;
	
	public CommandManager() { 
		this.textCommands = new ConcurrentHashMap<String, ServerCommand>();
		this.reactCommands = new ConcurrentHashMap<String, ReactionCommand>();

		this.registerTextCommand(ABSENT, new AbsentCommand());
		this.registerTextCommand(PREPARE_ABSENT_REACT, new PrepareAbsentResponseCommand());
		
		this.registerReactCommand(REACT_COMMAND, new ReactCommand());
	}

	public void registerTextCommand(String key, ServerCommand command) {
		if (this.textCommands.get(key) != null) this.unregisterTextCommand(key);
		else this.textCommands.put(key, command);
	}
	
	public void unregisterTextCommand(String key) {
		if (this.textCommands.get(key) != null) this.textCommands.remove(key);
	}

	public void registerReactCommand(String key, ReactionCommand command) {
		if (this.reactCommands.get(key) != null) this.unregisterReactCommand(key);
		else this.reactCommands.put(key, command);
	}
	
	public void unregisterReactCommand(String key) {
		if (this.reactCommands.get(key) != null) this.reactCommands.remove(key);
	}

	public void perfromTextCommand (String key, Member member, TextChannel channel, Message message) {
		if (this.textCommands.get(key)!=null) this.textCommands.get(key).performTextCommand(member, channel, message);
	}

	public void perfromReactCommand (String key, Member member, TextChannel channel, MessageReaction reaction) {
		if (this.reactCommands.get(key)!=null) this.reactCommands.get(key).performReactionCommand(member, channel, reaction);
	}
}
