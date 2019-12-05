package org.wolf.bot.commands;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import org.wolf.bot.commands.server.ServerCommand;
import org.wolf.bot.common.Constants;
import org.wolf.date.DateRangeBuilder; 
import org.wolf.sqlite.SQLiteManager;
import org.wolf.sqlite.entities.MessageBuffer;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class PrepareAbsentResponseCommand implements ServerCommand{

	public PrepareAbsentResponseCommand() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public void performTextCommand(Member member, TextChannel channel, Message message) {
		// TODO Auto-generated method stub
		if (!message.getContentDisplay().contains("Absent-List")) return;
		
		message.addReaction(Constants.CHECK_MARK_BUTTON).queue();
		message.addReaction(Constants.CROSS_MARK_BUTTON).queue();
		
		Member mentionedMember = message.getMentionedMembers().get(0);
		
		DateRangeBuilder dateRangeBuilder = new DateRangeBuilder();
		try {
			String[] parts = message.getContentDisplay().split("\n");
			String fromTo = parts[parts.length-1];
			dateRangeBuilder.buildFromMessage(fromTo);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		MessageBuffer buffer = new MessageBuffer(mentionedMember.getAsMention(), 
												 message.getId(), 
												 0, 
												 new Date(), 
												 dateRangeBuilder.getStartDate(), 
												 dateRangeBuilder.getEndDate());
		//logging
		System.out.println(message.getContentDisplay());
		System.out.println(message.getContentRaw());
		System.out.println(mentionedMember.getAsMention());
		System.out.println(buffer.toString());
		
		ArrayList<MessageBuffer> entries = new ArrayList<MessageBuffer>();
		entries.add(buffer);
		SQLiteManager.singleton().persistMessageBufferEntries(entries);
		
		
	}

}
