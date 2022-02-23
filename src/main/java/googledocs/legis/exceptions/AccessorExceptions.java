package googledocs.legis.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AccessorExceptions implements ExceptionType {
	INVALID_FIELD("{0} does not contain the field \"{1}\"!"),
	INVALID_TYPE("{0} does not contain any fields!"),
	META_EVAL_INVALID_FIELD("{0} does not contain the field \"{1}\"!"),
	META_EVAL_INVALID_TYPE("{0} does not contain any fields!");
	
	private String message;
}