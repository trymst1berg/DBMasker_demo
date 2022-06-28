// Generated with g9 DBmasker.

package example.anonymizer.updateadresstasks;

import no.esito.anonymizer.IAnonymization;
import no.esito.anonymizer.IColumn;
import no.esito.anonymizer.column.DateTimeColumn;
import no.esito.anonymizer.column.NumberColumn;
import no.esito.anonymizer.column.TextColumn;
import no.esito.anonymizer.core.AbstractUpdateTask;

public class UpdateAddressFields extends AbstractUpdateTask {

    @Override
    public String getName() {
        return "UpdateAddressFields";
    }

    @Override
    public String getTable() {
        return "Address";
    }

    @Override
    public String getSchema() {
        return "";
    }

    @Override
    public IColumn[] getAllColumns() {
        return new IColumn[] {
            new NumberColumn("addressNo"),
            new TextColumn("homeAddress"),
            new TextColumn("postalCode"),
            new DateTimeColumn("created"),
        };
    }

    @Override
    public String getWhere() {
        return null;
    }

    @Override
    public String getSelectionKey() {
        return null;
    }

    @Override
    public IColumn[] getIndexColumns() {
        return new IColumn[] {
            new NumberColumn("addressNo"),
        };
    }

    @Override
    public IAnonymization[] getAnonymizations() {
        return new IAnonymization[] {
            new UpdateAddressUsingFile(),new UpdateToRandomPostalCodeAsString(),new Address_created(),
        };
    }
}
