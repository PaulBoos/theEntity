package core;

import database.BalanceManager;

public enum Currency {
	
	STARS("Star", 2, "<:resource:831308477325508688>"), CROWNS("Crown", 1, "<:quest:829368832656277515>");
	
	static final Currency[] currencies = new Currency[]{STARS, CROWNS};
	
	public final String name, emote;
	public final int id;
	
	Currency(String name, int id, String emote) {
		this.name = name;
		this.emote = emote;
		this.id = id;
	}
	
	public static Currency getCurrency(String name) {
		for(Currency c : currencies) if(c.name.equalsIgnoreCase(name)) return c;
		return null;
	}
	
	public static Currency getCurrency(int id) {
		for(Currency c : currencies) if(c.id == id) return c;
		return null;
	}
	
}
