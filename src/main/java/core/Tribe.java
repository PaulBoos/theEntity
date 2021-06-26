package core;

import net.dv8tion.jda.api.JDA;

public enum Tribe {
	
	TEST     ("test",     "test", false, 848904154802159646L, 848904154802159646L),
	VENGIR   ("Vengir",   "ven",  true,  0L, 836240032074825769L),
	IMPERIUS ("Imperius", "imp",  true,  0L, 838865509658656839L),
	BARDUR   ("Bardur",   "bar",  true,  0L, 826498545079025766L),
	HOODRICK ("Hoodrick", "hood", true,  0L, 826488999610023936L),
	AIMO     ("Ai-Mo",    "aimo", true,  0L, 839923684427956315L);
	
	String name, call;
	boolean visible;
	long materialsChannel, actionChannel;
	public static final Tribe[] visibleTribes = new Tribe[] {VENGIR, IMPERIUS, BARDUR, HOODRICK, AIMO};
	public static final Tribe[] invisibleTribes = new Tribe[] {TEST};
	public static final Tribe[] newTurnSubs = new Tribe[] {TEST, VENGIR, IMPERIUS, BARDUR, HOODRICK, AIMO};
	
	Tribe(String name, String call, boolean visible, long materialsChannel, long actionChannel) {
		this.call = call;
		this.name = name;
		this.visible = visible;
		this.materialsChannel = materialsChannel;
		this.actionChannel = actionChannel;
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
	
}
