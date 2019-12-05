package org.wolf.bot.absent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.wolf.bot.common.Constants;
import org.wolf.sqlite.SQLiteManager;
import org.wolf.sqlite.entities.AbsentEntry;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class AbsentListManage {
	
	private static final String PLACEHOLDER = "%PLACEHOLDER%";
	private static final String TEMPLATE_HEADER = "Mitgliederliste:\n\n";
	private static final String ACTIVE_MEMBER = PLACEHOLDER + ": `Aktiv`\n";
	private static final String INACTIVE_MEMBER = PLACEHOLDER + 1 + ":```\n" + PLACEHOLDER + 2 + "```\n";
	private static final String INACTIVE_TIME_FRAME = PLACEHOLDER + 1 + " - " + PLACEHOLDER + 2 + "\n";
	private static final String TEMPLATE_FOOTER = "\nStand:(" + PLACEHOLDER + ")";
	
	public static void buildAbsentList() {
		TextChannel channel = AbsentBotManage.getTextChannelByName(Constants.RESPOND_CHANNEL_NAME);
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		SimpleDateFormat sdfTs = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
		
		ArrayList<Member> members = new ArrayList<Member>(AbsentBotManage.getMembers());
		
		String text = TEMPLATE_HEADER;
		for (Member member : members) {
			String mention = member.getAsMention();
			String nickname = member.getUser().getAsTag();
			
			ArrayList<AbsentEntry> absentEntries = SQLiteManager.singleton().getAbsentEntriesForMember(mention);
			if (absentEntries.size()==0) {
				text = text + ACTIVE_MEMBER.replace(PLACEHOLDER, nickname);
			}
			else {
				String timeframes = "";
				for (AbsentEntry entry : absentEntries) {
					timeframes = timeframes + INACTIVE_TIME_FRAME.replace(PLACEHOLDER + 1, sdf.format(entry.getFromDate())).replace(PLACEHOLDER+2, sdf.format(entry.getToDate()));
				}
				text = text + INACTIVE_MEMBER.replace(PLACEHOLDER+1, nickname).replace(PLACEHOLDER+2, timeframes);
			}
		}
		
		text = text + TEMPLATE_FOOTER.replace(PLACEHOLDER, sdfTs.format(now));
		
		channel.sendMessage(text).queue();
	}
	/*
	public static void clearInputChannel() {
		TextChannel channel = AbsentBotManage.getTextChannelByName(Constants.LISTENINCG_CHANNEL_NAME);
		channel.deleteMessages(channel.getHistory().getRetrievedHistory());
	}
	*/
}
