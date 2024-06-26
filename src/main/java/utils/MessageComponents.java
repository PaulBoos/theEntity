package utils;

import finance.Currency;

public class MessageComponents {
	
	public static String displayPrice(String nullText, int crowns, int stars) {
		return ((crowns == 0 && stars == 0) ? nullText : (crowns == 0) ? stars + " " + Currency.STARS.emoji : (stars == 0) ? crowns + " " + Currency.CROWNS.emoji : crowns + " " + Currency.CROWNS.emoji + " + " + stars + " " + Currency.STARS.emoji);
	}
	
	public static String calculateSurrogate(String input) {
		return String.format("%x %x", ((Integer.parseInt(input, 16) - 65536) / 1024) + 55296, ((Integer.parseInt(input, 16) - 65536) % 1024) + 56320).toUpperCase();
	}
	
}
