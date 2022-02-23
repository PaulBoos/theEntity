package googledocs.legis.conditions;

import googledocs.legis.exceptions.ASTEvalException;
import googledocs.legis.exceptions.AccessorExceptions;
import googledocs.legis.results.*;
import lombok.AllArgsConstructor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
public class AccessorNode implements ASTNode {
	
	private ASTNode astNode;
	private String accessor;
	
	@Override
	public Result eval(Map<String, Result> variableMap) {
		Result result = astNode.eval(variableMap);
		if (result instanceof GroupResult groupResult) {
			Set<Result> resultSet = new HashSet<>();
			groupResult.getResultSet().forEach(current -> {
				Result element = internalEval(current);
				if (element instanceof GroupResult innerGroup) {
					resultSet.addAll(innerGroup.getResultSet());
				} else {
					resultSet.add(element);
				}
			});
			return new GroupResult(resultSet);
		}
		return internalEval(result);
	}
	
	private Result internalEval(Result result) {
		if(result instanceof TribeResult tribeResult) {
			switch(accessor) {
				case "cities" -> {
					return new GroupResult(tribeResult.getTribe().getCities().stream().map(CityResult::new).collect(Collectors.toSet()));
				}
				case "fortresses" -> {
					return new GroupResult(tribeResult.getTribe().getFortresses().stream().map(CityResult::new).collect(Collectors.toSet()));
				}
				case "nobles" -> {
					return new GroupResult(tribeResult.getTribe().getNobles().stream().map(NobleResult::new).collect(Collectors.toSet()));
				}
			}
			throw new ASTEvalException(AccessorExceptions.INVALID_FIELD, astNode.source(), result.getClass().getSimpleName(), accessor);
		} else if(result instanceof CityResult cityResult) {
			switch(accessor) {
				case "nobles" -> {
					return new GroupResult(cityResult.getCity().getNobles().stream().map(NobleResult::new).collect(Collectors.toSet()));
				}
				case "areas" -> {
					return new GroupResult(cityResult.getCity().getArea());
				}
			}
			throw new ASTEvalException(AccessorExceptions.INVALID_FIELD, astNode.source(), result.getClass().getSimpleName(), accessor);
		} else if(result instanceof NobleResult nobleResult) {
			switch(accessor) {
				case "location" -> {
					return nobleResult.getLocation();
				}
			}
			throw new ASTEvalException(AccessorExceptions.INVALID_FIELD, astNode.source(), result.getClass().getSimpleName(), accessor);
		} else if (result instanceof BillResult billResult) {
			switch(accessor) {
				case "tribe" -> {
					return new TribeResult(billResult.getValue().getTribe());
				}
			}
			throw new ASTEvalException(AccessorExceptions.INVALID_FIELD, astNode.source(), result.getClass().getSimpleName(), accessor);
		}
		throw new ASTEvalException(AccessorExceptions.INVALID_TYPE, astNode.source(), result.getClass().getSimpleName());
	}
	
	@Override
	public ResultType metaEval(Map<String, ResultType> variableTypeMap) {
		ResultType resultType = astNode.metaEval(variableTypeMap);
		if (resultType.isGroup()) {
			resultType = resultType.unwrap();
		}
		if (resultType == ResultType.TRIBE) {
			switch(accessor) {
				case "cities" -> {
					return ResultType.CITY_GROUP;
				}
				case "fortresses" -> {
					return ResultType.FORTRESS_GROUP;
				}
				case "nobles" -> {
					return ResultType.NOBLE_GROUP;
				}
			}
			throw new ASTEvalException(AccessorExceptions.META_EVAL_INVALID_FIELD, astNode.source(), resultType.name(), accessor);
		} else if (resultType == ResultType.CITY) {
			switch(accessor) {
				case "nobles" -> {
					return ResultType.NOBLE_GROUP;
				}
				case "areas" -> {
					return ResultType.LOCATION_GROUP;
				}
			}
			throw new ASTEvalException(AccessorExceptions.META_EVAL_INVALID_FIELD, astNode.source(), resultType.name(), accessor);
		}  else if(resultType == ResultType.NOBLE) {
			switch(accessor) {
				case "location" -> {
					return ResultType.LOCATION;
				}
			}
			throw new ASTEvalException(AccessorExceptions.META_EVAL_INVALID_FIELD, astNode.source(), resultType.name(), accessor);
		} else if (resultType == ResultType.BILL) {
			switch(accessor) {
				case "tribe" -> {
					return ResultType.TRIBE;
				}
			}
			throw new ASTEvalException(AccessorExceptions.META_EVAL_INVALID_FIELD, astNode.source(), resultType.name(), accessor);
		}
		throw new ASTEvalException(AccessorExceptions.META_EVAL_INVALID_TYPE, astNode.source(), resultType.name());
	}
	
	@Override
	public String source() {
		return "(" + astNode.source() + "." + accessor + ")";
	}
}
