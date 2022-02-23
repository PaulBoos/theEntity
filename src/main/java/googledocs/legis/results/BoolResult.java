package googledocs.legis.results;

import lombok.Data;

import java.util.Map;

@Data
public class BoolResult implements Result {
	
	private final boolean value;
	
	@Override
	public String source() {
		return value + "";
	}
	
	@Override
	public ResultType metaEval(Map<String, ResultType> variableTypeMap) {
		return ResultType.BOOL;
	}
}
