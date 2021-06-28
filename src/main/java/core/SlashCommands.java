package core;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class SlashCommands {
	
	private static final CommandData
			HOWLONG = new CommandData("howlong", "Tell me how long it takes until the next turn."),
			HELP = new CommandData("help", "Bechotron Help Site"),
			ABOUT = new CommandData("about", "Legal Stuff"),
			BANK = new CommandData("bank", "Money related stuff [WIP]")
					.addSubcommands(
							new SubcommandData("balance", "Request to know your own balance.")
									.addOptions(
											new OptionData(OptionType.STRING, "currency", "The currency you want to know about")
													.addChoice("Crowns", "crown")
													.addChoice("Stars", "star")
									),
							new SubcommandData("transfer", "Transfer money to someone else")
									.addOptions(
											new OptionData(OptionType.INTEGER, "amount", "amount of credit you want to transfer", true),
											new OptionData(OptionType.STRING, "currency", "currency you want to send", true)
													.addChoice("Crowns", "crown")
													.addChoice("Stars", "star"),
											new OptionData(OptionType.USER, "receiver", "The User you want to send credit", true)
									)
					);
	
	static void updateGlobalCommands(JDA jda) {
		jda.updateCommands().addCommands(
				//GLOBAL COMMANDS
				HELP, ABOUT, HOWLONG, BANK
		).queue();
	}
	
	static void updateGuildCommands(JDA jda) {
		jda.getGuildById(826170347207655434L).updateCommands().addCommands(
				//TRIBEVERSE COMMANDS
				BANK
		).queue();
		
		jda.getGuildById(555819034877231115L).updateCommands().addCommands(
				//WYABRO COMMANDS
				BANK,
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
				new CommandData("testtimer", "Create a timer")
						.addOption(OptionType.INTEGER, "minutes", "how long to wait in minutes", true)
						.addOption(OptionType.INTEGER, "seconds", "how long to wait in seconds", true),
				new CommandData("cheat", "Cheat some money")
						.addOptions(
								new OptionData(OptionType.INTEGER, "amount", "amount of credit you want to cheat", true),
								new OptionData(OptionType.STRING, "currency", "currency you want to send", true)
										.addChoice("Crowns", "crown")
										.addChoice("Stars", "star"),
								new OptionData(OptionType.USER, "receiver", "The User you want to cheat credit", true)
						)
		).queue();
	}
	
}
