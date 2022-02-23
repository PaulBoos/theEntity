package googledocs.legis.parser;

public enum TokenType {
	UNKNOWN,
	
	OPERATOR,
	LOGIC,
	COMPARATOR,
	AT,
	
	SEPARATOR,
	
	BRACKET,
	
	TEXT,
	STRING,
	WHITESPACE,
	
	NUMERIC,
}
