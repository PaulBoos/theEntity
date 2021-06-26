package core;

import org.apache.commons.lang3.ObjectUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;

public class ConsoleInteraction {
	
	BufferedReader reader;
	HashMap<String, codeContainer> commands = new HashMap<>();
	
	ConsoleInteraction() {
		reader = new BufferedReader(new InputStreamReader(System.in));
		commands.put("test", args -> System.out.println("test"));
		new Thread(this::update).start();
	}
	
	void update() {
		while(true) {
			try {
				String[] args = reader.readLine().split(" ");
				if(args.length > 1) commands.get(args[0]).execute(Arrays.copyOfRange(args, 1, args.length-1));
				else commands.get(args[0]).execute();
			} catch(IOException e) {
				e.printStackTrace();
			} catch(NullPointerException ignored) {
				System.out.println("Command not found.");
			}
		}
	}
	
	interface codeContainer {
		
		void execute(String... args);
		
	}
	
}
