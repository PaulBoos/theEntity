package googledocs.legis.results;

import googledocs.legis.Bill;
import lombok.Data;

import java.util.Map;

@Data
public class BillResult implements Result {
	
	private final Bill value;
	
	@Override
	public String source() {
		return value + "";
	}
	
	@Override
	public ResultType metaEval(Map<String, ResultType> variableTypeMap) {
		return ResultType.BILL;
	}
}
