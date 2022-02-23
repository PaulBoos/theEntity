package googledocs.legis.parser;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Token {
	private TokenType type;
	private String value;
}
