package core;

import finance.BalanceManager;
import finance.Currency;
import market.ProductController;
import market.RequestController;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;
import timers.TimerQueue;
import utils.MessageComponents;

import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Handler extends ListenerAdapter {
	
	private final BotInstance botInstance;
	private static final long LOGCHANNEL = 858858060931923968L;
	private static final long[] mods = new long[] {282551955975307264L /*Becher*/, };
	
	public Handler(BotInstance botInstance) {
		this.botInstance = botInstance;
	}
	
	@Override
	public synchronized void onSlashCommand(@NotNull SlashCommandEvent event) {
		switch(event.getName()) {
			case "testtimer" -> {
				event.deferReply().queue();
				botInstance.tt.addTimer(
						new TimerQueue.Timer(
								Instant.now().plusSeconds((event.getOption("minutes").getAsLong() * 60) + (event.getOption("seconds").getAsLong())),
								instance -> instance.jda.getTextChannelById(858858060931923968L).sendMessage("Testtimer over.").queue()
						)
				);
			}
			case "newturn" -> {
				int day = (int) event.getOption("day").getAsLong();
				int month = (int) (
						event.getOption("month") != null ?
								event.getOption("month").getAsLong() :
								day >= LocalDate.now().getDayOfMonth() ?
										LocalDate.now().getMonthValue() :
										LocalDate.now().getMonthValue() + 1
				);
				int year =
						month >= LocalDate.now().getMonthValue() ?
								LocalDate.now().getYear() :
								LocalDate.now().getYear() + 1;
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
					int year = event.getOption("year") != null ? (int) event.getOption("year").getAsLong() : LocalDate.now().getYear();
					int month = event.getOption("month") != null ? (int) event.getOption("month").getAsLong() : LocalDate.now().getMonthValue();
					int day = event.getOption("day") != null ? (int) event.getOption("day").getAsLong() : LocalDate.now().getDayOfMonth();
					int hour = event.getOption("hour") != null ? (int) event.getOption("hour").getAsLong() : 12;
					int minute = event.getOption("minute") != null ? (int) event.getOption("minute").getAsLong() : 0;
					int second = event.getOption("second") != null ? (int) event.getOption("second").getAsLong() : 0;
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
									Instant.ofEpochSecond(botInstance.tt.getHead().getExecutionTime()))
									.toString();
					message = message.split("M")[0]
							.replace("PT", "")
							.replace("H", " hours, ");
					message += " minutes.";
					event.reply("Next turn in: " + message + " - <t:" + botInstance.tt.getHead().getExecutionTime() + ">").queue();
				} catch(NullPointerException e) {
					event.reply("Currently no turn is in queue.").queue();
				}
			}
			case "help", "about" -> event.reply("Very... empty here.").queue();
			case "bank" -> {
				switch(event.getSubcommandName()) {
					case "balance" -> {
						OptionMapping om = event.getOption("currency");
						if(om == null) {
							event.reply(
									"Your balance is: " +
											BotInstance.botInstance.bank.getBalance(event.getUser().getIdLong(), Currency.CROWNS)
											+ " " + Currency.CROWNS.emote
											+ " + " +
											BotInstance.botInstance.bank.getBalance(event.getUser().getIdLong(),Currency.STARS)
											+ " " + Currency.STARS.emote
							).queue();
						} else {
							Currency currency = Currency.getCurrency(om.getAsString());
							event.reply(
									"Your balance is: " +
											BotInstance.botInstance.bank.getBalance(event.getUser().getIdLong(), currency)
											+ " " + currency.emote
							).queue();
						}
					}
					case "transfer" -> {
						if(!event.isFromGuild()) {
							event.reply("I'm sorry, but currently `/bank transfer` is a guild-only command.\nYou can however still use `/bank balance`").queue();
						} else if(event.getOption("receiver").getAsMember().getUser().isBot()) {
							event.reply("You cannot send credit to bots.").queue();
						} else if(event.getOption("receiver").getAsMember().getIdLong() == event.getUser().getIdLong()) {
							event.reply("You cannot send credit to yourself.").queue();
						} else if(event.getOption("amount").getAsLong() < 0) {
							event.reply("You cannot suck credit from people.").queue();
						} else if(
								BotInstance.botInstance.bank.withdraw(
										event.getMember().getIdLong(),
										Currency.getCurrency(event.getOption("currency").getAsString()),
										(int) event.getOption("amount").getAsLong(), false)
						) {
							BotInstance.botInstance.bank.credit(
									event.getOption("receiver").getAsMember().getIdLong(),
									Currency.getCurrency(event.getOption("currency").getAsString()),
									(int) event.getOption("amount").getAsLong()
							);
							event.reply("Transferring " + event.getOption("amount").getAsLong() + " " +
									Currency.getCurrency(event.getOption("currency").getAsString()).emote + " to " + event.getOption("receiver").getAsUser().getAsTag() + ".").queue(
									interactionHook -> interactionHook.retrieveOriginal().queue(
											interactionHook2 -> interactionHook.editOriginal(interactionHook2.getContentRaw()
													.replace(event.getOption("receiver").getAsUser().getAsTag(), event.getOption("receiver").getAsUser().getAsMention())
											).queue()
									));
						} else {
							event.reply("You do not have enough credit.").queue();
						}
					}
					default -> System.out.println("WHAT THE ACTUAL FUCK");
				}
			}
			case "cheat" -> {
				BotInstance.botInstance.bank.credit(
						event.getOption("receiver").getAsUser().getIdLong(),
						Currency.getCurrency(event.getOption("currency").getAsString()),
						(int) event.getOption("amount").getAsLong());
				event.reply("Cheated " + event.getOption("amount").getAsLong() + " " +
						Currency.getCurrency(event.getOption("currency").getAsString()).emote + " to " + event.getOption("receiver").getAsUser().getAsTag() + ".").queue(
						interactionHook -> interactionHook.retrieveOriginal().queue(
								interactionHook2 -> interactionHook.editOriginal(interactionHook2.getContentRaw()
										.replace(event.getOption("receiver").getAsUser().getAsTag(), event.getOption("receiver").getAsUser().getAsMention())
								).queue()
						));
			}
			case "booth" -> {
				if(event.getChannel().getIdLong() == 849428863779733564L)
				switch(event.getSubcommandName()) {
					case "open" -> {
						if(botInstance.booths.isOpen(event.getUser().getIdLong())) {
							event.reply("Your booth is open already.").queue();
						} else {
							botInstance.booths.changeOpen(event.getUser().getIdLong(), true);
							event.reply("Your booth is now open.").queue();
						}
					}
					case "close" -> {
						if(!botInstance.booths.isOpen(event.getUser().getIdLong())) {
							event.reply("Your booth is closed already.").queue();
						} else {
							botInstance.booths.changeOpen(event.getUser().getIdLong(), false);
							event.reply("Your booth is now closed.").queue();
						}
					}
					case "rename" -> {
						botInstance.booths.changeName(event.getUser().getIdLong(), event.getOption("name").getAsString());
						event.reply("Your booth's name is now \"" + event.getOption("name").getAsString() + "\"").queue();
					}
					default -> event.reply("howw").queue();
				}
				else event.reply("Currently, you cannot do this here. Go to <#849428863779733564>").queue();
			}
			case "product" -> {
				if(event.getChannel().getIdLong() == 849428863779733564L)
				switch(event.getSubcommandName()) {
					case "list" -> {
						event.deferReply(event.getOption("secret").getAsBoolean()).queue();
						OptionMapping om = event.getOption("owner");
						long user = (om == null ? event.getUser().getIdLong() : om.getAsUser().getIdLong());
						List<ProductController.ProductContainer> list = botInstance.booths.products.getProducts(user);
						if((list.size() > 0 && botInstance.booths.isOpen(user)) || om == null) {
							EmbedBuilder eb = new EmbedBuilder().setTitle("__" + botInstance.booths.getName(user) + "__");
							for(ProductController.ProductContainer product : list) {
								if(product.open || om == null) {
									eb.addField("__" + product.name + "__", "**ID:** `" + product.productid + "`" +
													(product.stock == -1 ? "\n**Infinite** stock." : "\n**" + product.stock + "** in stock.") +
													"\nSold for **" + MessageComponents.computePrice("free", product.crowns, product.stars) +
													(product.auto ? "**\nSold **automatically \uD83D\uDD01**." : "**\nSold **manually \u270B**."),
											true);
								}
							}
							event.getHook().editOriginalEmbeds(eb.build()).queue();
						} else event.getHook().editOriginal("The user has no booth, or no products.").queue();
					}
					case "register" -> {
						String name = event.getOption("name").getAsString();
						int crowns = (int) event.getOption("crowns").getAsLong(),
								stars = (int) event.getOption("stars").getAsLong(),
								stock = (int) event.getOption("stock").getAsLong();
						boolean autotrade = Boolean.parseBoolean(event.getOption("autotrade").getAsString());
						long id = botInstance.booths.products.registerProduct(
								event.getUser().getIdLong(), name, crowns, stars, stock, autotrade, 5);
						if(id != 0)
							event.replyEmbeds(
									new EmbedBuilder()
											.setTitle("\u2705  You registered your new Product **\"__" + name + "__\"**")
											.addField(
													"with id *`#" + id + "`*.",
													"Current stock is **" + (stock == -1 ? "infinite \uD83D\uDD01" : stock + " \uD83D\uDCE6") +
															"**.\nTrade for **" + (
															(crowns == 0 && stars == 0) ?
																	"free" :
																	(crowns == 0) ?
																			stars + " " + Currency.STARS.emote :
																			(stars == 0) ?
																					crowns + " " + Currency.CROWNS.emote :
																					crowns + " " + Currency.CROWNS.emote +
																							" + " + stars + " " + Currency.STARS.emote) +
															(autotrade ? " automatically \uD83D\uDD01**." : " manually \u270B**.") +
															"\nYou can allow purchase with `/product open " + id + "`",
													false
											).build()
							).queue();
						else event.reply("Could not register product.").queue();
					}
					case "add" -> {
						int crowns = (int) event.getOption("crowns").getAsLong(),
								stars = (int) event.getOption("stars").getAsLong();
						boolean autotrade = Boolean.parseBoolean(event.getOption("autotrade").getAsString());
						/*if(botInstance.booths.products.getOwner(event.getOption("source").getAsLong()) == event.getUser().getIdLong())
							event.reply("\u2755 You cannot add your own product.").queue();
						else {*/
							long id = botInstance.booths.products.registerForeignProduct(
									event.getOption("source").getAsLong(), event.getUser().getIdLong(), crowns, stars, autotrade, 5);
							if(id != 0)
								event.replyEmbeds(
										new EmbedBuilder()
												.setTitle("\u2705  You added the Product **\"__" + botInstance.booths.products.getName(id) + "__\"**")
												.addField(
														"with id *`#" + id + "`*.",
														"You do not have starting stock, you will have to buy some." +
																".\nTrade for **" + (
																(crowns == 0 && stars == 0) ?
																		"free" :
																		(crowns == 0) ?
																				stars + " " + Currency.STARS.emote :
																				(stars == 0) ?
																						crowns + " " + Currency.CROWNS.emote :
																						crowns + " " + Currency.CROWNS.emote +
																								" + " + stars + " " + Currency.STARS.emote) +
																(autotrade ? " automatically \uD83D\uDD01**." : " manually \u270B**.") +
																"\nYou can allow purchase with `/product open " + id + "`",
														false
												).build()
								).queue();
							else event.reply("\u2755 Could not add product.").queue();
						//}
					}
					case "remove" -> {
						long productid = event.getOption("id").getAsLong();
						String productname = botInstance.booths.products.getName(productid);
						
						if(productname == null)
							event.reply("\u2754 Product not found.").queue();
						else if(!(botInstance.booths.products.getOwner(productid) == event.getUser().getIdLong()))
							event.reply("\u2755 You are not the owner of " + productname).queue();
						else if(botInstance.booths.products.dropProduct(productid))
							event.reply("<:trashcan:859412077114556416> Deleted " + productname + ".").queue();
						else event.reply("\u2754 Product not found.").queue();
					}
					case "rename" -> {
						ProductController.ProductContainer product = botInstance.booths.products.getProduct(event.getOption("id").getAsLong());
						String productname = product.name,
								newproductname = event.getOption("name").getAsString();
						if(product.isForeign)
							event.reply("You cannot edit foreign products.").queue();
						else if(productname.equals(newproductname))
							event.reply("The product is already called " + newproductname).queue();
						else if(product.open)
							event.reply("The product is open for purchase, it cannot be renamed. Use `/product close " + product.productid).queue();
						else if(!(botInstance.booths.products.getOwner(product.productid) == event.getUser().getIdLong()))
							event.reply("\u2755 You are not the owner of " + botInstance.booths.products.getName(product.productid)).queue();
						else if(botInstance.booths.products.rename(product.productid, newproductname))
							event.reply("\uD83C\uDFF7 Renamed " + productname + " to " + newproductname).queue();
						else event.reply("\u2754 Product not found.").queue();
					}
					case "reprice" -> {
						long productid = event.getOption("id").getAsLong();
						String productname = botInstance.booths.products.getName(productid);
						int crowns = (int) event.getOption("crowns").getAsLong(),
								stars = (int) event.getOption("stars").getAsLong();
						if(productname == null)
							event.reply("\u2754 Product not found.").queue();
						else if(!(botInstance.booths.products.getOwner(productid) == event.getUser().getIdLong()))
							event.reply("\u2755 You are not the owner of " + productname).queue();
						else if(botInstance.booths.products.reprice(productid, crowns, stars))
							event.reply("\uD83E\uDE99 Repriced " + productname + " to " + ((crowns == 0 && stars == 0) ?
									"free." : (crowns == 0) ? stars + " " + Currency.STARS.emote + "." : (stars == 0) ?
									crowns + " " + Currency.CROWNS.emote + "." :
									crowns + " " + Currency.CROWNS.emote + " + " + stars + " " + Currency.STARS.emote + ".")).queue();
						else event.reply("\u2754 Product not found.").queue();
					}
					case "autotrade" -> {
						long productid = event.getOption("id").getAsLong();
						String productname = botInstance.booths.products.getName(productid);
						boolean autotrade = Boolean.parseBoolean(event.getOption("autotrade").getAsString());
						if(productname == null)
							event.reply("\u2754 Product not found.").queue();
						else if(!(botInstance.booths.products.getOwner(productid) == event.getUser().getIdLong()))
							event.reply("\u2755 You are not the owner of " + productname).queue();
						else if(botInstance.booths.products.changeAutotrade(productid, autotrade))
							event.reply(autotrade ? productname + " trade set to **automatic \uD83D\uDD01**." :
									productname + " trade set to **manual \u270B**.").queue();
						else event.reply("\u2754 Product not found.").queue();
					}
					case "restock" -> {
						long productid = event.getOption("id").getAsLong();
						ProductController.ProductContainer product = botInstance.booths.products.getProduct(productid);
						int stock = (int) event.getOption("stock").getAsLong();
						
						if(product == null)
							event.reply("\u2754 Product not found.").queue();
						else if(stock < -1)
							event.reply("\u2755 You can't set the stock below -1").queue();
						else if(product.isForeign) {
							long adminid = 0;
							long itemid = product.itemid;
							while(adminid == 0) {
								if(botInstance.booths.products.isForeign(itemid))
									itemid = botInstance.booths.products.getItemId(itemid);
								else
									adminid = botInstance.booths.products.getOwner(itemid);
							}
							if(!(botInstance.booths.products.getOwner(productid) == event.getUser().getIdLong()))
								event.reply("\u2755 You are not the owner of " + productname).queue();
							else if(botInstance.booths.products.restock(productid, stock))
								event.reply("\uD83D\uDCE6 Restocked the product " + (stock == -1 ? "without limit." : ("to " + stock + "."))).queue();
							else event.reply("\u2754 Product not found.").queue();
						}
					}
					case "open" -> {
						long productid = event.getOption("id").getAsLong();
						String productname = botInstance.booths.products.getName(productid);
						
						if(productname == null)
							event.reply("\u2754 Product not found.").queue();
						else if(botInstance.booths.products.isOpen(productid))
							event.reply("\u2705 " + productname + " is already open for trading").queue();
						else if(!(botInstance.booths.products.getOwner(productid) == event.getUser().getIdLong()))
							event.reply("\u2755 You are not the owner of " + productname).queue();
						else if(botInstance.booths.products.open(productid, true))
							event.reply("\u2705 Opened trading of " + productname).queue();
						else event.reply("\u2754 Product not found.").queue();
					}
					case "close" -> {
						long productid = event.getOption("id").getAsLong();
						String productname = botInstance.booths.products.getName(productid);
						if(productname == null)
							event.reply("\u2754 Product not found.").queue();
						else if(!botInstance.booths.products.isOpen(productid))
							event.reply("\u274E " + productname + " is not being sold.").queue();
						else if(!(botInstance.booths.products.getOwner(productid) == event.getUser().getIdLong()))
							event.reply("\u2755 You are not the owner of " + productname).queue();
						else if(botInstance.booths.products.open(productid, false))
							event.reply("\u274E Closed trading of " + productname).queue();
						else event.reply("\u2754 Product not found.").queue();
					}
					default -> event.reply("As it seems, Becher has not yet implemented `/product " + event.getSubcommandName() + "`").queue();
				}
				else event.reply("Currently, you cannot do this here. Go to <#849428863779733564>").queue();
			}
			case "buy" -> {
				long productid = event.getOption("id").getAsLong();
				long buyerid = event.getUser().getIdLong();
				OptionMapping om = event.getOption("amount");
				int amount = om == null ? 1 : (int) om.getAsLong();
				ProductController.ProductContainer product = botInstance.booths.products.getProduct(productid);
				BalanceManager.BankAccount account = botInstance.bank.getAccount(buyerid);
				if(amount < 1)
					event.reply("\u2755 You would want to buy more then nothing.").queue();
				else if(product == null || !product.open)
					event.reply("\u2754 The requested product does not exist or is currently not for sale").queue();
				else if(botInstance.booths.products.getOwner(productid) == event.getUser().getIdLong())
					event.reply("\u2755 You cannot buy your own products.").queue();
				else if(account.crowns < product.crowns * amount ||
						account.stars < product.stars * amount)
					event.reply("\u2755 You cannot afford this product. ").queue();
				else if(product.auto) {
					if(product.stock < amount && product.stock != -1)
						event.reply("\u2755 Stock is too low.").queue();
					else if(!botInstance.bank.withdraw(buyerid, product.crowns * amount, product.stars * amount, false))
						event.reply("\u2755 Could not process withdrawal. Cancelled transaction.").queue();
					else {
						botInstance.bank.credit(product.ownerid, product.crowns * amount, product.stars * amount);
						if(!botInstance.booths.products.removeStock(productid, amount))
							event.reply("Could not remove stock. THIS SHOULD NEVER HAPPEN! REPORTING <@282551955975307264> PID=" + productid + " VOL=" + amount + " ST=" + product.stock).queue();
						else event.reply("\u2705 You successfully bought " + amount + "x **__" + product.name + "__**").queue();
					}
				} else {
					if(!botInstance.bank.withdraw(buyerid, product.crowns * amount, product.stars * amount, false))
						event.reply("\u2755 Could not process withdrawal. Cancelled transaction.").queue();
					else {
						long requestid = botInstance.booths.requests.createRequest(
								productid, buyerid, product.ownerid, product.crowns * amount, product.stars * amount, amount, 5);
						PrivateChannel pc = botInstance.jda.getUserById(product.ownerid).openPrivateChannel().complete();
						pc.sendMessage(new EmbedBuilder()
								.setTitle("\uD83D\uDCE9 You receive a trade-request:")
								.setDescription(
										"**" + event.getUser().getAsTag() + "** sent you a trade request!" +
										"\nThey want to buy:")
								.addField(
										(amount > 1 ? amount + "x " + product.name : product.name),
										"ID: `" + productid +
										"`\nCurrent stock is **" + (product.stock == -1 ? "infinite \uD83D\uDD01" : product.stock + " \uD83D\uDCE6") +
										"**\nCurrent price is **" + MessageComponents.computePrice("free", product.stars, product.crowns) +
										(amount > 1 && (product.crowns > 0 || product.stars > 0) ? "**\nThus you receive **" + MessageComponents.computePrice("nothing", product.stars * amount, product.crowns * amount) : "") +
										"**.\n\nAs you set this product's trade manual, you have to \n`/accept " + requestid + "` or\n`/decline " + requestid + "`",
										false)
								.build()).queue();
						event.reply("\u2705 Created trade-request. Awaiting reply. Type `/cancel " + requestid + "` to cancel." +
								(product.stock < amount ? "\n_(Notice: You created a request above stock, the seller will have to supply more to accept.)_":"")).queue();
					}
				}
			}
			case "accept" -> {
				OptionMapping om = event.getOption("id");
				HashMap<Long, RequestController.RequestContainer> requests = botInstance.booths.requests.getRequestsByTrader(event.getUser().getIdLong());
				if(requests.isEmpty())
					event.reply("\u2755 You have no requests pending.").queue();
				else {
					if(om == null && requests.size() > 1)
						event.reply("\u2705 You have multiple requests pending, please provide a request id").queue();
					else {
						RequestController.RequestContainer request = om == null ?
								(RequestController.RequestContainer) requests.values().toArray()[0] : requests.get(om.getAsLong());//TODO pretty ugly
						if(request == null)
							event.reply("\u2705 The id you provided is invalid.").queue();
						else {
							ProductController.ProductContainer product = botInstance.booths.products.getProduct(request.productid);
							if(product == null)
								event.reply("The Product does not exist anymore.").queue();
							else if(botInstance.booths.products.removeStock(request.productid, request.amount)) {
								botInstance.bank.credit(botInstance.booths.products.getOwner(request.productid),request.crowns, request.stars);
								if(!botInstance.booths.requests.dropRequest(request.requestid))
									System.out.println("Could not drop Request #" + request.requestid + ". THIS SHOULD NEVER HAPPEN!");
								event.reply("\u2705 You accepted the trade.").queue();
								PrivateChannel pc = botInstance.jda.getUserById(request.customerid).openPrivateChannel().complete();
								pc.sendMessage(
										new EmbedBuilder()
										.setTitle("\u2705 " + botInstance.jda.getUserById(product.ownerid).getAsTag() + " accepted your trade offer.")
										.setDescription(
												request.amount + "x __" + product.name + "__ for **" +
												MessageComponents.computePrice("free", request.crowns, request.stars) + "**"
										).build()
								).queue();
							} else
								event.reply("\u2705 You do not have enough stock left. Use `/product restock " + request.productid + "` to restock.").queue();
						}
					}
				}
			}
			case "decline" -> {
				OptionMapping om = event.getOption("id");
				HashMap<Long, RequestController.RequestContainer> requests = botInstance.booths.requests.getRequestsByTrader(event.getUser().getIdLong());
				if(requests.isEmpty())
					event.reply("\u2755 You have no requests pending.").queue();
				else {
					if(om == null && requests.size() > 1)
						event.reply("\u2705 You have multiple requests pending, please provide a request id").queue();
					else {
						RequestController.RequestContainer request = om == null ?
								(RequestController.RequestContainer) requests.values().toArray()[0] : requests.get(om.getAsLong());//TODO pretty ugly
						if(request == null)
							event.reply("\u2705 The id you provided is invalid.").queue();
						else {
							ProductController.ProductContainer product = botInstance.booths.products.getProduct(request.productid);
							if(product == null) {
								botInstance.bank.credit(request.customerid, request.crowns, request.stars);
								if(!botInstance.booths.requests.dropRequest(request.requestid))
									System.out.println("Could not drop Request #" + request.requestid + ". THIS SHOULD NEVER HAPPEN!");
								event.reply("The Product does not exist anymore. The request is declined.").queue();
								PrivateChannel pc = botInstance.jda.getUserById(request.customerid).openPrivateChannel().complete();
								pc.sendMessage(
										new EmbedBuilder()
												.setTitle("\u274E " + botInstance.jda.getUserById(product.ownerid).getAsTag() + " declined your trade offer.")
												.setDescription(
														request.amount + "x __" + product.name + "__ for **" +
																MessageComponents.computePrice("free", request.crowns, request.stars) + "**"
												).build()
								).queue();
							} else {
								botInstance.bank.credit(request.customerid, request.crowns, request.stars);
								if(!botInstance.booths.requests.dropRequest(request.requestid))
									System.out.println("Could not drop Request #" + request.requestid + ". THIS SHOULD NEVER HAPPEN!");
								PrivateChannel pc = botInstance.jda.getUserById(request.customerid).openPrivateChannel().complete();
								pc.sendMessage(
										new EmbedBuilder()
												.setTitle("\u274E " + botInstance.jda.getUserById(product.ownerid).getAsTag() + " declined your trade offer.")
												.setDescription(
														request.amount + "x __" + product.name + "__ for **" +
																MessageComponents.computePrice("free", request.crowns, request.stars) + "**"
												).build()
								).queue();
								event.reply("\u274E The offer was declined.").queue();
							}
						}
					}
				}
			}
			case "cancel" -> {
				OptionMapping om = event.getOption("id");
				HashMap<Long, RequestController.RequestContainer> requests = botInstance.booths.requests.getRequestsByTrader(event.getUser().getIdLong());
				if(requests.isEmpty())
					event.reply("\u2755 You have no requests pending.").queue();
				else {
					if(om == null && requests.size() > 1)
						event.reply("\u2705 You have multiple requests pending, please provide a request id").queue();
					else {
						RequestController.RequestContainer request = om == null ?
								(RequestController.RequestContainer) requests.values().toArray()[0] : requests.get(om.getAsLong());//TODO pretty ugly
						if(request == null)
							event.reply("\u2705 The id you provided is invalid.").queue();
						else {
							ProductController.ProductContainer product = botInstance.booths.products.getProduct(request.productid);
							if(botInstance.booths.requests.dropRequest(request.requestid)) {
								botInstance.bank.credit(request.customerid, request.crowns, request.stars);
								PrivateChannel pc = botInstance.jda.getUserById(botInstance.booths.products.getOwner(request.productid)).openPrivateChannel().complete();
								if(product == null)
									pc.sendMessage("\u274E " + event.getUser().getAsTag() + " cancelled his trade offer.").queue();
								else
									pc.sendMessage(
										new EmbedBuilder()
												.setTitle("\u274E " + event.getUser().getAsTag() + " cancelled his trade offer.")
												.setDescription(request.amount + "x __" + product.name + "__ for **" +
																MessageComponents.computePrice("free", request.crowns, request.stars) + "**"
												).build()
									).queue();
								event.reply("\u274E You cancelled your offer.").queue();
							} else
								event.reply("\u2705 I could not drop the request.").queue();
						}
					}
				}
			}
			default -> event.reply("I currently have no idea how to react to this.").queue();
		}
	}
	
	public void announceNewTurn(Instant instant, int turn) {
		botInstance.tt.addTimer(new TimerQueue.Timer(instant, instance -> {
			instance.jda.getTextChannelById(826170348756140125L).sendMessage(botInstance.jda.getGuildById(826170347207655434L).getRoleById(845263298446491690L).getAsMention()).embed(
					new EmbedBuilder()
							.setTitle("A New Turn Has Begun.")
							.setDescription("With this message, a new turn has begun.")
							.setColor(Color.GREEN)
							.setImage("https://cdn.discordapp.com/attachments/555819034877231117/847202452608647228/logo.png")
							.build()).queue();
			for(Tribe t : Tribe.newTurnSubs) t.announceNewTurn(instance.jda, turn);
			botInstance.tt = null;
		}));
	}
	
	public void announceNewTurn(Instant instant) {
		botInstance.tt.addTimer(new TimerQueue.Timer(instant, instance -> {
			instance.jda.getTextChannelById(826170348756140125L).sendMessage(botInstance.jda.getGuildById(826170347207655434L).getRoleById(845263298446491690L).getAsMention()).embed(
					new EmbedBuilder()
							.setTitle("A New Turn Has Begun.")
							.setDescription("With this message, a new turn has begun.")
							.setColor(Color.GREEN)
							.setImage("https://cdn.discordapp.com/attachments/555819034877231117/847202452608647228/logo.png")
							.build()).queue();
			for(Tribe t : Tribe.newTurnSubs) t.announceNewTurn(instance.jda);
			botInstance.tt = null;
		}));
	}
	
	@Override
	public void onGuildMemberUpdateNickname(@NotNull GuildMemberUpdateNicknameEvent event) {
		if(event.getMember().getIdLong() == 776656382010458112L) event.getMember().modifyNickname(null).queue();
	}
	
	@Override
	public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
		if(event.getAuthor().isBot()) return;
		if(event.getMessage().getMentionedMembers().contains(event.getGuild().getSelfMember()))
			event.getChannel().sendMessage("Yeah?").queue();
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
								Instant.ofEpochSecond(botInstance.tt.getHead().getExecutionTime())
						).toString();
				message = message.split("M")[0]
						.replace("PT", "")
						.replace("H", " hours, ");
				message += " minutes.";
				event.getChannel().sendMessage("Next turn in: " + message).queue();
			} catch(NullPointerException e) {
				event.getChannel().sendMessage("Currently no turn is in queue.").queue();
			}
		}
		try {
			Matcher m = Pattern.compile("\\d{1,3}[dD]\\d{1,3}").matcher(event.getMessage().getContentRaw());
			if(m.find()) {
				String s = event.getMessage().getContentRaw().substring(m.start(), m.end());
				String[] ss = s.split("[dD]");
				if(ss.length != 2)
					throw new Exception(s + " did somehow pass regex \"\\d{1,3}[dD]\\d{1,3}\" but fail \nString s = event.getMessage().getContentRaw().substring(m.start(),m.end());\nString[] ss = s.split(\"[dD]\");\n");
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
				results.insert(results.indexOf(s) + s.length() + 1, " {Sum: " + sum + "}");
				event.getChannel().sendMessage(results).queue();
			}
		} catch(Exception e) {
			if(!e.getMessage().equals("Not important")) e.printStackTrace();
		}
		if(event.getMessage().getContentRaw().startsWith("makebinary")) {
			StringBuilder out = new StringBuilder();
			for(String arg: event.getMessage().getContentRaw().substring(11).split(" ")) {
				char[] chars = arg.toCharArray();
				int[] ints = new int[chars.length];
				for(int i = 0; i < chars.length; i++)
					ints[i] = chars[i];
				for(int Int: ints) {
					StringBuilder currentInt = new StringBuilder();
					int pot = 1;
					while(Int > 0) {
						currentInt.append(Int % ((int) Math.pow(2, pot)) > 0 ? '1':'0');
						Int -= Int % (int) Math.pow(2, pot);
						pot++;
					}
					out.append(currentInt.reverse());
					out.append(' ');
				}
			}
			event.getMessage().delete().queue();
			event.getChannel().sendMessage(out.toString()).queue();
		}
	}
	
	@Override
	public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
		if(event.getAuthor().isBot()) return;
		try {
			Matcher m = Pattern.compile("\\d+[dD]\\d+").matcher(event.getMessage().getContentRaw());
			if(m.find()) {
				String s = event.getMessage().getContentRaw().substring(m.start(), m.end());
				String[] ss = s.split("[dD]");
				if(ss.length != 2)
					throw new Exception(s + " did somehow pass regex \"\\d+[dD]\\d+\" but fail \nString s = event.getMessage().getContentRaw().substring(m.start(),m.end());\nString[] ss = s.split(\"[dD]\");\n");
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
				results.insert(results.indexOf(s) + s.length() + 1, " {Sum: " + sum + "}");
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
							Instant.ofEpochSecond(botInstance.tt.getHead().getExecutionTime()))
							.toString();
			message = message.split("M")[0]
					.replace("PT", "")
					.replace("H", " hours, ");
			message += " minutes.";
			event.getChannel().sendMessage("Next turn in: " + message).queue();
		}
	}
	
}
