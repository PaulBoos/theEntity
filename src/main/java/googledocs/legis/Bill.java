package googledocs.legis;

import core.Tribe;
import googledocs.legis.conditions.ASTNode;

public class Bill {
	
	private Tribe tribe;
	private Trigger trigger;
	private ASTNode condition;
	private Action action;
	
	public Bill(Tribe tribe, String expression) throws ExpressionNotRecognizedException {
		String[] args = expression.split(";");
		if(args.length < 2 || args.length > 3)
			throw new ExpressionNotRecognizedException();
		if(args.length == 3) {
		
		} else if(args.length == 2) {
		
		}
	}
	
	public Tribe getTribe() {
		return tribe;
	}
	
	public static void main(String[] args) throws ExpressionNotRecognizedException {
		new Bill(Tribe.TEST, "purchase;!(seller@\"TESTTRIBE\")AND(buyer@\"TESTTRIBE\");tax(value/50)"); // IMPORT TAX EXAMPLE
	}
	
	public static class ExpressionNotRecognizedException extends Exception {}
	
}
