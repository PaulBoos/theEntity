package core;

import database.BalanceManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import timers.TimerQueue;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class BotInstance {
	
	public static BotInstance botInstance;
	public JDA jda;
	public TimerQueue tt;
	ConsoleInteraction console;
	BalanceManager bank;
	
	public static void main(String[] args) throws IOException, UnsupportedFlavorException {
		if(Facts.readTokenFile()) {
			new Thread(() -> {
				try {
					new BotInstance(Facts.firstLine);
				} catch(Exception e) {
					System.out.println("\nvvv LOGIN FAILED vvv");
					e.printStackTrace();
				}
			}).start();
		} else {
			if(Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor).toString().contains(Integer.toString(0x0a))) {
				System.out.println("LOGIN FAILED - CLIPBOARD TOKEN INVALID - CONTAINS LINE BREAK CHARACTER");
			}
			new Thread(() -> {
				try {
					new BotInstance(Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor).toString());
				} catch(Exception e) {
					System.out.println("\nvvv LOGIN FAILED vvv");
					e.printStackTrace();
				}
			}).start();
		}
	}
	
	BotInstance(String token) throws LoginException, InterruptedException {
		botInstance = this;
		tt = new TimerQueue();
		jda = JDABuilder.create(
				token,
				GatewayIntent.GUILD_MEMBERS,
				GatewayIntent.GUILD_BANS,
				GatewayIntent.GUILD_EMOJIS,
				GatewayIntent.GUILD_VOICE_STATES,
				GatewayIntent.GUILD_MESSAGES,
				GatewayIntent.GUILD_MESSAGE_REACTIONS,
				GatewayIntent.GUILD_PRESENCES,
				GatewayIntent.DIRECT_MESSAGES,
				GatewayIntent.DIRECT_MESSAGE_REACTIONS
		).enableCache(
				CacheFlag.ACTIVITY,
				CacheFlag.CLIENT_STATUS
		).setMemberCachePolicy(
				MemberCachePolicy.ALL
		).build();
		Runtime.getRuntime().addShutdownHook(new Thread(() -> jda.shutdown()));
		System.out.println("Login Successful");
		jda.awaitReady();
		setPresence();
		SlashCommands.updateGuildCommands(jda);
		jda.addEventListener(new Handler(this));
		console = new ConsoleInteraction();
		bank = new BalanceManager();
	}
	
	public void setPresence() {
		jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.LISTENING, "/howlong"));
	}
	
}
