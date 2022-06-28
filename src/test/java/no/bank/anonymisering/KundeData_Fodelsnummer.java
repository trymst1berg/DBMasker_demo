// Generated with g9 DBmasker.

package no.bank.anonymisering;

import no.bank.transformations.Person_NO;
import no.esito.anonymizer.IColumn;
import no.esito.anonymizer.IInput;
import no.esito.anonymizer.ITransformation;
import no.esito.anonymizer.column.DateColumn;
import no.esito.anonymizer.column.TextColumn;
import no.esito.anonymizer.conversions.String2Date;
import no.esito.anonymizer.core.AbstractMasking;
import no.esito.anonymizer.mask.MaskColumn;
import no.esito.anonymizer.mask.MaskRandomInteger;

/**
 * Create random norwegian phone number
 */
public class KundeData_Fodelsnummer extends AbstractMasking {

    @Override
    public IColumn getColumn() {
        return new TextColumn("Fodelsnummer");
    }

    @Override
    public IInput[] getInputs() {
        return new IInput[] {
            new MaskColumn(new DateColumn("Fodselsdag"),new String2Date()),
            new MaskRandomInteger("10000","99999"),
        };
    }

    @Override
    public ITransformation getTransformation() {
        return new Person_NO();
    }

    @Override
    public String getFormat() {
        return "%1$td%1$tm%1$ty%2$s";
    }

    @Override
    public boolean isUnique() {
        return false;
    }
}
