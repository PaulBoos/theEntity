package googledocs.legis.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PrefixOperatorExceptions implements ExceptionType {
	
	INVALID_TYPE_FOR_NOT("The '!' prefix operator requires an argument of type BOOLEAN"),
	INVALID_TYPE_FOR_NEGATE("The '-' prefix operator requires an argument of type NUMERIC"),
	META_EVAL_INVALID_TYPE_FOR_NOT("[Pre-Evaluation] The '!' prefix operator requires an argument of type BOOLEAN"),
	META_EVAL_INVALID_TYPE_FOR_NEGATE("[Pre-Evaluation] The '-' prefix operator requires an argument of type NUMERIC");
	
	private String message;
}
