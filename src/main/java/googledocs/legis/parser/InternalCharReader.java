package googledocs.legis.parser;

import java.util.LinkedList;
import java.util.List;

class InternalCharReader {
	
	private final CharReader charReader;
	private final List<Character> characterList = new LinkedList<>();
	
	InternalCharReader(CharReader charReader) {
		this.charReader = charReader;
	}
	
	public char next() {
		if (characterList.isEmpty()) {
			return charReader.next();
		}
		return characterList.remove(characterList.size() - 1);
	}
	
	public void unread(char c) {
		characterList.add(c);
	}
	
	public boolean hasNext() {
		if (characterList.isEmpty()) return charReader.hasNext();
		return true;
	}
}
