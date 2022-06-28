// Generated with g9 DBmasker.

package no.bank.anonymisering;

import no.esito.anonymizer.IAnonymization;
import no.esito.anonymizer.IColumn;
import no.esito.anonymizer.column.DateColumn;
import no.esito.anonymizer.column.TextColumn;
import no.esito.anonymizer.core.AbstractUpdateTask;

/**
 * Anonymize - Mask various fields
 */
public class Update_KundeData extends AbstractUpdateTask {

    @Override
    public String getName() {
        return "Update_KundeData";
    }

    @Override
    public String getDescription() {
        return "Anonymize - Mask various fields";
    }

    @Override
    public String getTable() {
        return "KundeData";
    }

    @Override
    public String getSchema() {
        return "";
    }

    @Override
    public IColumn[] getAllColumns() {
        return new IColumn[] {
            new TextColumn("Fornavn"),
            new TextColumn("Etternavn"),
            new DateColumn("Fodselsdag"),
            new TextColumn("Fodelsnummer"),
            new TextColumn("Kontonummer"),
            new TextColumn("Telefonnummer"),
        };
    }

    @Override
    public String getWhere() {
        return null;
    }

    @Override
    public IColumn[] getIndexColumns() {
        return new IColumn[] {
        };
    }

    @Override
    public IAnonymization[] getAnonymizations() {
        return new IAnonymization[] {
            new KundeData_Fornavn(),new KundeData_Etternavn(),new KundeData_Fodselsdag(),new KundeData_Fodelsnummer(),new KundeData_Kontonummer(),new KundeData_Telefonnummer(),
        };
    }
}
