package no.esito.anonymizer.distributions;

import java.util.List;

import no.esito.anonymizer.IDistribution;
import no.esito.anonymizer.core.CreateParent;

/**
 * Simple distribution where it ensures a minimum occurances of each parent
 * when creating new records
 */
public class MinPerParent implements IDistribution {
	public static final String LABEL = "MinPerParent - Ensures a minimum number of occurrences of each parent value";
	public static final String PARENT_LABEL= "Minimum #rows / parent";
	int[] min;

	@Override
	public int calculateNewRows(CreateParent[] parents, int numExistRows, List<String[]> existing) {
		min=new int[parents.length];
		for (int i = 0; i < parents.length; i++) {
			min[i]=Integer.valueOf(parents[i].params);
		}
		int x1 = 0;
		for (int i = 0; i < parents.length; i++) {
			CreateParent ct = parents[i];
			int dmin = 0;
			for (int j : ct.count) {
				dmin += Math.max(min[i] - j, 0);
			}
			x1 = Math.max(x1,dmin );
		}
		return x1;
	}

	@Override
	public void distribute(List<String> columns, CreateParent[] parents, List<String[]> rows) {
		for (int i = 0; i < parents.length; i++) {
			CreateParent parent = parents[i];
			int irow = 0;
			int[] a = parent.count;
			for (int icol = 0; icol < a.length; icol++) {
				for (int j = a[icol]; j < min[i]; j++) {
					if (irow >= rows.size())
						break;
					IDistribution.assignRow(parent, columns, rows.get(irow++), icol); 
				}
			}
			int icol = 0;
			int size = parent.parentRows.size();
			while (size > 0 && irow < rows.size()) {
				IDistribution.assignRow(parent, columns, rows.get(irow++), icol++);
				if (icol >= size)
					icol = 0;
			}
		}
	}
}