package core;

import finance.Currency;
import net.dv8tion.jda.api.JDA;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;

public class ConsoleInteraction {
	
	BufferedReader reader;
	HashMap<String, codeContainer> commands = new HashMap<>();
	
	ConsoleInteraction() {
		reader = new BufferedReader(new InputStreamReader(System.in));
		commands.put("shutdown", args -> {
			System.out.println("Disconnecting...");
			BotInstance.botInstance.jda.shutdown();
			try {
				BotInstance.botInstance.jda.awaitStatus(JDA.Status.SHUTDOWN);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Shutting down...");
			System.exit(0);
		});
		commands.put("updateSlash", args -> {
			SlashCommands.updateGlobalCommands(BotInstance.botInstance.jda);
			System.out.println("Updating Global Slash Commands");
		});
		commands.put("cheat", args -> {
			BotInstance.botInstance.bank.credit(
					Long.parseLong(args[0]),
					Currency.getCurrency(args[1]),
					Integer.parseInt(args[2])
			);
			System.out.println("Cheat successful. New balance: " + BotInstance.botInstance.bank.getBalance(
					Long.parseLong(args[0]),
					Currency.getCurrency(args[1])
			) + " (" + Currency.getCurrency(args[1]).name + ")");
		});
		commands.put("balance", args -> {
			System.out.println("Balance is " + BotInstance.botInstance.bank.getBalance(
					Long.parseLong(args[0]),
					Currency.getCurrency(args[1])
			) + " (" + Currency.getCurrency(args[1]).name + ")");
		});
		commands.put("emotecode", args -> {
			try {
				System.out.println(BotInstance.botInstance.jda.getEmotesByName(args[0], true).get(0).getAsMention());
			} catch(Exception e) {
				System.out.println("No emote found.");
			}
		});
		new Thread(this::update).start();
	}
	
	void update() {
		while(true) {
			try {
				String[] args = reader.readLine().split(" ");
				if(args.length > 1) commands.get(args[0]).execute(Arrays.copyOfRange(args, 1, args.length));
				else commands.get(args[0]).execute();
			} catch(NullPointerException ignored) {
				System.out.println("Command not found.");
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	interface codeContainer {
		
		void execute(String... args);
		
	}
	
}
