package googledocs.legis.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TextParseExceptions implements ExceptionType {
	
	INVALID_LEGAL_OBJECT("Unknown legal object"),
	LEGAL_OBJECT_NOT_FOUND("Unknown id."),
	INVALID_VARIABLE("Unknown variable used"),
	
	META_EVAL_ILLEGAL_GROUP("Group evaluation would reach an illegal state. Check your Groups.")
	;
	
	private String message;
	
}
