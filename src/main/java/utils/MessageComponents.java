package utils;

import finance.Currency;

public class MessageComponents {
	
	public static String computePrice(int crowns, int stars) {
		return ((crowns == 0 && stars == 0) ? "free" : (crowns == 0) ? stars + " " + Currency.STARS.emote : (stars == 0) ? crowns + " " + Currency.CROWNS.emote : crowns + " " + Currency.CROWNS.emote + " + " + stars + " " + Currency.STARS.emote);
	}
	
}
