package googledocs.legis.parser;

public interface TokenDefinition {
	TokenType tokenName();
	boolean isIncluded(int length, char c);
	default boolean separate() {
		return false;
	}
	
	static TokenDefinition separate(TokenDefinition tokenDefinition) {
		return new TokenDefinition() {
			@Override
			public TokenType tokenName() {
				return tokenDefinition.tokenName();
			}
			
			@Override
			public boolean isIncluded(int length, char c) {
				return tokenDefinition.isIncluded(length, c);
			}
			
			@Override
			public boolean separate() {
				return true;
			}
		};
	}
	
	static TokenDefinition group(TokenType tokenType, TokenDefinition... tokenDefinitions) {
		return new TokenDefinition() {
			@Override
			public TokenType tokenName() {
				return tokenType;
			}
			
			@Override
			public boolean isIncluded(int length, char c) {
				for (TokenDefinition tokenDefinition: tokenDefinitions) {
					if (tokenDefinition.isIncluded(length, c)) {
						return true;
					}
				}
				return false;
			}
		};
	}
	
	static TokenDefinition fromTo(TokenType tokenType, char lowerBound, char upperBound) {
		return new TokenDefinition() {
			@Override
			public TokenType tokenName() {
				return tokenType;
			}
			
			@Override
			public boolean isIncluded(int length, char c) {
				return c >= lowerBound && c <= upperBound;
			}
		};
	}
	
	static TokenDefinition anyOf(TokenType tokenType, char... chars) {
		return new TokenDefinition() {
			@Override
			public TokenType tokenName() {
				return tokenType;
			}
			
			@Override
			public boolean isIncluded(int length, char c) {
				for(char current : chars) {
					if (current == c) {
						return true;
					}
				}
				return false;
			}
		};
	}
}
