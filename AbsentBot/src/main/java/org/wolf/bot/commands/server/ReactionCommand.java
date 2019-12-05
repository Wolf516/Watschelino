package org.wolf.bot.commands.server;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;

public interface ReactionCommand {

	public void performReactionCommand(Member member, TextChannel channel, MessageReaction reaction);
}
