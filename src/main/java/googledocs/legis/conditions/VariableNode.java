package googledocs.legis.conditions;

import googledocs.legis.exceptions.ASTEvalException;
import googledocs.legis.exceptions.TextParseExceptions;
import googledocs.legis.results.Result;
import googledocs.legis.results.ResultType;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class VariableNode implements ASTNode {
	
	private String variable;
	
	@Override
	public Result eval(Map<String, Result> variableMap) {
		if (!variableMap.containsKey(variable)) {
			throw new ASTEvalException(TextParseExceptions.INVALID_VARIABLE, variable);
		}
		return variableMap.get(variable);
	}
	
	@Override
	public ResultType metaEval(Map<String, ResultType> variableTypeMap) {
		if (!variableTypeMap.containsKey(variable)) {
			return ResultType.UNKNOWN;
		}
		return variableTypeMap.get(variable);
	}
	
	@Override
	public String source() {
		return variable;
	}
}
