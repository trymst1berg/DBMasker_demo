// Generated with g9 DBmasker.

package no.bank.anonymisering;

import no.esito.anonymizer.IColumn;
import no.esito.anonymizer.IInput;
import no.esito.anonymizer.ITransformation;
import no.esito.anonymizer.column.TextColumn;
import no.esito.anonymizer.core.AbstractMasking;
import no.esito.anonymizer.mask.MaskFileRandom;

/**
 * Create random name from list of firstnames and lastnames
 */
public class KundeData_Fornavn extends AbstractMasking {

    @Override
    public IColumn getColumn() {
        return new TextColumn("Fornavn");
    }

    @Override
    public IInput[] getInputs() {
        return new IInput[] {
            new MaskFileRandom("firstname.txt",null),
        };
    }

    @Override
    public ITransformation getTransformation() {
        return null;
    }

    @Override
    public String getFormat() {
        return "%s";
    }

    @Override
    public boolean isUnique() {
        return false;
    }
}
