package org.wolf.bot.listeners;

import java.util.ArrayList;



import org.wolf.bot.absent.AbsentListManage;
import org.wolf.bot.commands.server.CommandManager;
import org.wolf.bot.common.Constants;
import org.wolf.sqlite.SQLiteManager;
import org.wolf.sqlite.entities.AbsentEntry;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter {

	private static final String RE_COMMAND = "!re";
	 
	private CommandManager manager;
	
	public CommandListener() {
		super();
		
		this.manager = new CommandManager();
	}

	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		System.out.println("message received from "+event.getMember().getAsMention()+" in Channel " + event.getTextChannel());
		
		if(event.getChannel().getName().equals(Constants.LISTENINCG_CHANNEL_NAME) && !event.getMember().getAsMention().equals(Constants.ME)) {
			
			
			if (event.getMessage().getContentDisplay().toLowerCase().equals(RE_COMMAND)) {
				this.removeMemberFromAbsentList(event.getMember(), event.getTextChannel());
			} else {
				this.manager.perfromTextCommand(CommandManager.ABSENT, event.getMember(), event.getTextChannel(), event.getMessage());
			}

		} else {
			this.manager.perfromTextCommand(CommandManager.PREPARE_ABSENT_REACT, event.getMember(), event.getTextChannel(), event.getMessage());
		}
		
	}
	
	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {
		this.manager.perfromReactCommand(CommandManager.REACT_COMMAND, event.getMember(), event.getTextChannel(), event.getReaction());
	}
	
	private void removeMemberFromAbsentList(Member member, TextChannel channel) {
		//tbd
		ArrayList<AbsentEntry> entries = SQLiteManager.singleton().getAbsentEntriesForMember(member.getAsMention());
		ArrayList<AbsentEntry> updated = new ArrayList<AbsentEntry>();
		int i = 0;
		for (AbsentEntry entry : entries) {
			i++;
			entry.setInvalid(true);
			updated.add(entry);
		}
		SQLiteManager.singleton().persistAbsentEntries(updated);
		
		if (i>0) {
			channel.sendMessage(member.getAsMention()+"\nIch habe "+i+ (i>1 ? " Einträge " : " Eintrag ") + "für dich entfernt.").queue();
		} else {
			channel.sendMessage(member.getAsMention()+"\nDu warst nicht abgemeldet.").queue();
		}

		AbsentListManage.buildAbsentList();
	}
}
