// Generated with g9 DBmasker.

package example.anonymizer.updateadresstasks;

import no.esito.anonymizer.IColumn;
import no.esito.anonymizer.column.DateTimeColumn;
import no.esito.anonymizer.core.AbstractPermutation;

public class Address_created extends AbstractPermutation {

    @Override
    public IColumn getColumn() {
        return new DateTimeColumn("created");
    }
}
