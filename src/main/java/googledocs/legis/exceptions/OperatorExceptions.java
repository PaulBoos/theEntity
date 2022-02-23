package googledocs.legis.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OperatorExceptions implements ExceptionType {
	
	INVALID_TYPE_FOR_DIVIDE                    ("[Runtime] The '/' operator requires two arguments of type NUMERIC."),
	INVALID_TYPE_FOR_MULTIPLY                  ("[Runtime] The '*' operator requires\n  two arguments of type NUMERIC or\n  one NUMERIC and a BOOLEAN."),
	INVALID_TYPE_FOR_ADDITION_CONCAT           ("[Runtime] The '+' operator requires\n  two arguments of types BOOLEAN or NUMERIC\n  or two arguments of the SAME TYPE."),
	INVALID_TYPE_FOR_SUBTRACTION               ("[Runtime] The '-' operator requires\n  two arguments of type NUMERIC\n  or one of type NUMERIC and one of type BOOLEAN."),
	INVALID_TYPE_FOR_LESS                      ("[Runtime] The '<' operator requires two arguments of type NUMERIC."),
	INVALID_TYPE_FOR_GREATER                   ("[Runtime] The '>' operator requires two arguments of type NUMERIC."),
	INVALID_TYPE_FOR_EQUALS                    ("[Runtime] The '=' operator requires two arguments of the SAME TYPE."),
	INVALID_TYPE_FOR_AND                       ("[Runtime] The '&' operator requires two arguments of type BOOLEAN."),
	INVALID_TYPE_FOR_XOR                       ("[Runtime] The '#' operator requires two arguments of type BOOLEAN."),
	INVALID_TYPE_FOR_OR                        ("[Runtime] The '|' operator requires two arguments of type BOOLEAN."),
	INVALID_TYPE_FOR_AT                        ("[Runtime] The '@' operator requires one argument of ANY TYPE and one argument-group of the SAME TYPE."),
	
	META_EVAL_INVALID_TYPE_FOR_DIVIDE          ("[Pre-Evaluation] The '/' operator requires two arguments of type NUMERIC."),
	META_EVAL_INVALID_TYPE_FOR_MULTIPLY        ("[Pre-Evaluation] The '*' operator requires\n  two arguments of type NUMERIC or\n  one NUMERIC and a BOOLEAN."),
	META_EVAL_INVALID_TYPE_FOR_ADDITION_CONCAT ("[Pre-Evaluation] The '+' operator requires\n  two arguments of types BOOLEAN or NUMERIC\n  or two arguments of the SAME TYPE."),
	META_EVAL_INVALID_TYPE_FOR_SUBTRACTION     ("[Pre-Evaluation] The '-' operator requires\n  two arguments of type NUMERIC\n  or one of type NUMERIC and one of type BOOLEAN."),
	META_EVAL_INVALID_TYPE_FOR_LESS            ("[Pre-Evaluation] The '<' operator requires two arguments of type NUMERIC."),
	META_EVAL_INVALID_TYPE_FOR_GREATER         ("[Pre-Evaluation] The '>' operator requires two arguments of type NUMERIC."),
	META_EVAL_INVALID_TYPE_FOR_EQUALS          ("[Pre-Evaluation] The '=' operator requires two arguments of the SAME TYPE."),
	META_EVAL_INVALID_TYPE_FOR_AND             ("[Pre-Evaluation] The '&' operator requires two arguments of type BOOLEAN."),
	META_EVAL_INVALID_TYPE_FOR_XOR             ("[Pre-Evaluation] The '#' operator requires two arguments of type BOOLEAN."),
	META_EVAL_INVALID_TYPE_FOR_OR              ("[Pre-Evaluation] The '|' operator requires two arguments of type BOOLEAN."),
	META_EVAL_INVALID_TYPE_FOR_AT              ("[Pre-Evaluation] The '@' operator requires one argument of ANY TYPE and one argument-group of the SAME TYPE.");
	
	private String message;
}
