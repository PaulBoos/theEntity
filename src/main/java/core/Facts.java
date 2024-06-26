package core;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Facts {
	
	public static final String factURL = "data/facts.txt";
	
	public static String firstLine = "";
	
	public static int readInt(String fact) {
		return Integer.parseInt(readString(fact));
	}
	
	public static long readLong(String fact) {
		return Long.parseLong(readString(fact));
	}
	
	public static String readString(String fact) {
		try {
			DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("."), path -> path.toFile().isFile());
			for(Path p: stream) {
				if(p.toFile().getName().equals(factURL)) {
					List<String> lines = Files.readAllLines(p);
					for(String s: lines) {
						if(s.startsWith(fact.replace(":","") + ":")) {
							return s.substring(fact.length());
						}
					}
					return "";
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static boolean readTokenFile() {
		try {
			DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("tokens/"), path -> path.toFile().isFile());
			for(Path path: stream) {
				if(path.getFileName().toString().equals("BotToken")) {
					List<String> lines = Files.readAllLines(path);
					firstLine = lines.get(0);
					return true;
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
