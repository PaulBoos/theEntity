package googledocs.legis.results;

import core.WorldObject.Fortress;
import lombok.Data;

import java.util.Map;

@Data
public class FortressResult implements Result {
	
	private final Fortress value;
	
	@Override
	public String source() {
		return value + "";
	}
	
	@Override
	public ResultType metaEval(Map<String, ResultType> variableTypeMap) {
		return ResultType.FORTRESS;
	}
}
