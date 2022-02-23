package googledocs.legis.results;

import lombok.Data;

import java.util.Map;

@Data
public class LocationResult implements Result {
	
	private final int x, y;
	
	@Override
	public String source() {
		return "\"" + x + "|" + y + "\"";
	}
	
	@Override
	public ResultType metaEval(Map<String, ResultType> variableTypeMap) {
		return ResultType.LOCATION;
	}
}
