package core;

import finance.Currency;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Member;
import utils.MessageComponents;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static java.lang.Math.pow;

public class ConsoleInteraction {
	
	BufferedReader reader;
	HashMap<String, codeContainer> commands = new HashMap<>();
	
	ConsoleInteraction() {
		reader = new BufferedReader(new InputStreamReader(System.in));
		commands.put("shutdown", args -> {
			System.out.println("Disconnecting...");
			BotInstance.botInstance.jda.shutdown();
			System.exit(0);
			return "Shutting down...";
		});
		commands.put("updateSlash", args -> {
			SlashCommands.updateGlobalCommands(BotInstance.botInstance.jda);
			return "Updating Global Slash Commands";
		});
		commands.put("cheat", args -> {
			BotInstance.botInstance.bank.credit(
					Long.parseLong(args[0]),
					Currency.getCurrency(args[1]),
					Integer.parseInt(args[2])
			);
			return
					"Cheat successful. New balance: " + BotInstance.botInstance.bank.getBalance(
							Long.parseLong(args[0]),
							Currency.getCurrency(args[1])
					) + " (" + Currency.getCurrency(args[1]).name + ")";
		});
		commands.put("balance", args -> "Balance is " + BotInstance.botInstance.bank.getBalance(
				Long.parseLong(args[0]),
				Currency.getCurrency(args[1])
		) + " (" + Currency.getCurrency(args[1]).name + ")");
		commands.put("emotecode", args -> {
			System.out.println("Hits:");
			for(Emote e: BotInstance.botInstance.jda.getEmotesByName(args[0], true))
				System.out.println(e.getAsMention());
			return "~end";
		});
		commands.put("share", args -> {
			List<Member> memberList = BotInstance.botInstance.jda.getGuildById(826170347207655434L).getMembers();
			for(Member m: memberList) BotInstance.botInstance.bank.credit(m.getIdLong(), Currency.getCurrency(args[1]), Integer.parseInt(args[0]));
			return "Cheat successful.";
		});
		commands.put("tagbyid", args -> BotInstance.botInstance.jda.getUserById(args[0]).getAsTag());
		addCommand("bintoascii", args -> {
			StringBuilder out = new StringBuilder();
			for(String arg: args) {
				boolean[] bools = new boolean[arg.length()];
				char[] chars = arg.toCharArray();
				for(int i = 0; i < arg.length(); i++)
					bools[i] = chars[i] == '1';
				int x = 0;
				for(int i = 0; i < bools.length; i++)
					x += bools[i] ? (int) pow(2, bools.length-i-1) : 0;
				out.append((char) x);
			}
			return out.toString();
		});
		addCommand("asciitobin", args -> {
			StringBuilder out = new StringBuilder();
			for(String arg: args) {
				char[] chars = arg.toCharArray();
				int[] ints = new int[chars.length];
				for(int i = 0; i < chars.length; i++)
					ints[i] = chars[i];
				for(int Int: ints) {
					StringBuilder currentInt = new StringBuilder();
					int pot = 1;
					while(Int > 0) {
						currentInt.append(Int % ((int) pow(2, pot)) > 0 ? '1':'0');
						Int -= Int % (int) pow(2, pot);
						pot++;
					}
					out.append(currentInt.reverse());
					out.append(' ');
				}
				out.append("100000 ");
			}
			return out.substring(0,out.length() - 8);
		});
		addCommand("tidybinary", args -> {
			StringBuilder sb = new StringBuilder();
			String calculated = this.commands.get("asciitobin").execute(args);
			for(String s: calculated.split(" ")) {
				sb.append(String.format("%08d ", Integer.parseInt(s)));
			}
			return sb.toString();
		});
		addCommand("surrogate", args -> MessageComponents.calculateSurrogate(args[0]));
		addCommand("smashstars", args -> {
			BotInstance.botInstance.bank.eraseStars();
			return "Success";
		});
		new Thread(this::update).start();
	}
	
	void addCommand(String command, codeContainer code) {
		commands.put(command, code);
	}
	
	void update() {
		while(true) {
			try {
				String[] args = reader.readLine().split("[ \n]");
				if(args.length > 1 && commands.containsKey(args[0])) System.out.println(commands.get(args[0]).execute(Arrays.copyOfRange(args, 1, args.length)));
				else if(commands.containsKey(args[0])) System.out.println(commands.get(args[0]).execute());
				else System.out.println("Command not found.");
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	interface codeContainer {
		
		String execute(String... args);
		
	}
	
}
