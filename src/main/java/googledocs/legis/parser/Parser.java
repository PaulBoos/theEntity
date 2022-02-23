package googledocs.legis.parser;

import core.Tribe;
import core.WorldObject.City;
import core.WorldObject.Fortress;
import googledocs.legis.conditions.*;
import googledocs.legis.exceptions.ASTEvalException;
import googledocs.legis.exceptions.TextParseExceptions;
import googledocs.legis.results.*;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Parser {
	
	@SneakyThrows
	public static void main(String[] args) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		while(true) {
			String s = reader.readLine();
			if (s.equals("exit")) {
				return;
			}
			parse(s);
		}
	}
	
	public static void parse(String expression) {
		Parser parser = new Parser(new StringCharReader(expression));
		List<Token> tokens = new ArrayList<>();
		while(parser.hasNext()) {
			tokens.add(parser.next());
		}
		mergeStrings(tokens);
		mergeNumbers(tokens);
		System.out.println(tokens);
		List<Integer> order = new ArrayList<>();
		int current = 0;
		for(Token token: tokens) {
			switch(token.getType()) {
				case OPERATOR -> {
					switch(token.getValue()) {
						case "+", "-" -> order.add(current + 7);
						case "*", "/" -> order.add(current + 8);
					}
				}
				case LOGIC -> {
					switch(token.getValue()) {
						case "&" -> order.add(current + 4);
						case "#" -> order.add(current + 3);
						case "|" -> order.add(current + 2);
						case "!" -> order.add(current + 9);
					}
				}
				case COMPARATOR -> {
					switch(token.getValue()) {
						case "<", ">" -> order.add(current + 6);
						case "=" -> order.add(current + 5);
					}
				}
				case SEPARATOR -> order.add(current + 10);
				case AT -> order.add(current + 1);
				case BRACKET -> {
					if(token.getValue().equals("(")) current += 11;
					else if(token.getValue().equals(")")) current -= 11;
					order.add(0);
				}
				default -> order.add(0);
			}
		}
		for (int i = tokens.size() - 1; i >= 0; i--) {
			if (tokens.get(i).getType() == TokenType.BRACKET) {
				tokens.remove(i);
				order.remove(i);
			}
		}
		
		List<Object> objects = new ArrayList<>(tokens);
		while(objects.size() > 1) {
			int index = getHighestIdentity(order);
			if (index > 0) {
				Object left = objects.get(index - 1);
				Object right = objects.get(index + 1);
				Token token = (Token) objects.get(index);
				if (left instanceof Token leftToken) {
					left = parseString(leftToken);
				}
				objects.remove(index - 1);
				objects.remove(index - 1);
				order.remove(index - 1);
				order.remove(index - 1);
				order.set(index - 1, 0);
				if (token.getValue().equals(".") && right instanceof Token rightToken) {
					objects.set(index - 1, new AccessorNode((ASTNode) left, rightToken.getValue()));
				} else {
					if (right instanceof Token rightToken) {
						right = parseString(rightToken);
					}
					objects.set(index - 1, new OperatorNode((ASTNode) left, OperatorNode.Operator.getOperator(token.getValue().charAt(0)), (ASTNode) right));
				}
			} else {
				Token token = (Token) objects.get(index);
				Object other = objects.get(index + 1);
				if (other instanceof Token otherToken) {
					other = parseString(otherToken);
				}
				objects.remove(index);
				objects.set(index, new PrefixOperatorNode(PrefixOperatorNode.PrefixOperator.getOperator(token.getValue().charAt(0)), (ASTNode) other));
				order.remove(index);
				order.set(index, 0);
			}
		}
		
		Map<String, Result> resultMap = new HashMap<>();
		resultMap.put("seller", new TextResult("seller"));
		resultMap.put("buyer", new TextResult("buyer"));
		
		Map<String, ResultType> resultTypeMap = new HashMap<>();
		resultTypeMap.put("seller", ResultType.TEXT);
		resultTypeMap.put("buyer", ResultType.TEXT);
		
		ASTNode rootNode = (ASTNode) objects.get(0);
		System.out.println(rootNode.source());
		ResultType resultType = rootNode.metaEval(resultTypeMap);
		System.out.println(resultType);
		Result result = rootNode.eval(resultMap);
		System.out.println(result.source());
	}
	
	private static ASTNode parseString(Token token) {
		if (token.getType() == TokenType.NUMERIC) {
			return new NumericResult(Double.parseDouble(token.getValue()));
		}
		String s = token.getValue();
		if (token.getType() == TokenType.TEXT) {
			if (s.equals("true") || s.equals("false")) {
				return new BoolResult(s.equals("true"));
			}
		}
		if (!(s.startsWith("\"") && s.endsWith("\""))) {
			return new VariableNode(s);
		}
		s = s.substring(1, s.length() - 1);
		if (!s.matches("([NTCF]:\\d+)|(-?\\d+\\|-?\\d+)")) {
			throw new ASTEvalException(TextParseExceptions.INVALID_LEGAL_OBJECT, s);
		}
		if (s.contains("|")) {
			String[] strings = s.split("\\|");
			return new LocationResult(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]));
		} else {
			long id = Long.parseLong(s.substring(2));
			return switch(s.charAt(0)) {
				case 'N' -> new NobleResult(id);
				case 'T' -> new TribeResult(Tribe.TEST); // throw new ASTEvalException(TextParseExceptions.LEGAL_OBJECT_NOT_FOUND, s);
				case 'C' -> new CityResult(City.TESTCITY);
				case 'F' -> new FortressResult(Fortress.TESTFORT);
				default -> null;
			};
		}
	}
	
	private static int getHighestIdentity(List<Integer> identity) {
		int index = 0;
		int number = -1;
		for (int i = 0; i < identity.size(); i++) {
			if (identity.get(i) > number) {
				number = identity.get(i);
				index = i;
			}
		}
		return index;
	}
	
	private static final List<TokenDefinition> tokenDefinitions = new ArrayList<>();
	
	static {
		tokenDefinitions.add(TokenDefinition.separate(TokenDefinition.anyOf(TokenType.OPERATOR, '+', '-', '*', '/')));
		tokenDefinitions.add(TokenDefinition.separate(TokenDefinition.anyOf(TokenType.COMPARATOR, '<', '>', '=')));
		tokenDefinitions.add(TokenDefinition.separate(TokenDefinition.anyOf(TokenType.LOGIC, '&', '|', '#', '!')));
		tokenDefinitions.add(TokenDefinition.separate(TokenDefinition.anyOf(TokenType.AT, '@')));
		tokenDefinitions.add(TokenDefinition.separate(TokenDefinition.anyOf(TokenType.SEPARATOR, '.')));
		tokenDefinitions.add(TokenDefinition.separate(TokenDefinition.anyOf(TokenType.BRACKET, '(', ')')));
		tokenDefinitions.add(TokenDefinition.group(TokenType.TEXT,
				TokenDefinition.fromTo(TokenType.TEXT, 'a', 'z'),
				TokenDefinition.fromTo(TokenType.TEXT, 'A', 'Z'),
				TokenDefinition.fromTo(TokenType.TEXT, '0', '9')
				));
		tokenDefinitions.add(TokenDefinition.anyOf(TokenType.WHITESPACE, ' ', '\t'));
		tokenDefinitions.add(TokenDefinition.anyOf(TokenType.STRING, '"', '\''));
	}
	
	private static void mergeStrings(List<Token> tokens) {
		int i = 0;
		outer:
		while(i < tokens.size()) {
			Token currentToken = tokens.get(i);
			if(!(currentToken.getType() == TokenType.STRING && (currentToken.getValue().equals("\"") || currentToken.getValue().equals("'")))) {
				i++;
				continue;
			}
			int current = 1;
			List<Token> currentTokens = new ArrayList<>();
			currentTokens.add(currentToken);
			while(current < tokens.size()) {
				if(i + current >= tokens.size()) {
					i++;
					continue outer;
				}
				currentTokens.add(tokens.get(i + current));
				Token currentTokenValue = tokens.get(i + current);
				if(currentTokenValue.getType() == TokenType.STRING && (currentTokenValue.getValue().equals("\"") || currentTokenValue.getValue().equals("'"))) {
					break;
				}
				current++;
			}
			String value = currentTokens.stream().map(Token::getValue).collect(Collectors.joining());
			for(int j = i; j <= i + current; j++) {
				tokens.remove(i);
			}
			tokens.add(i, new Token(TokenType.STRING, value));
			i++;
		}
	}
	
	private static void mergeNumbers(List<Token> tokens) {
		int i = 0;
		while(i < tokens.size()) {
			Token currentToken = tokens.get(i);
			if (currentToken.getValue().matches("\\d+")) {
				Token last = new Token(TokenType.UNKNOWN, "");
				boolean hasLast = false;
				if (i > 0) {
					last = tokens.get(i - 1);
					hasLast = true;
					if (!(last.getType() == TokenType.OPERATOR && last.getValue().equals("-"))) {
						last = new Token(TokenType.UNKNOWN, "");
						hasLast = false;
					} else if (i > 1) {
						Token secondToLast = tokens.get(i - 2);
						if (secondToLast.getType() != TokenType.OPERATOR) {
							last = new Token(TokenType.UNKNOWN, "");
							hasLast = false;
						}
					}
				}
				if (i + 1 < tokens.size()) {
					Token nextToken = tokens.get(i + 1);
					if (nextToken.getType() == TokenType.SEPARATOR && nextToken.getValue().equals(".")) {
						if (i + 2  < tokens.size()) {
							Token nextNextToken = tokens.get(i + 2);
							if (nextNextToken.getValue().matches("\\d+")) {
								tokens.remove(i - (hasLast ? 1 : 0));
								tokens.remove(i - (hasLast ? 1 : 0));
								if (hasLast) {
									tokens.remove(i - 1);
								}
								tokens.set(i - (hasLast ? 1 : 0), new Token(TokenType.NUMERIC, last.getValue() + currentToken.getValue() + nextToken.getValue() + nextNextToken.getValue()));
								continue;
							}
						}
					}
				}
				if (hasLast) {
					tokens.remove(i - 1);
				}
				tokens.set(i - (hasLast ? 1 : 0), new Token(TokenType.NUMERIC, last.getValue() + currentToken.getValue()));
			}
			i++;
		}
	}
	
	private StringBuilder currentValue = new StringBuilder();
	private final InternalCharReader charReader;
	
	public Parser(CharReader charReader) {
		this.charReader = new InternalCharReader(charReader);
	}
	
	public Token next() {
		if(!charReader.hasNext()) throw new SecurityException("End of Reader");
		char currentChar = charReader.next();
		
		TokenDefinition tokenDefinition = null;
		for(TokenDefinition test: tokenDefinitions) {
			if(test.isIncluded(0, currentChar)) {
				tokenDefinition = test;
				break;
			}
		}
		if(tokenDefinition == null) return new Token(TokenType.UNKNOWN, currentChar + "");
		
		if(tokenDefinition.separate()) {
			currentValue = new StringBuilder();
			return new Token(tokenDefinition.tokenName(), currentChar + "");
		}
		
		currentValue.append(currentChar);
		while(charReader.hasNext()) {
			currentChar = charReader.next();
			if(!tokenDefinition.isIncluded(currentValue.length(), currentChar)) {
				charReader.unread(currentChar);
				String value = currentValue.toString();
				currentValue = new StringBuilder();
				return new Token(tokenDefinition.tokenName(), value);
			}
			currentValue.append(currentChar);
		}
		return new Token(tokenDefinition.tokenName(), currentValue.toString());
	}
	
	public boolean hasNext() {
		return charReader.hasNext();
	}
}
