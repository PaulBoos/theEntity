package core;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;
import timers.TimerQueue;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BotInstance {
	
	public static BotInstance botInstance;
	public JDA jda;
	public TimerQueue tt;
	ConsoleInteraction console;
	
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
		
		jda.getGuildById(555819034877231115L).updateCommands().addCommands(
				new CommandData("convert", "Convert Time into a usable Timestamp")
						.addOption(OptionType.INTEGER, "day", "Put the day here damnit")
						.addOption(OptionType.INTEGER, "month", "Put the month here damnit")
						.addOption(OptionType.INTEGER, "year", "Put the year here damnit")
						.addOption(OptionType.INTEGER, "hour", "Put the hour here damnit")
						.addOption(OptionType.INTEGER, "minute", "Put the minute here damnit")
						.addOption(OptionType.INTEGER, "second", "Put the second here damnit"),
				new CommandData("newturn", "Start a new turn at 12:00 GMT")
						.addOption(OptionType.INTEGER, "day", "Put the day here damnit", true)
						.addOption(OptionType.INTEGER, "month", "Put the month here damnit")
						.addOption(OptionType.INTEGER, "turn", "Put the turn number here, as I don't memorize that currently."),
				new CommandData("timer", "Create a custom timer")
						.addOption(OptionType.INTEGER, "hour", "Put the hour here damnit", true)
						.addOption(OptionType.INTEGER, "minute", "Put the minute here damnit", true)
						.addOption(OptionType.INTEGER, "day", "Put the day here damnit", true)
						.addOption(OptionType.INTEGER, "month", "Put the month here damnit", true)
						.addOption(OptionType.INTEGER, "year", "Put the year here damnit", true)
						.addOption(OptionType.INTEGER, "turn", "Put the turn number here, as I don't memorize that currently."),
				new CommandData("now", "NEXT TURN RIGHT NOW!")
						.addOption(OptionType.INTEGER, "turn", "Put the turn number here, as I don't memorize that currently."),
				new CommandData("howlong", "Tell me how long it takes until the next turn."),
				new CommandData("testtimer", "Create a timer")
						.addOption(OptionType.INTEGER, "minutes", "how long to wait in minutes", true)
						.addOption(OptionType.INTEGER, "seconds", "how long to wait in seconds", true)
		).queue();
		jda.getGuildById(826170347207655434L).updateCommands().addCommands(
				new CommandData("howlong", "Tell me how long it takes until the next turn.")
		).queue();
//		jda.updateCommands().addCommands(
//				new CommandData("help", "Bechotron Help Site"),
//				new CommandData("about", "Legal Stuff")
//		).queue();
//		jda.getTextChannelById(831871320521441360L).sendMessage("").queue();
//		jda.getGuildById(826170347207655434L).getMemberById(764244062291099648L).modifyNickname("NOT The Almighty One").queue();
		jda.addEventListener(new Handler());
//		new TurnTimer(Instant.parse(""), instance -> instance.jda.getGuildById(0L).getTextChannelById(0L).sendMessage("TURN TIMER'S UP!"));
		console = new ConsoleInteraction();
	}
	
	public void setPresence() {
		jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.LISTENING, "\\howlong"));
