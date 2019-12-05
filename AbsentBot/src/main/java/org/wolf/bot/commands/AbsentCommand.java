package org.wolf.bot.commands;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.wolf.bot.commands.server.ServerCommand;
import org.wolf.date.DateRangeBuilder;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class AbsentCommand implements ServerCommand {
	
	private static final String PLACEHOLDER = "%PLACEHOLDER%";
	private static final String RESPONSE_TEMPLATE = "Hey " + PLACEHOLDER + "1,\n" + 
													"Soll ich dich in die Absent-List eintragen?\n"+
													"Vom " + PLACEHOLDER + "2 bis zum " + PLACEHOLDER + "3?";
	
	private DateRangeBuilder dateRangeBuilder;
	
	public AbsentCommand() { 
		super();
		this.dateRangeBuilder = new DateRangeBuilder();
	}
	
	public void performTextCommand(Member member, TextChannel channel, Message message) {
		System.out.println("performing absent command");
		try {
			this.dateRangeBuilder.buildFromMessage(message.getContentDisplay());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (this.dateRangeBuilder.getStartDate() != null && this.dateRangeBuilder.getEndDate() != null) {
			
			String response = this.assembleResponse(message, member);
			Message responseMessage = new MessageBuilder()
					.setContent(response)
					.build();
			
			channel.sendMessage(responseMessage).queue();
		}
	}
	
	private String assembleResponse(Message inputMessage, Member member) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		String response = RESPONSE_TEMPLATE
				.replace(PLACEHOLDER + "1", member.getAsMention())
				.replace(PLACEHOLDER + "2", sdf.format(this.dateRangeBuilder.getStartDate()))
				.replace(PLACEHOLDER + "3", sdf.format(this.dateRangeBuilder.getEndDate()));
		
		return response; 
	}
}
