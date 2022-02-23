package googledocs.legis.results;

import googledocs.legis.exceptions.ASTEvalException;
import googledocs.legis.exceptions.TextParseExceptions;

public enum ResultType {
	UNKNOWN,
	
	BILL,
	BILL_GROUP,
	BOOL,
	BOOL_GROUP,
	CITY,
	CITY_GROUP,
	FORTRESS,
	FORTRESS_GROUP,
	LOCATION,
	LOCATION_GROUP,
	NOBLE,
	NOBLE_GROUP,
	NUMERIC,
	NUMERIC_GROUP,
	TEXT,
	TEXT_GROUP,
	TRIBE,
	TRIBE_GROUP;
	
	public ResultType unwrap() {
		return switch(this) {
			case BILL_GROUP -> BILL;
			case BOOL_GROUP -> BOOL;
			case CITY_GROUP -> CITY;
			case FORTRESS_GROUP -> FORTRESS;
			case LOCATION_GROUP -> LOCATION;
			case NOBLE_GROUP -> NOBLE;
			case NUMERIC_GROUP -> NUMERIC;
			case TEXT_GROUP -> TEXT;
			case TRIBE_GROUP -> TRIBE;
			default -> {
				throw new ASTEvalException(TextParseExceptions.META_EVAL_ILLEGAL_GROUP, this.name());
			}
		};
	}
	
	public ResultType wrapIntoGroup() {
		return switch(this) {
			case BILL -> BILL_GROUP;
			case BOOL -> BOOL_GROUP;
			case CITY -> CITY_GROUP;
			case FORTRESS -> FORTRESS_GROUP;
			case LOCATION -> LOCATION_GROUP;
			case NOBLE -> NOBLE_GROUP;
			case NUMERIC -> NUMERIC_GROUP;
			case TEXT -> TEXT_GROUP;
			case TRIBE -> TRIBE_GROUP;
			default -> {
				throw new ASTEvalException(TextParseExceptions.META_EVAL_ILLEGAL_GROUP, this.name());
			}
		};
	}
	
	public boolean isNotGroup() {
		if(this == UNKNOWN) return false;
		return !isGroup();
	}
	
	public boolean isGroup() {
		return switch(this) {
			case BILL_GROUP, BOOL_GROUP, CITY_GROUP, FORTRESS_GROUP, LOCATION_GROUP, NOBLE_GROUP, NUMERIC_GROUP, TEXT_GROUP, TRIBE_GROUP -> true;
			default -> false;
		};
	}
}
