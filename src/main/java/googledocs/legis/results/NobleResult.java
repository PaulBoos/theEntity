package googledocs.legis.results;

import lombok.Data;

import java.util.Map;

@Data
public class NobleResult implements Result {
	
	private final long userID;
	
	@Override
	public String source() {
		return String.valueOf(userID);
	}
	
	public LocationResult getLocation() {
		return new LocationResult(0,0);
	}
	
	@Override
	public ResultType metaEval(Map<String, ResultType> variableTypeMap) {
		return ResultType.NOBLE;
	}
}
