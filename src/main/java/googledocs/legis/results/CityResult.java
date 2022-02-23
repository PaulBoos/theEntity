package googledocs.legis.results;

import core.WorldObject.City;
import lombok.Data;

import java.util.Map;

@Data
public class CityResult implements Result {
	
	private final City city;
	
	@Override
	public String source() {
		return city.toString();
	}
	
	@Override
	public ResultType metaEval(Map<String, ResultType> variableTypeMap) {
		return ResultType.CITY;
	}
}
