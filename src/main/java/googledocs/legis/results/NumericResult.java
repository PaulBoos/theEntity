package googledocs.legis.results;

import lombok.Data;

import java.util.Map;

@Data
public class NumericResult implements Result {
	
	private final double numeric;
	
	@Override
	public String source() {
		return numeric + "";
	}
	
	@Override
	public ResultType metaEval(Map<String, ResultType> variableTypeMap) {
		return ResultType.NUMERIC;
	}
}
