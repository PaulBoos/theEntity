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
					),
			BOOTH = new CommandData("booth", "Edit your booth")
					.addSubcommands(
							new SubcommandData("open", "Open your booth."),
							new SubcommandData("close", "Close your booth."),
							new SubcommandData("rename", "Rename your booth.")
									.addOption(OptionType.STRING, "name", "The new name of your booth.", true)),
			PRODUCT = new CommandData("product", "Edit a product")
					.addSubcommands(
							new SubcommandData("list", "List Products")
									.addOption(OptionType.BOOLEAN, "secret", "Hide this message for others.", true)
									.addOption(OptionType.USER, "owner", "Whose Booth's Products you want to see"),
							new SubcommandData("open", "Make your product purchasable")
									.addOption(OptionType.INTEGER, "id", "The id of the product", true),
							new SubcommandData("close", "Make your product unavailable")
									.addOption(OptionType.INTEGER, "id", "The id of the product", true),
							new SubcommandData("add", "Register a new product.")
									.addOptions(
											new OptionData(OptionType.STRING, "name", "How you want to call it.", true),
											new OptionData(OptionType.INTEGER, "crowns", "How many stars you want", true),
											new OptionData(OptionType.INTEGER, "stars", "How many stars you want", true),
											new OptionData(OptionType.INTEGER, "stock", "What you have in Stock. -1 for infinite stocks.", true),
											new OptionData(OptionType.STRING, "autotrade", "If you want to automatically trade.", true)
													.addChoice("automatic", "true")
													.addChoice("manual", "false")),
							new SubcommandData("remove", "Remove one product.")
									.addOption(OptionType.INTEGER, "id", "the id of your product", true),
							new SubcommandData("rename", "Modify the name of your Product")
									.addOption(OptionType.INTEGER, "id", "The id of the product", true)
									.addOption(OptionType.STRING, "name", "The new name of your product", true),
							new SubcommandData("reprice", "Modify the exchange of your product")
									.addOption(OptionType.INTEGER, "id", "The id of the product", true)
									.addOption(OptionType.INTEGER, "crowns", "Amount of crowns you demand", true)
									.addOption(OptionType.INTEGER, "stars", "Amount of stars you demand", true),
							new SubcommandData("autotrade", "Select automation mode")
									.addOptions(
											new OptionData(OptionType.INTEGER, "id", "The id of the product", true),
											new OptionData(OptionType.STRING, "autotrade", "Automation", true)
													.addChoice("automatic", "true")
													.addChoice("manual", "false")),
							new SubcommandData("restock", "Modify the stock of your product")
									.addOption(OptionType.INTEGER, "id", "The id of the product", true)
									.addOption(OptionType.INTEGER, "stock", "The new stock", true)
					),
			BUY = new CommandData("buy", "Buy a Product")
					.addOption(OptionType.INTEGER, "id", "ID of the product you want to buy", true)
					.addOption(OptionType.INTEGER, "amount", "Amount of products you want to buy"),
			ACCEPT = new CommandData("accept", "Accept the latest request")
					.addOption(OptionType.INTEGER, "id", "ID of the request you want to accept"),
			DECLINE = new CommandData("decline", "Decline the latest request")
					.addOption(OptionType.INTEGER, "id", "ID of the request you want to decline"),
			CANCEL = new CommandData("cancel", "Cancel a trade request")
					.addOption(OptionType.INTEGER, "id", "ID of the request you want to cancel");
	
	static void updateGlobalCommands(JDA jda) {
		jda.updateCommands().addCommands(
				//GLOBAL COMMANDS
				HELP, ABOUT, HOWLONG, BANK, BOOTH, PRODUCT, BUY, ACCEPT, DECLINE, CANCEL
		).queue();
	}
	
	static void updateGuildCommands(JDA jda) {
		jda.getGuildById(826170347207655434L).updateCommands().addCommands(
				//TRIBEVERSE COMMANDS
				BOOTH, PRODUCT
		).queue();
		
		jda.getGuildById(555819034877231115L).updateCommands().addCommands(
				//WYABRO COMMANDS
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
