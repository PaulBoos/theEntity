package googledocs.legis.results;

import lombok.Data;

import java.util.Map;

@Data
public class TextResult implements Result {
	
	private final String s;
	
	@Override
	public String source() {
		return "\"" + s + "\"";
	}
	
	@Override
	public ResultType metaEval(Map<String, ResultType> variableTypeMap) {
		return ResultType.TEXT;
	}
}
