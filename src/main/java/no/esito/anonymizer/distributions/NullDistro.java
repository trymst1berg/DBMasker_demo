package no.esito.anonymizer.distributions;

import java.util.List;
import java.util.Random;

import no.esito.anonymizer.IDistribution;
import no.esito.anonymizer.core.CreateParent;
import no.esito.anonymizer.mask.AbstractRandom;

/**
 * Sample distribution to randomly assign foreign keys with chance of setting null
 */
public class NullDistro extends AbstractRandom implements IDistribution {
	public static final String LABEL = "DistroWithNullValues";
	public static final String MAIN_LABEL= "Allow orphans";
	public static final String PARENT_LABEL= "% Chance of null value";
	private boolean allowOrphans;
	private Random random;
	
	public NullDistro(boolean allowOrphans) { // MAIN_LABEL parameter
		this.allowOrphans=allowOrphans;
	}

	@Override
	public int calculateNewRows(CreateParent[] parents, int numExistRows, List<String[]> existing) {
		return 0;
	}

	@Override
	public void distribute(List<String> columns, CreateParent[] parents, List<String[]> rows) {
		for (String[] row : rows) {
			boolean orphan = true;
			do {
				for (CreateParent parent : parents) {
					int chance=Integer.parseInt(parent.params); // PARENT_LABEL ( % null)  
					if(random.nextInt(100) >= chance) { // assign if random gives a roll higher 
						randomAssignColumn(columns, row, parent); // helper method in AbstractRandom
						orphan=false;
					}
				}
			} while (orphan && !allowOrphans);  // Continue running until not orphan
		}
	}

    @Override
    public void setRandom(Random random) {
        this.random = random;
    }

    @Override
    public Random getRandom() {
        return random;
    }
	
}