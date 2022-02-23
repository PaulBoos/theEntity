package googledocs.legis.exceptions;

import lombok.Getter;

import java.text.MessageFormat;

@Getter
public class ASTEvalException extends RuntimeException {
	
	private final ExceptionType exceptionType;
	private final Object[] objects;
	private final String source;
	
	public ASTEvalException(ExceptionType exceptionType, String source, Object... objects) {
		super(MessageFormat.format(exceptionType.getMessage(), objects) + " (" + source + ")");
		this.exceptionType = exceptionType;
		this.source = source;
		this.objects = objects;
	}
}
