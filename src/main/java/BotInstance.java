import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BotInstance {
	
	public static BotInstance botInstance;
	public JDA jda;
	public TurnTimer tt;
	
	public static void main(String[] args) throws IOException, UnsupportedFlavorException {
		if(Facts.readTokenFile()) {
			Thread t = new Thread(() -> {
				try {
					new BotInstance(Facts.firstLine);
				} catch(Exception e) {
					System.out.println("\nvvv LOGIN FAILED vvv");
					e.printStackTrace();
				}
			});
			t.start();
		} else {
			if(Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor).toString().contains(Integer.toString(0x0a))) {
				System.out.println("LOGIN FAILED - CLIPBOARD TOKEN INVALID");
			}
			Thread t = new Thread(() -> {
				try {
					new BotInstance(Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor).toString());
				} catch(Exception e) {
					System.out.println("\nvvv LOGIN FAILED vvv");
					e.printStackTrace();
				}
			});
			t.start();
		}
	}
	
	BotInstance(String token) throws LoginException, InterruptedException {
		botInstance = this;
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
		System.out.println("Login Successful");
		jda.awaitReady();
		setPresence();
		
		jda.getGuildById(555819034877231115L).updateCommands().addCommands(
				new CommandData("convert", "Convert Time into a usable Timestamp (currently at noon)")
						.addOption(OptionType.INTEGER, "turn", "Put the turn number here, as I don't memorize that currently.", true)
						.addOption(OptionType.INTEGER, "day", "Put the day here damnit", true)
						.addOption(OptionType.INTEGER, "month", "Put the month here damnit", true)
						.addOption(OptionType.INTEGER, "year", "Put the year here damnit", true),
				new CommandData("newturn", "Start a new turn at 12:00 GMT")
						.addOption(OptionType.INTEGER, "turn", "Put the turn number here, as I don't memorize that currently.", true)
						.addOption(OptionType.INTEGER, "day", "Put the day here damnit", true)
						.addOption(OptionType.INTEGER, "month", "Put the month here damnit", true),
				new CommandData("timer", "Create a custom timer")
						.addOption(OptionType.INTEGER, "turn", "Put the turn number here, as I don't memorize that currently.", true)
						.addOption(OptionType.INTEGER, "hour", "Put the hour here damnit", true)
						.addOption(OptionType.INTEGER, "minute", "Put the minute here damnit", true)
						.addOption(OptionType.INTEGER, "day", "Put the day here damnit", true)
						.addOption(OptionType.INTEGER, "month", "Put the month here damnit", true)
						.addOption(OptionType.INTEGER, "year", "Put the year here damnit", true),
				new CommandData("now", "NEXT TURN RIGHT NOW!")
						.addOption(OptionType.INTEGER, "turn", "Put the turn number here, as I don't memorize that currently.", true),
				new CommandData("howlong", "Tell me how long it takes until the next turn.")
		).queue();
//		jda.getGuildById(826170347207655434L).updateCommands().addCommands(
//				new CommandUpdateAction.CommandData("howlong", "Tell me how long it takes until the next turn.")
//		).queue();
//		jda.updateCommands().addCommands(
//				new CommandUpdateAction.CommandData("help!", "Bechotron Help Site"),
//				new CommandUpdateAction.CommandData("about!", "Legal Stuff")
//		).queue();
		//jda.getTextChannelById(831871320521441360L).sendMessage("").queue();
		//jda.getGuildById(826170347207655434L).getMemberById(764244062291099648L).modifyNickname("NOT The Almighty One").queue();
		jda.addEventListener(new Handler());
		//new TurnTimer(Instant.parse(""), instance -> instance.jda.getGuildById(0L).getTextChannelById(0L).sendMessage("TURN TIMER'S UP!"));
	}
	
	public void setPresence() {
//		jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.LISTENING, "\"help!\" or \"about!\""));
		jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.DEFAULT, "Meow."));
		System.out.println("Presence Set.");
	}
	
	private class Handler extends ListenerAdapter {
		@Override
		public void onSlashCommand(@NotNull SlashCommandEvent event) {
			switch(event.getName()) {
				case "newturn":
					Instant newturnInstant = Instant.parse(
							String.format(
									"2021-%02d-%02dT12:00:00.00Z", event.getOption("month").getAsLong(), event.getOption("day").getAsLong()
							)
					);
					event.reply("\nCreating Timer for " + newturnInstant.toString().replace("T"," @ ").replace("Z"," GMT")).complete();
					announceNewTurn(newturnInstant, (int) event.getOption("turn").getAsLong());
					break;
				case "timer":
					Instant timerInstant = Instant.parse(
							String.format(
									"%04d-%02d-%02dT%02d:%02d:00.00Z", event.getOption("year").getAsLong(), event.getOption("month").getAsLong(), event.getOption("day").getAsLong(), event.getOption("hour").getAsLong(), event.getOption("minute").getAsLong()
							)
					);
					event.reply("\nCreating Timer for " + timerInstant.toString().replace("T"," @ ").replace("Z"," GMT")).complete();
					announceNewTurn(timerInstant, (int) event.getOption("turn").getAsLong());
					break;
				case "convert":
					Instant convertInstant = Instant.parse(
							String.format(
									"%04d-%02d-%02dT12:00:00.00Z", event.getOption("year").getAsLong(), event.getOption("month").getAsLong(), event.getOption("day").getAsLong()
							)
					);
					event.reply(
							"Epoch seconds: " + convertInstant.getEpochSecond()
							+ "\nTimestamp: " + convertInstant.toString().replace("T"," @ ").replace("Z","")
					).complete();
					break;
				case "now":
					announceNewTurn(Instant.now(), (int) event.getOption("turn").getAsLong());
					event.deferReply(false).queue();
					break;
				case "howlong":
					String message =
							Duration.between(
									Instant.now(),
									Instant.ofEpochSecond(tt.getExecutionTime()))
							.toString();
					message = message.split("M")[0]
							.replace("PT","")
							.replace("H"," hours, ");
					message += " minutes.";
					event.reply("Next turn in: " + message).queue();
					break;
				default:
					event.reply("I currently have no idea how to react to this.").queue();
			}
		}
		
		public void announceNewTurn(Instant instant, int turn) {
			tt = new TurnTimer(instant, instance -> {
				instance.jda.getTextChannelById(555819034877231117L).sendMessage(jda.getGuildById(555819034877231115L).getRoleById(555830704773136385L).getAsMention()).embed(
						new EmbedBuilder()
								.setTitle("A New Turn Has Begun.")
								.setDescription("With this message, a new turn has begun.")
								.setColor(Color.GREEN)
								.setImage("https://cdn.discordapp.com/attachments/555819034877231117/847202452608647228/logo.png")
								.build()).queue();
				for(Tribe t: Tribe.newTurnSubs) t.announceNewTurn(instance.jda, turn);
			});
		}
		
		@Override
		public void onGuildMemberUpdateNickname(@NotNull GuildMemberUpdateNicknameEvent event) {
			if(event.getMember().getIdLong() == 776656382010458112L) event.getMember().modifyNickname("").queue();
		}
		
		@Override
		public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
			if(event.getAuthor().isBot()) return;
			if(event.getMessage().getMentionedMembers().contains(event.getGuild().getSelfMember())) event.getChannel().sendMessage("Yeah?").queue();
			/*if(event.getMessage().getContentRaw().equals("!besttribe")) {
				switch((int) (Math.random()*5)) {
					case 0:
						event.getChannel().sendMessage("Vengir.").queue();
						break;
					case 1:
						event.getChannel().sendMessage("What do you expect now?").queue();
						break;
					case 2:
						event.getChannel().sendMessage("Not Bardur for sure.").queue();
						break;
					case 3:
						event.getChannel().sendMessage("Stop it.").queue();
						break;
					case 4:
						event.getChannel().sendMessage("No Politics.").queue();
						break;
				}
			}*/
			if(event.getMessage().getContentRaw().startsWith("howlong")) {
				String message =
						Duration.between(
								Instant.now(),
								Instant.ofEpochSecond(tt.getExecutionTime()))
								.toString();
				message = message.split("M")[0]
						.replace("PT","")
						.replace("H"," hours, ");
				message += " minutes.";
				event.getChannel().sendMessage("Next turn in: " + message).queue();
			}
			try {
				Matcher m = Pattern.compile("\\d{1,3}[dD]\\d{1,3}").matcher(event.getMessage().getContentRaw());
				if(m.find()) {
					String s = event.getMessage().getContentRaw().substring(m.start(),m.end());
					String[] ss = s.split("[dD]");
					if(ss.length != 2) throw new Exception(s + " did somehow pass regex \"\\d{1,3}[dD]\\d{1,3}\" but fail \nString s = event.getMessage().getContentRaw().substring(m.start(),m.end());\nString[] ss = s.split(\"[dD]\");\n");
					StringBuilder results = new StringBuilder("You rolled a [").append(s).append("]```");
					int maxValue = Integer.parseInt(ss[1]);
					int dices = Integer.parseInt(ss[0]);
					if(dices > 128 || maxValue > 128) {
						event.getChannel().sendMessage("Nonono, max 128 Dices, max 128 Pips").queue();
						throw new Exception("Not important");
					}
					if(dices < 1 || maxValue < 2) {
						event.getChannel().sendMessage("[" + s + "] makes no sense.").queue();
						throw new Exception("Not important");
					}
					int[] values = new int[dices];
					int sum = 0;
					for(int i = 0; i < dices; i++) {
						values[i] = (int) (Math.random() * (maxValue)) + 1;
						sum += values[i];
						if(i % 8 == 0) results.append("\n").append(String.format("%3d", values[i]));
						else results.append(", ").append(String.format("%3d", values[i]));
					}
					results.append("```");
					results.insert(results.indexOf(s) + s.length() + 1," {Sum: " + sum + "}");
					event.getChannel().sendMessage(results).queue();
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
			if(event.getAuthor().isBot()) return;
			try {
				Matcher m = Pattern.compile("\\d+[dD]\\d+").matcher(event.getMessage().getContentRaw());
				if(m.find()) {
					String s = event.getMessage().getContentRaw().substring(m.start(),m.end());
					String[] ss = s.split("[dD]");
					if(ss.length != 2) throw new Exception(s + " did somehow pass regex \"\\d+[dD]\\d+\" but fail \nString s = event.getMessage().getContentRaw().substring(m.start(),m.end());\nString[] ss = s.split(\"[dD]\");\n");
					StringBuilder results = new StringBuilder("You rolled a [").append(s).append("]```");
					int maxValue = Integer.parseInt(ss[1]);
					int dices = Integer.parseInt(ss[0]);
					if(dices < 1 || maxValue < 2) {
						event.getChannel().sendMessage("[" + s + "] makes no sense.").queue();
						throw new Exception("Not important");
					}
					int[] values = new int[dices];
					int sum = 0;
					for(int i = 0; i < dices; i++) {
						values[i] = (int) (Math.random() * (maxValue)) + 1;
						sum += values[i];
						if(i % 8 == 0) results.append("\n").append(values[i]);
						else results.append(", ").append(values[i]);
					}
					results.append("```");
					results.insert(results.indexOf(s) + s.length() + 1," {Sum: " + sum + "}");
					event.getChannel().sendMessage(results).queue();
				}
			} catch(Exception e) {
				switch(e.getMessage()) {
					case "Provided text for message must be less than 2000 characters in length":
						event.getChannel().sendMessage("As the compiled Message would be above 2000 Characters, I cannot send it.").queue();
						break;
					case "Not important":
						break;
					default:
						e.printStackTrace();
						break;
				}
			}
		}
	}
	
}
