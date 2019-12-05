package org.wolf.bot.commands.server;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public interface ServerCommand {
	
	public void performTextCommand(Member member, TextChannel channel, Message message);
}
