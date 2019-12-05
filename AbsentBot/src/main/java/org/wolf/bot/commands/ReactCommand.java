package org.wolf.bot.commands;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.wolf.bot.absent.AbsentListManage;
import org.wolf.bot.commands.server.ReactionCommand;
import org.wolf.bot.common.Constants;
import org.wolf.sqlite.SQLiteManager;
import org.wolf.sqlite.entities.AbsentEntry;
import org.wolf.sqlite.entities.MessageBuffer;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;

public class ReactCommand implements ReactionCommand{

	private static final String PLACEHOLDER = "%PLACEHOLDER%";
	private static final String NO_RESPONSE = PLACEHOLDER + "\nOK. Nichts hinzugefügt.";
	private static final String YES_RESPONSE = PLACEHOLDER + "1\nAlles klar. ich habe dich vom " + PLACEHOLDER + "2 bis zum " + PLACEHOLDER +"3 abgemeldet.\n"
												+ "Du kannst deine Abmeldung jederzeit mit '!re' aufheben";
	
	@Override
	public void performReactionCommand(Member member, TextChannel channel, MessageReaction reaction) {
		// TODO Auto-generated method stub
		ArrayList<MessageBuffer> bufferList = SQLiteManager.singleton().getMessageBufferEntriesForMessage(reaction.getMessageId());
		
		if (!bufferList.isEmpty()) {
			MessageBuffer buffer = bufferList.get(0);
			boolean isCheckMark = reaction.getReactionEmote().getName().equals(Constants.CHECK_MARK_BUTTON);
			boolean isCrossMark = reaction.getReactionEmote().getEmoji().equals(Constants.CROSS_MARK_BUTTON);
			String mention = member.getAsMention();
			if (buffer.getMemberMention().equals(mention) && (isCheckMark || isCrossMark)) {
				buffer.setReaction(isCheckMark ? 1:2);
				if (isCheckMark) {

					//TODO:Entries zusammenführen falls überschneidungen
					ArrayList<AbsentEntry> entries = SQLiteManager.singleton().getAbsentEntriesForMember(buffer.getMemberMention());
					entries.add(new AbsentEntry(buffer.getMemberMention(), buffer.getFromDate(), buffer.getToDate(), false));
					
					SQLiteManager.singleton().persistAbsentEntries(SQLiteManager.consolidateEntries(entries));
					
					
					SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
					String response = YES_RESPONSE.replace(PLACEHOLDER + "1", member.getAsMention())
							.replace(PLACEHOLDER + "2", sdf.format(buffer.getFromDate()))
							.replace(PLACEHOLDER + "3", sdf.format(buffer.getToDate()));
					channel.sendMessage(response).queue();
				} else {
					String response = NO_RESPONSE.replace(PLACEHOLDER, member.getAsMention());
					channel.sendMessage(response).queue();
				}
				ArrayList<MessageBuffer> updatedBuffer = new ArrayList<MessageBuffer>();
				updatedBuffer.add(buffer);
				SQLiteManager.singleton().persistMessageBufferEntries(updatedBuffer);

				AbsentListManage.buildAbsentList();
			}
		}
	}  

	

}
