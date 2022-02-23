package googledocs.legis.conditions;

import googledocs.legis.exceptions.ASTEvalException;
import googledocs.legis.exceptions.OperatorExceptions;
import googledocs.legis.results.*;
import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
public class OperatorNode implements ASTNode {
	
	private ASTNode left;
	private Operator operator;
	private ASTNode right;
	
	@Override
	public Result eval(Map<String, Result> variableMap) {
		Result leftResult = left.eval(variableMap);
		Result rightResult = right.eval(variableMap);
		switch(operator) {
			case DIVIDE -> {
				if(!(leftResult instanceof NumericResult leftNumeric) || !(rightResult instanceof NumericResult rightNumeric)) {
					throw new ASTEvalException(OperatorExceptions.INVALID_TYPE_FOR_DIVIDE, source());
				}
				return new NumericResult(leftNumeric.getNumeric() / rightNumeric.getNumeric());
			}
			case MULTIPLY -> {
				if(leftResult instanceof NumericResult leftNumeric && rightResult instanceof NumericResult rightNumeric) {
					return new NumericResult(leftNumeric.getNumeric() * rightNumeric.getNumeric());
				} else if(leftResult instanceof NumericResult left && rightResult instanceof BoolResult right) {
					return new NumericResult(right.isValue() ? left.getNumeric() : 0);
				} else if(leftResult instanceof BoolResult left && rightResult instanceof NumericResult right) {
					return new NumericResult(left.isValue() ? right.getNumeric() : 0);
				} else {
					throw new ASTEvalException(OperatorExceptions.INVALID_TYPE_FOR_MULTIPLY, source());
				}
			}
			case ADD_CONCAT -> {
				// ADD
				if(leftResult instanceof NumericResult leftNumeric && rightResult instanceof NumericResult rightNumeric) {
					return new NumericResult(leftNumeric.getNumeric() + rightNumeric.getNumeric());
				} else if(leftResult instanceof NumericResult leftNumeric && rightResult instanceof BoolResult rightBool) {
					return new NumericResult(leftNumeric.getNumeric() + (rightBool.isValue() ? 1 : 0));
				} else if(leftResult instanceof BoolResult leftBool && rightResult instanceof NumericResult rightNumeric) {
					return new NumericResult(rightNumeric.getNumeric() + (leftBool.isValue() ? 1 : 0));
				} else if(leftResult instanceof BoolResult leftBool && rightResult instanceof BoolResult rightBool) {
					return new NumericResult((rightBool.isValue() ? 1 : 0) + (leftBool.isValue() ? 1 : 0));
				}
				
				// Concatenation
				if(leftResult instanceof LocationResult || leftResult instanceof NobleResult || leftResult instanceof TribeResult || leftResult instanceof CityResult) {
					if(leftResult.getClass().isInstance(rightResult)) { // Check if left and right are equal types
						Set<Result> results = new HashSet<>();
						results.add(leftResult);
						results.add(rightResult);
						return new GroupResult(results);
					} else if(rightResult instanceof GroupResult group && group.getType().isInstance(leftResult)) {
						group.getResultSet().add(leftResult);
						return group;
					} else {
						throw new ASTEvalException(OperatorExceptions.INVALID_TYPE_FOR_ADDITION_CONCAT, source());
					}
				} else if(rightResult instanceof LocationResult || rightResult instanceof NobleResult || rightResult instanceof TribeResult || rightResult instanceof CityResult) {
					if(leftResult instanceof GroupResult group && group.getType().isInstance(rightResult)) {
						group.getResultSet().add(leftResult);
						return group;
					} else {
						throw new ASTEvalException(OperatorExceptions.INVALID_TYPE_FOR_ADDITION_CONCAT, source());
					}
				}
				throw new ASTEvalException(OperatorExceptions.INVALID_TYPE_FOR_ADDITION_CONCAT, source());
			}
			case SUBTRACT -> {
				if(leftResult instanceof NumericResult leftNumeric && rightResult instanceof NumericResult rightNumeric) {
					return new NumericResult(leftNumeric.getNumeric() - rightNumeric.getNumeric());
				} else if(leftResult instanceof NumericResult leftNumeric && rightResult instanceof BoolResult rightBool) {
					return new NumericResult(leftNumeric.getNumeric() - (rightBool.isValue() ? 1 : 0));
				} else if(leftResult instanceof BoolResult leftBool && rightResult instanceof NumericResult rightNumeric) {
					return new NumericResult(rightNumeric.getNumeric() - (leftBool.isValue() ? 1 : 0));
				}
				throw new ASTEvalException(OperatorExceptions.INVALID_TYPE_FOR_SUBTRACTION, source());
			}
			
			case LESS -> {
				if(leftResult instanceof NumericResult leftNumeric && rightResult instanceof NumericResult rightNumeric) {
					return new BoolResult(leftNumeric.getNumeric() < rightNumeric.getNumeric());
				}
				throw new ASTEvalException(OperatorExceptions.INVALID_TYPE_FOR_LESS, source());
			}
			case GREATER -> {
				if(leftResult instanceof NumericResult leftNumeric && rightResult instanceof NumericResult rightNumeric) {
					return new BoolResult(leftNumeric.getNumeric() > rightNumeric.getNumeric());
				}
				throw new ASTEvalException(OperatorExceptions.INVALID_TYPE_FOR_GREATER, source());
			}
			case EQUALS -> {
				if(leftResult.getClass().isInstance(rightResult)) {
					return new BoolResult(leftResult.equals(rightResult));
				}
				throw new ASTEvalException(OperatorExceptions.INVALID_TYPE_FOR_EQUALS, source());
			}
			
			case AND -> {
				if(!(leftResult instanceof BoolResult leftBoolean) || !(rightResult instanceof BoolResult rightBoolean)) {
					throw new ASTEvalException(OperatorExceptions.INVALID_TYPE_FOR_AND, source());
				}
				return new BoolResult(leftBoolean.isValue() && rightBoolean.isValue());
			}
			case XOR -> {
				if(!(leftResult instanceof BoolResult leftBoolean) || !(rightResult instanceof BoolResult rightBoolean)) {
					throw new ASTEvalException(OperatorExceptions.INVALID_TYPE_FOR_XOR, source());
				}
				return new BoolResult(leftBoolean.isValue() ^ rightBoolean.isValue());
			}
			case OR -> {
				if(!(leftResult instanceof BoolResult leftBoolean) || !(rightResult instanceof BoolResult rightBoolean)) {
					throw new ASTEvalException(OperatorExceptions.INVALID_TYPE_FOR_OR, source());
				}
				return new BoolResult(leftBoolean.isValue() || rightBoolean.isValue());
			}
			
			case AT -> {
				if(!(rightResult instanceof GroupResult groupResult)) {
					throw new ASTEvalException(OperatorExceptions.INVALID_TYPE_FOR_AT, source());
				}
				if(!groupResult.getType().isInstance(leftResult)) {
					throw new ASTEvalException(OperatorExceptions.INVALID_TYPE_FOR_AT, source());
				}
				return new BoolResult(groupResult.getResultSet().contains(leftResult));
			}
		}
		throw new SecurityException();
	}
	
