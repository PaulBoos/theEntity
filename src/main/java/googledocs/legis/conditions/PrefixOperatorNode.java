package googledocs.legis.conditions;

import googledocs.legis.exceptions.ASTEvalException;
import googledocs.legis.exceptions.PrefixOperatorExceptions;
import googledocs.legis.results.BoolResult;
import googledocs.legis.results.NumericResult;
import googledocs.legis.results.Result;
import googledocs.legis.results.ResultType;
import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.Map;

@AllArgsConstructor
public class PrefixOperatorNode implements ASTNode {
	
	private PrefixOperator prefixOperator;
	private ASTNode astNode;
	
	@Override
	public Result eval(Map<String, Result> variableMap) {
		Result result = astNode.eval(variableMap);
		switch(prefixOperator) {
			case NOT -> {
				if (!(result instanceof BoolResult boolResult)) {
					throw new ASTEvalException(PrefixOperatorExceptions.INVALID_TYPE_FOR_NOT, astNode.source());
				}
				return new BoolResult(!boolResult.isValue());
			}
			case NEGATE -> {
				if(!(result instanceof NumericResult numericResult)) {
					throw new ASTEvalException(PrefixOperatorExceptions.INVALID_TYPE_FOR_NEGATE, astNode.source());
				}
				return new NumericResult(-numericResult.getNumeric());
			}
		}
		throw new SecurityException();
	}
	
	@Override
	public ResultType metaEval(Map<String, ResultType> variableTypeMap) {
		ResultType resultType = astNode.metaEval(variableTypeMap);
		switch(prefixOperator) {
			case NOT -> {
				if (resultType == ResultType.BOOL) {
					return ResultType.BOOL;
				}
				throw new ASTEvalException(PrefixOperatorExceptions.META_EVAL_INVALID_TYPE_FOR_NOT, astNode.source());
			}
			case NEGATE -> {
				if (resultType == ResultType.NUMERIC) {
					return ResultType.NUMERIC;
				}
				throw new ASTEvalException(PrefixOperatorExceptions.META_EVAL_INVALID_TYPE_FOR_NEGATE, astNode.source());
			}
		}
		throw new SecurityException();
	}
	
	@Override
	public String source() {
		return "(" + prefixOperator.operatorChar + astNode.source() + ")";
	}
	
	@AllArgsConstructor
	public enum PrefixOperator {
		NOT('!'),
		NEGATE('-');
		
		public static PrefixOperator getOperator(char c) {
			return Arrays.stream(PrefixOperator.values()).filter(current -> current.operatorChar == c).findFirst().orElse(null);
		}
		
		private char operatorChar;
	}
}