//		jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.of(Activity.ActivityType.DEFAULT, "\"howlong\" for Turn Time left!"));
		System.out.println("Presence Set.");
	}
	
	private class Handler extends ListenerAdapter {
		@Override
		public void onSlashCommand(@NotNull SlashCommandEvent event) {
			switch(event.getName()) {
				case "newturn" -> {
					int day = (int) event.getOption("day").getAsLong();
					int month = (int) (
							event.getOption("month") != null ?
									event.getOption("month").getAsLong() :
									day >= LocalDate.now().getDayOfMonth()?
											LocalDate.now().getMonthValue():
											LocalDate.now().getMonthValue()+1
					);
					int year =
							month >= LocalDate.now().getMonthValue() ?
									LocalDate.now().getYear():
									LocalDate.now().getYear()+1;
					Instant i = Instant.parse(
							String.format(
									"%04d-%02d-%02dT12:00:00.00Z", year, month, day
							)
					);
					event.reply("\nCreating timer for " + i.toString().replace("T", " @ ").replace("Z", " GMT")).complete();
					OptionMapping s = event.getOption("turn");
					if(s == null)
						announceNewTurn(i);
					else
						announceNewTurn(i, (int) event.getOption("turn").getAsLong());
				}
				case "timer" -> {
					Instant i = Instant.parse(
							String.format(
									"%04d-%02d-%02dT%02d:%02d:00.00Z", event.getOption("year").getAsLong(), event.getOption("month").getAsLong(), event.getOption("day").getAsLong(), event.getOption("hour").getAsLong(), event.getOption("minute").getAsLong()
							)
					);
					event.reply("\nCreating timer for " + i.toString().replace("T", " @ ").replace("Z", " GMT")).complete();
					OptionMapping s = event.getOption("turn");
					if(s == null)
						announceNewTurn(i);
					else
						announceNewTurn(i, (int) event.getOption("turn").getAsLong());
				}
				case "convert" -> {
					try {
						int  year   = event.getOption("year")   != null ? (int) event.getOption("year")  .getAsLong() : LocalDate.now().getYear();
						int  month  = event.getOption("month")  != null ? (int) event.getOption("month") .getAsLong() : LocalDate.now().getMonthValue();
						int  day    = event.getOption("day")    != null ? (int) event.getOption("day")   .getAsLong() : LocalDate.now().getDayOfMonth();
						int  hour   = event.getOption("hour")   != null ? (int) event.getOption("hour")  .getAsLong() : 12;
						int  minute = event.getOption("minute") != null ? (int) event.getOption("minute").getAsLong() : 0;
						int  second = event.getOption("second") != null ? (int) event.getOption("second").getAsLong() : 0;
						Instant i = Instant.parse(
								String.format(
										"%04d-%02d-%02dT%02d:%02d:%02d.00Z", year, month, day, hour, minute, second
								)
						);
						event.reply(
								"Epoch seconds: `" + i.getEpochSecond()
										+ "`\nTimestamp: `" + i
										+ "`\nGMT: `" + i.toString().replace("T", " @ ").replace("Z", "") + "`"
						).complete();
					} catch(Exception e) {
						//event.reply(e.getMessage()).queue();
						e.printStackTrace();
					}
				}
				case "now" -> {
					OptionMapping s = event.getOption("turn");
					if(s == null)
						announceNewTurn(Instant.now());
					else
						announceNewTurn(Instant.now(), (int) event.getOption("turn").getAsLong());
					event.deferReply(false).queue();
				}
				case "howlong" -> {
					try {
						String message =
								Duration.between(
										Instant.now(),
										Instant.ofEpochSecond(tt.getHead().getExecutionTime()))
										.toString();
						message = message.split("M")[0]
								.replace("PT","")
								.replace("H"," hours, ");
						message += " minutes.";
						event.reply("Next turn in: " + message).queue();
					} catch(NullPointerException e) {
						event.reply("Currently no turn is in queue.").queue();
					}
				}
				case "help", "about" -> event.reply("Very... empty here.").queue();
				case "testtimer" -> {
					event.deferReply().queue();
					tt.addTimer(
							new TimerQueue.Timer(
									Instant.now().plusSeconds((event.getOption("minutes").getAsLong() * 60) + (event.getOption("seconds").getAsLong())),
									instance -> instance.jda.getTextChannelById(858858060931923968L).sendMessage("Testtimer over.").queue()
							)
					);
				}
				default -> event.reply("I currently have no idea how to react to this.").queue();
			}
		}
		
		public void announceNewTurn(Instant instant, int turn) {
			tt.addTimer(new TimerQueue.Timer(instant, instance -> {
				instance.jda.getTextChannelById(826170348756140125L).sendMessage(jda.getGuildById(826170347207655434L).getRoleById(845263298446491690L).getAsMention()).embed(
						new EmbedBuilder()
								.setTitle("A New Turn Has Begun.")
								.setDescription("With this message, a new turn has begun.")
								.setColor(Color.GREEN)
								.setImage("https://cdn.discordapp.com/attachments/555819034877231117/847202452608647228/logo.png")
								.build()).queue();
				for(Tribe t: Tribe.newTurnSubs) t.announceNewTurn(instance.jda, turn);
				tt = null;
			}));
		}
		
		public void announceNewTurn(Instant instant) {
			tt.addTimer(new TimerQueue.Timer(instant, instance -> {
				instance.jda.getTextChannelById(826170348756140125L).sendMessage(jda.getGuildById(826170347207655434L).getRoleById(845263298446491690L).getAsMention()).embed(
						new EmbedBuilder()
								.setTitle("A New Turn Has Begun.")
								.setDescription("With this message, a new turn has begun.")
								.setColor(Color.GREEN)
								.setImage("https://cdn.discordapp.com/attachments/555819034877231117/847202452608647228/logo.png")
								.build()).queue();
				for(Tribe t: Tribe.newTurnSubs) t.announceNewTurn(instance.jda);
				tt = null;
			}));
		}
		
		@Override
		public void onGuildMemberUpdateNickname(@NotNull GuildMemberUpdateNicknameEvent event) {
			if(event.getMember().getIdLong() == 776656382010458112L) event.getMember().modifyNickname(null).queue();
		}
		
		@Override
		public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
			if(event.getAuthor().isBot()) return;
			if(event.getMessage().getMentionedMembers().contains(event.getGuild().getSelfMember())) event.getChannel().sendMessage("Yeah?").queue();
			if(event.getMessage().getContentRaw().equals("!besttribe")) {
				switch((int) (Math.random() * 6)) {
					case 0 -> event.getChannel().sendMessage("Vengir.").queue();
					case 1 -> event.getChannel().sendMessage("What do you expect now?").queue();
					case 2 -> event.getChannel().sendMessage("Not Bardur for sure.").queue();
					case 3 -> event.getChannel().sendMessage("Stop it.").queue();
					case 4 -> event.getChannel().sendMessage("No Politics.").queue();
					case 5 -> event.getChannel().sendMessage(event.getGuild().getEmotesByName("vengir", true).get(0).getAsMention()).queue();
				}
			}
			if(event.getMessage().getContentRaw().contains("number")) {
				switch((int) (Math.random() * 10)) {
					case 0 -> event.getChannel().sendMessage("69").queue();
					case 1 -> event.getChannel().sendMessage("420").queue();
					case 2 -> event.getChannel().sendMessage("42").queue();
				}
			}
			if(event.getMessage().getContentRaw().startsWith("howlong")) {
				try {
					String message =
							Duration.between(
									Instant.now(),
									Instant.ofEpochSecond(tt.getHead().getExecutionTime())
							).toString();
					message = message.split("M")[0]
							.replace("PT","")
							.replace("H"," hours, ");
					message += " minutes.";
					event.getChannel().sendMessage("Next turn in: " + message).queue();
				} catch(NullPointerException e) {
					event.getChannel().sendMessage("Currently no turn is in queue.").queue();
				}
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
				if(!e.getMessage().equals("Not important")) e.printStackTrace();
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
			if(event.getMessage().getContentRaw().equals("howlong")) {
				String message =
						Duration.between(
								Instant.now(),
								Instant.ofEpochSecond(tt.getHead().getExecutionTime()))
								.toString();
				message = message.split("M")[0]
						.replace("PT","")
						.replace("H"," hours, ");
				message += " minutes.";
				event.getChannel().sendMessage("Next turn in: " + message).queue();
			}
		}
	}
	
}
