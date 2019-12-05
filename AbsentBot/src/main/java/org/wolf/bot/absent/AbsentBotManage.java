package org.wolf.bot.absent;

import java.util.ArrayList;
import java.util.List;

import org.wolf.bot.listeners.CommandListener;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.internal.entities.EntityBuilder;

public class AbsentBotManage {
	private static AbsentBotManage singleton;
	
    private ShardManager shardManager;

    private AbsentBotManage(){
        super();
        this.initialize();
    }
    
    public static void init() {
    	if (singleton == null) singleton = new AbsentBotManage();
    }
    
    public static void shutdown() {
    	singleton.shutDown();
    }
    
    public static TextChannel getTextChannelByName(String channelName) {
    	ArrayList<TextChannel> channels = new ArrayList<TextChannel>(singleton.shardManager.getGuildsByName("BotTesting4711", false).get(0).getTextChannels());
    	for (TextChannel channel : channels) {
    		if (channel.getName().equals(channelName)) return channel;
    	}
    	return null;
    }
    public static List<Member> getMembers() {
    	return singleton.shardManager.getGuildsByName("BotTesting4711", false).get(0).getMembers();
    	
    }
    
    private AbsentBotManage initialize(){
        if (this.shardManager != null) this.shutDown();
        DefaultShardManagerBuilder builder = new DefaultShardManagerBuilder()
                .setToken("NjUwNzM1NTQ3MTM2MDgyMDEx.Xed2OA.cfYNUBOh09xMCm5z9-wRTXSwabU")
                .setActivity(EntityBuilder.createActivity("Krankenschwester", null, ActivityType.DEFAULT))
                .setStatus(OnlineStatus.ONLINE)
                .addEventListeners(new CommandListener());

        try {
            this.shardManager = builder.build();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return this;
    }

    private AbsentBotManage shutDown(){
        if (this.shardManager != null){
            this.shardManager.setStatus(OnlineStatus.ONLINE);
            this.shardManager.shutdown();
        }
        return this;
    }
    
}
