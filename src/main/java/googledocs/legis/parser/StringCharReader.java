package googledocs.legis.parser;

public class StringCharReader implements CharReader {
	
	private final String s;
	private int index = 0;
	
	public StringCharReader(String s) {
		this.s = s;
	}
	
	@Override
	public char next() {
		return s.charAt(index++);
	}
	
	@Override
	public boolean hasNext() {
		return s.length() > index;
	}
}
