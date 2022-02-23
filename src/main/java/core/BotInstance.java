package core;

import database.CompetitionManager;
import finance.BalanceManager;
import googledocs.legis.LegisMain;
import market.BoothController;
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
import java.sql.SQLException;

public class BotInstance {
	
	public static final long[] modids = new long[] {729100801095630858L, 282551955975307264L, 543338103218372608L, 695946463405932594L, 484039398527074335L};
	public static BotInstance botInstance;
	public JDA jda;
	public TimerQueue tt;
	ConsoleInteraction console;
	BalanceManager bank;
	BoothController booths;
	CompetitionManager competitions;
	public LegisMain legislator;
	
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
		jda.awaitReady();
		setPresence();
		SlashCommands.updateGuildCommands(jda);
		jda.addEventListener(new Handler(this));
		console = new ConsoleInteraction();
		try {
			bank = new BalanceManager();
			booths = new BoothController();
			competitions = new CompetitionManager();
		} catch(SQLException throwables) {
			throwables.printStackTrace();
		}
		legislator = new LegisMain(this);
		
		
		
		/*List<Message> completeHistory = new ArrayList<>();
		long lastMessage = 923228577909071933L;
		while(true) {
			int size = completeHistory.size();
			completeHistory.addAll(
					Lists.reverse(
							jda.getTextChannelById(826860317812326440L)
									.getHistoryAfter(lastMessage, 100)
									.complete()
									.getRetrievedHistory()
					)
			);
			if(size == completeHistory.size()) break;
			lastMessage = completeHistory.get(completeHistory.size()-1).getIdLong();
		}
		int counter = 1;
		HashMap<Long, Integer> users = new HashMap<>();
		for(Message m: completeHistory) {
			System.out.println("Checking " + counter + " against " + m.getContentRaw());
			while(counter == 87 || counter == 88 || counter == 314 || counter == 368 || counter == 484 || counter == 766 || (counter >= 974 && counter <= 978)) counter++;
			if(m.getContentRaw().contains(String.valueOf(counter))) {
				counter++;
				if(!users.containsKey(m.getAuthor().getIdLong()))
					users.put(m.getAuthor().getIdLong(), 1);
				else
					users.replace(m.getAuthor().getIdLong(), users.get(m.getAuthor().getIdLong()) + 1);
			}
		}
		long[] userArray = new long[users.size()];
		int[]  values    = new int[users.size()];
		while(users.size() > 0) {
			users.forEach((key, value) -> {
				if(currentValue > (value != null ? value : Integer.MAX_VALUE)) {
					currentValue = value;
					currentMin = key;
				}
			});
			userArray[userArray.length - users.size()] = currentMin;
			values[values.length - users.size()] = currentValue;
			users.remove(currentMin);
			currentValue = Integer.MAX_VALUE;
		}
		StringBuilder output = new StringBuilder("1k COUNTER RANKING:\n=============================\n");
		for(int i = userArray.length; i > 0; i--) {
			User u = jda.getUserById(userArray[i-1]);
			output.append(String.format("`#%02d` w/ `%03d` Points: %s%n", userArray.length-i, values[i-1], (u != null ? u.getAsMention() : "(unknown)")));
		}
		jda.getTextChannelById(826860317812326440L).sendMessage(output.toString()).queue();
		*/
	}
	
	public void setPresence() {
		jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.LISTENING, "/howlong"));
	}
	
}
