package no.esito.anonymizer.transformations;

import no.esito.anonymizer.ITransformation;
import no.esito.anonymizer.mask.AbstractRandom;

/**
 * Simple example to replace digits in a string
 */
public class ReplaceDigits extends AbstractRandom implements ITransformation {

	public static final String LABEL = "ReplaceDigits - randomize the digits in a string";

	@Override
	public String transform(String input) {
		if(input == null || input.isEmpty())
			return input;
		char[] chars = input.toCharArray();
		StringBuilder sb = new StringBuilder();
		for (char c : chars) {
			if(Character.isDigit(c))
				sb.append(getRandom().nextInt(10));
			else
				sb.append(c);
		}
		return sb.toString();
	}

}