	@Override
	public ResultType metaEval(Map<String, ResultType> variableTypeMap) {
		ResultType leftResultType = left.metaEval(variableTypeMap);
		ResultType rightResultType = right.metaEval(variableTypeMap);
		switch(operator) {
			case DIVIDE -> {
				if(leftResultType == ResultType.NUMERIC && rightResultType == ResultType.NUMERIC) {
					return ResultType.NUMERIC;
				}
				throw new ASTEvalException(OperatorExceptions.META_EVAL_INVALID_TYPE_FOR_DIVIDE, source());
			}
			case MULTIPLY -> {
				if(leftResultType == ResultType.NUMERIC && rightResultType == ResultType.NUMERIC) {
					return ResultType.NUMERIC;
				} else if(leftResultType == ResultType.NUMERIC && rightResultType == ResultType.BOOL) {
					return ResultType.NUMERIC;
				} else if(leftResultType == ResultType.BOOL && rightResultType == ResultType.NUMERIC) {
					return ResultType.NUMERIC;
				} else {
					throw new ASTEvalException(OperatorExceptions.META_EVAL_INVALID_TYPE_FOR_MULTIPLY, source());
				}
			}
			case ADD_CONCAT -> {
				if(leftResultType == ResultType.NUMERIC && rightResultType == ResultType.NUMERIC) {
					return ResultType.NUMERIC;
				} else if(leftResultType == ResultType.NUMERIC && rightResultType == ResultType.BOOL) {
					return ResultType.NUMERIC;
				} else if(leftResultType == ResultType.BOOL && rightResultType == ResultType.NUMERIC) {
					return ResultType.NUMERIC;
				} else if(leftResultType == ResultType.BOOL && rightResultType == ResultType.BOOL) {
					return ResultType.NUMERIC;
				}
				if(leftResultType == ResultType.LOCATION || leftResultType == ResultType.NOBLE || leftResultType == ResultType.TRIBE || leftResultType == ResultType.CITY) {
					if(leftResultType == rightResultType) {
						return leftResultType.wrapIntoGroup();
					}
					if(leftResultType.wrapIntoGroup() == rightResultType) {
						return leftResultType;
					}
				} else if(rightResultType == ResultType.LOCATION || rightResultType == ResultType.NOBLE || rightResultType == ResultType.TRIBE || rightResultType == ResultType.CITY) {
					if (leftResultType.isGroup() && leftResultType == rightResultType.wrapIntoGroup()) {
						return leftResultType;
					}
				}
				throw new ASTEvalException(OperatorExceptions.META_EVAL_INVALID_TYPE_FOR_ADDITION_CONCAT, source());
			}
			case SUBTRACT -> {
				if (leftResultType == ResultType.NUMERIC && rightResultType == ResultType.NUMERIC) {
					return ResultType.NUMERIC;
				} else if (leftResultType == ResultType.NUMERIC && rightResultType == ResultType.BOOL) {
					return ResultType.NUMERIC;
				} else if (leftResultType == ResultType.BOOL && rightResultType == ResultType.NUMERIC) {
					return ResultType.NUMERIC;
				}
				throw new ASTEvalException(OperatorExceptions.META_EVAL_INVALID_TYPE_FOR_SUBTRACTION, source());
			}
			
			case LESS -> {
				if (leftResultType == ResultType.NUMERIC && rightResultType == ResultType.NUMERIC) {
					return ResultType.BOOL;
				}
				throw new ASTEvalException(OperatorExceptions.META_EVAL_INVALID_TYPE_FOR_LESS, source());
			}
			case GREATER -> {
				if (leftResultType == ResultType.NUMERIC && rightResultType == ResultType.NUMERIC) {
					return ResultType.BOOL;
				}
				throw new ASTEvalException(OperatorExceptions.META_EVAL_INVALID_TYPE_FOR_GREATER, source());
			}
			case EQUALS -> {
				if (leftResultType == rightResultType) {
					return ResultType.BOOL;
				}
				throw new ASTEvalException(OperatorExceptions.META_EVAL_INVALID_TYPE_FOR_EQUALS, source());
			}
			
			case AND -> {
				if (leftResultType == ResultType.BOOL && rightResultType == ResultType.BOOL) {
					return ResultType.BOOL;
				}
				throw new ASTEvalException(OperatorExceptions.META_EVAL_INVALID_TYPE_FOR_AND, source());
			}
			case XOR -> {
				if (leftResultType == ResultType.BOOL && rightResultType == ResultType.BOOL) {
					return ResultType.BOOL;
				}
				throw new ASTEvalException(OperatorExceptions.META_EVAL_INVALID_TYPE_FOR_XOR, source());
			}
			case OR -> {
				if (leftResultType == ResultType.BOOL && rightResultType == ResultType.BOOL) {
					return ResultType.BOOL;
				}
				throw new ASTEvalException(OperatorExceptions.META_EVAL_INVALID_TYPE_FOR_OR, source());
			}
			
			case AT -> {
				if (rightResultType.isGroup() && rightResultType == leftResultType.wrapIntoGroup()) {
					return ResultType.BOOL;
				}
				throw new ASTEvalException(OperatorExceptions.META_EVAL_INVALID_TYPE_FOR_AT, source());
			}
		}
		throw new SecurityException();
	}
	
	@Override
	public String source() {
		return "(" + left.source() + " " + operator.operatorChar + " " + right.source() + ")";
	}
	
	@AllArgsConstructor
	public enum Operator {
		DIVIDE('/'),
		MULTIPLY('*'),
		ADD_CONCAT('+'),
		SUBTRACT('-'),
		
		LESS('<'),
		GREATER('>'),
		EQUALS('='),
		
		AND('&'),
		XOR('#'),
		OR('|'),
		
		AT('@');
		
		private char operatorChar;
		
		public static Operator getOperator(char c) {
			return Arrays.stream(Operator.values()).filter(current -> current.operatorChar == c).findFirst().orElse(null);
		}
	}
}
