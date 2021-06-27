package core;

import java.io.BufferedWriter;
import java.io.FileWriter;
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
	
	public static void writeString(String fact, String value) {
		try {
			DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("."), path -> path.toFile().isFile());
			for(Path path: stream) {
				if(path.getFileName().toString().equals(factURL)) {
					BufferedWriter writer = new BufferedWriter(new FileWriter(factURL, true));
					List<String> lines = Files.readAllLines(path);
					boolean inserted = false;
					for(String s: lines) {
						if(s.startsWith(fact + ":")) {
							s = fact + ":" + value;
							inserted = true;
							break;
						}
						writer.append(s).append('\n');
					}
					if(!inserted) {
						writer.append('\n').append(fact).append(':').append(value);
					}
					writer.close();
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static boolean readTokenFile() {
		try {
			DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("data/"), path -> path.toFile().isFile());
			for(Path path: stream) {
				if(path.getFileName().toString().equals("token")) {
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
