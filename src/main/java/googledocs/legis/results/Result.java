package googledocs.legis.results;

import googledocs.legis.conditions.ASTNode;

import java.util.Map;

public interface Result extends ASTNode {
	
	@Override
	default Result eval(Map<String, Result> variableMap) {
		return this;
	}
	
}
