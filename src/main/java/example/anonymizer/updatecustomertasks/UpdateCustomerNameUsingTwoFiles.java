// Generated with g9 DBmasker.

package example.anonymizer.updatecustomertasks;

import no.esito.anonymizer.IAnonymization;
import no.esito.anonymizer.IColumn;
import no.esito.anonymizer.IContext;
import no.esito.anonymizer.column.DateTimeColumn;
import no.esito.anonymizer.column.NumberColumn;
import no.esito.anonymizer.column.TextColumn;
import no.esito.anonymizer.core.AbstractUpdateTask;

public class UpdateCustomerNameUsingTwoFiles extends AbstractUpdateTask {

    @Override
    public String getName() {
        return "UpdateCustomerNameUsingTwoFiles";
    }

    @Override
    public String getTable() {
        return "Customer";
    }

    @Override
    public String getSchema() {
        return "";
    }

    @Override
    public void run(IContext context) throws Throwable {
        sql(context,"delete from Customer where name is null");
        super.run(context);
    }

    @Override
    public IColumn[] getAllColumns() {
        return new IColumn[] {
            new NumberColumn("ID"),
            new TextColumn("email"),
            new TextColumn("name"),
            new DateTimeColumn("created"),
            new NumberColumn("addressNo"),
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
            new NumberColumn("ID"),
        };
    }

    @Override
    public IAnonymization[] getAnonymizations() {
        return new IAnonymization[] {
            new UpdateNamesUsingFiles(),
        };
    }
}
