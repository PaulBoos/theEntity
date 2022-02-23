package core;

import core.WorldObject.City;
import finance.Currency;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public enum Tribe {
	
	TEST     ("test",     "test", false, 848904154802159646L, 848904154802159646L, 826171858327044176L, event -> {
		BotInstance.botInstance.jda.getTextChannelById(848904154802159646L).sendMessage("Stars per turn: " + event.getOption("stars").getAsLong() + "<:resource:831308477325508688>").queue();
		return "Done!";
	}),
	VENGIR   ("Vengir",   "ven",  true,  829449469044457502L, 836240032074825769L, 826171392428998666L, event -> {
		int stars = (int) event.getOption("stars").getAsLong();
		List<Member> members = new ArrayList<>();
		for(Member m: BotInstance.botInstance.jda.getGuildById(826170347207655434L).getMembers()) if(m.getRoles().contains(BotInstance.botInstance.jda.getRoleById(826171392428998666L))) members.add(m);
		int[] starDistribution = new int[members.size()];
		Arrays.fill(starDistribution, stars / starDistribution.length);
		for(int surplus = stars % starDistribution.length; surplus > 0; surplus--) starDistribution[(int) (Math.random() * starDistribution.length)-1]++;
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < starDistribution.length; i++) {
			sb.append("> " + members.get(i).getEffectiveName()).append(" gets ").append(starDistribution[i]).append(" <:resource:831308477325508688>\n");
			BotInstance.botInstance.bank.credit(members.get(i).getIdLong(), Currency.STARS, starDistribution[i]);
		}
		BotInstance.botInstance.jda.getTextChannelById(829449469044457502L).sendMessage("Stars this turn: " + stars + " <:resource:831308477325508688>\nResource distribution:\n\n" + sb + "Technology costs: " + event.getOption("tech").getAsString()).queue();
		return "Done!";
	}),
	IMPERIUS ("Imperius", "imp",  true,  829449164046729336L, 838865509658656839L, 826171182544977922L, event -> {
		BotInstance.botInstance.jda.getTextChannelById(829449164046729336L).sendMessage("Stars this turn: " + event.getOption("stars").getAsLong() + "<:resource:831308477325508688>\n Technology costs: " + event.getOption("tech").getAsString()).queue();
		BotInstance.botInstance.bank.credit(
				BotInstance.botInstance.jda.getGuildById(826170347207655434L)
						.getMembersWithRoles(BotInstance.botInstance.jda.getGuildById(826170347207655434L)
								.getRoleById(826426883729784832L)).get(0).getIdLong(),
				Currency.STARS, (int) event.getOption("stars").getAsLong());//Transfer Stars to Elected Representative
		return "Done! Stars transferred.";
	}),
	BARDUR   ("Bardur",   "bar",  true,  829449379144138772L, 826498545079025766L, 826171205492801556L, event -> {
		BotInstance.botInstance.jda.getTextChannelById(829449379144138772L).sendMessage("Stars this turn: " + event.getOption("stars").getAsLong() + "<:resource:831308477325508688>\n Technology costs: " + event.getOption("tech").getAsString()).queue();
		BotInstance.botInstance.bank.credit(
				BotInstance.botInstance.jda.getGuildById(826170347207655434L)
						.getMembersWithRoles(BotInstance.botInstance.jda.getGuildById(826170347207655434L)
								.getRoleById(826498429248208917L)).get(0).getIdLong(),
				Currency.STARS, (int) event.getOption("stars").getAsLong());//Transfer Stars to Jarl
		return "Done!";
	}),
	HOODRICK ("Hoodrick", "hood", true,  829449268061798400L, 826488999610023936L, 826171282319736882L, event -> {
		BotInstance.botInstance.jda.getTextChannelById(829449268061798400L).sendMessage("Stars this turn: " + event.getOption("stars").getAsLong() + "<:resource:831308477325508688>\n Technology costs: " + event.getOption("tech").getAsString()).queue();
		BotInstance.botInstance.bank.credit(
				BotInstance.botInstance.jda.getGuildById(826170347207655434L)
						.getMembersWithRoles(BotInstance.botInstance.jda.getGuildById(826170347207655434L)
								.getRoleById(826218936873779230L)).get(0).getIdLong(),
				Currency.STARS, (int) event.getOption("stars").getAsLong());//Transfer Stars to King
		return "Done!";
	}),
	AIMO     ("Ai-Mo",    "aimo", true,  829449564376793209L, 839923684427956315L, 826171470677934130L, event -> {
		BotInstance.botInstance.jda.getTextChannelById(829449564376793209L).sendMessage("Stars this turn: " + event.getOption("stars").getAsLong() + "<:resource:831308477325508688>\n Technology costs: " + event.getOption("tech").getAsString()).queue();
		BotInstance.botInstance.bank.credit(
				BotInstance.botInstance.jda.getGuildById(826170347207655434L)
						.getMembersWithRoles(BotInstance.botInstance.jda.getGuildById(826170347207655434L)
								.getRoleById(826539926073769995L)).get(0).getIdLong(),
				Currency.STARS, (int) event.getOption("stars").getAsLong());//Transfer Stars to Abbot
		return "Done!";
	});
	
	String name, call;
	boolean visible;
	long materialsChannel, actionChannel, roleid;
	TurnHandler turnHandler;
	
	public static final Tribe[] visibleTribes = new Tribe[] {VENGIR, IMPERIUS, BARDUR, HOODRICK, AIMO};
	public static final Tribe[] invisibleTribes = new Tribe[] {TEST};
	public static final Tribe[] newTurnSubs = new Tribe[] {TEST, VENGIR, IMPERIUS, BARDUR, HOODRICK, AIMO};
	
	Tribe(String name, String call, boolean visible, long materialsChannel, long actionChannel, long roleid, TurnHandler turnHandler) {
		this.call = call;
		this.name = name;
		this.visible = visible;
		this.materialsChannel = materialsChannel;
		this.actionChannel = actionChannel;
		this.roleid = roleid;
		this.turnHandler = turnHandler;
	}
	
	public static String handleUpdate(SlashCommandEvent event) {
		for(Tribe t: visibleTribes) if(t.name.equalsIgnoreCase(event.getSubcommandName())) return t.turnHandler.handleUpdate(event);
		for(Tribe t: invisibleTribes) if(t.name.equalsIgnoreCase(event.getSubcommandName())) return t.turnHandler.handleUpdate(event);
		return "Becher messed up, i did not find a tribe.";
	}
	
	@NotNull
	public Set<City> getCities() {
		return new HashSet<>(); // TODO
	}
	
	@NotNull
	public Set<City> getFortresses() {
		return new HashSet<>(); // TODO
	}
	
	@NotNull
	public Set<Long> getNobles() {
		return new HashSet<>(); // TODO
	}
	
	void announceNewTurn(JDA jda, int turn) {
		try {
			jda.getTextChannelById(actionChannel).sendMessage("```Turn #" + turn + " begins```").queue();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	void announceNewTurn(JDA jda) {
		try {
			jda.getTextChannelById(actionChannel).sendMessage("```A new turn begins.```").queue();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static @Nullable Tribe getTribeByName(String name) {
		for(Tribe t: Tribe.values()) {
			if(t.name.equalsIgnoreCase(name)) return t;
		}
		return null;
	}
	
	private interface TurnHandler {
		
		String handleUpdate(SlashCommandEvent event);
		
	}
	
}
