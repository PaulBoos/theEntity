package googledocs.legis.conditions;

import googledocs.legis.results.Result;
import googledocs.legis.results.ResultType;

import java.util.Map;

public interface ASTNode {
	
	Result eval(Map<String, Result> variableMap);
	
	default ResultType metaEval(Map<String, ResultType> variableTypeMap) {
		return ResultType.UNKNOWN;
	}
	
	String source();
	
}
