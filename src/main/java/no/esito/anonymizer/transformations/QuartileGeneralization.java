package no.esito.anonymizer.transformations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import no.esito.anonymizer.IPreScan;
import no.esito.anonymizer.ITransformation;

/**
 * Generalization example that demonstrates using current value set in the calculation.
 * It divided current values into 4 equal size buckets where the value represents the average.
 */
public class QuartileGeneralization implements ITransformation, IPreScan {

	public static final String LABEL = "QuartileGeneralization - Create 4 groups and preserve the average";
	
	@Override
	public String transform(String input) {
		if(input == null || input.isEmpty())
			return input;
		int in=Integer.parseInt(input);
		for (int i = 0; i < 4; i++) {
			if(in<=max[i])
				return String.valueOf(tot[i]/each);
		}
		return String.valueOf(tot[3]/each);
	}
	
	int[] max=new int[]{0,0,0,0};
	int[] tot=new int[]{0,0,0,0};
	double each;

	@Override
	public void scan(int col, List<String[]> rows) {
		List<Integer> list=new ArrayList<>();
		for (String[] row : rows) {
			list.add(Integer.valueOf(row[col]));
		}
		Collections.sort(list);
		each = list.size()/4.0;
		for (int i = 0; i < list.size(); i++) {
			int bucket=(int)(i/each);
			Integer x = list.get(i);
			max[bucket]=x;
			tot[bucket]+=x;
		}
	}

}
