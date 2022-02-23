package googledocs.legis.results;

import core.Tribe;
import lombok.Data;

import java.util.Map;

@Data
public class TribeResult implements Result {
	
	private final Tribe tribe;
	
	@Override
	public String source() {
		return tribe.name();
	}
	
	@Override
	public ResultType metaEval(Map<String, ResultType> variableTypeMap) {
		return ResultType.TRIBE;
	}
}
