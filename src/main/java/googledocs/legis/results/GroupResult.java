package googledocs.legis.results;

import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
public class GroupResult implements Result {
	
	private final Set<Result> resultSet;
	
	@SuppressWarnings("unchecked")
	public <T extends Result> GroupResult(Set<T> resultSet) {
		this.resultSet = (Set<Result>) resultSet;
	}
	
	public Class<?> getType() {
		if (resultSet.isEmpty()) {
			return Result.class;
		}
		return resultSet.iterator().next().getClass();
	}
	
	@Override
	public String source() {
		return resultSet.toString();
	}
	
	@Override
	public ResultType metaEval(Map<String, ResultType> variableTypeMap) {
		if (resultSet.isEmpty()) {
			return ResultType.UNKNOWN;
		}
		return resultSet.iterator().next().metaEval(variableTypeMap).wrapIntoGroup();
	}
}
